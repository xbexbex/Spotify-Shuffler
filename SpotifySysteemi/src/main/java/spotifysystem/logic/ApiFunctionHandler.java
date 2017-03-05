package spotifysystem.logic;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.AddTrackToPlaylistRequest;
import com.wrapper.spotify.methods.PlaylistTracksRequest;
import com.wrapper.spotify.methods.RemoveTrackFromPlaylistRequest;
import com.wrapper.spotify.methods.ReorderPlaylistTracksRequest;
import com.wrapper.spotify.methods.ReplaceTracksInPlaylistRequest;
import com.wrapper.spotify.methods.UserPlaylistsRequest;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.RefreshAccessTokenCredentials;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.PlaylistTrackPosition;
import com.wrapper.spotify.models.SimplePlaylist;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all authentication related things with spotify web api
 *
 * @author xbexbex
 */
public class ApiFunctionHandler {

    static String clientId = "cb4d60eaad584defba20088354bf6bbc";
    static String clientSecret = "895f9958fdd04170a1095adf5ad83ef3";
    static String code;
    static String redirectUri = "http://localhost:8888/callback";
    static String accessToken;
    static String refreshToken;
    static int status;
    static String pass;
    static Api api;
    static int localFiles;
    static boolean lfb;
    private static AuthTimer timer = null;

    /**
     * Gets authorization tokens from spotify using a code
     *
     * @param s authorization code
     * @return error code, 0 if successfull
     */
    public static int getTokens(String s) {
        status = 0;
        code = s;
        api = returnApi(1);
        final SettableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = api.authorizationCodeGrant(code).build().getAsync();
        Futures.addCallback(authorizationCodeCredentialsFuture, new FutureCallback<AuthorizationCodeCredentials>() {
            @Override
            public void onSuccess(AuthorizationCodeCredentials authorizationCodeCredentials) {
                accessToken = authorizationCodeCredentials.getAccessToken();
                refreshToken = authorizationCodeCredentials.getRefreshToken();
                status = authorizationCodeCredentials.getExpiresIn();
                if (timer != null) {
                    timer.restart(status);
                } else {
                    timer = new AuthTimer();
                    timer.start(status);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                status = 1;
                MainLogic.print("Something went wrong");
            }

        });
        return status;
    }

    /**
     * Updates the spotify web api accesstoken by using the old access- and
     * refreshtokens refresh tokens
     */
    public static void refresh() {
        api = returnApi(2);
        api.setRefreshToken(refreshToken);
        final SettableFuture<RefreshAccessTokenCredentials> refreshAccessTokenCredentialsFuture = api.refreshAccessToken().build().getAsync();
        Futures.addCallback(refreshAccessTokenCredentialsFuture, new FutureCallback<RefreshAccessTokenCredentials>() {
            @Override
            public void onSuccess(RefreshAccessTokenCredentials refreshAccessTokenCredentials) {
                accessToken = refreshAccessTokenCredentials.getAccessToken();
                status = refreshAccessTokenCredentials.getExpiresIn();
                MainLogic.print("Logged in succesfully!");
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.toString();
            }

        });
    }

    /**
     * Fetches the user's playlists from spotify
     *
     * @return ArrayList<Playlist>
     */
    public static ArrayList<Playlist> getPlaylists() {
        api = returnApi(3);
        ArrayList<Playlist> lists = new ArrayList<>();
        final UserPlaylistsRequest request = api.getPlaylistsForUser(MainLogic.getUsername()).build();
        try {
            final Page<SimplePlaylist> playlistsPage = request.get();
            for (SimplePlaylist playlist : playlistsPage.getItems()) {
                lists.add(new Playlist(playlist.getName(), playlist.getId()));
            }
        } catch (Exception e) {
            MainLogic.print("Couldn't acquire playlists");
        }
        return lists;
    }

    /**
     * Shuffles the tracks in a playlist
     *
     * @param p playlist to be shuffled
     * @param b boolean, whether or not a new playlist is to be created
     * @return error code, 0 if successfull
     */
    public static int shufflePlaylist(Playlist p, boolean b) {
        localFiles = 0;
        api = returnApi(3);
        pass = "";
        status = 0;
        int error = 0;
        try {
            PlaylistTracksRequest request = api.getPlaylistTracks(MainLogic.getUsername(), p.getId()).build();
            Page<PlaylistTrack> tracks = request.get();
            List<PlaylistTrack> plt = tracks.getItems();
            if (plt.isEmpty()) {
                return 2;
            }
            int max = plt.size();
            plt = getTracks(plt, p.getId());

            List<String> pltx = randomize(plt);
            List<String> clr = new ArrayList();
            if (localFiles > 0 && b) {
                return 4;
//                pass = p.getId();
//                removeTracks(pltx, p.getId());
            } else if (b) {
                pass = p.getId();
                ReplaceTracksInPlaylistRequest rerequest = api.replaceTracksInPlaylist(MainLogic.getUsername(), pass, clr).build();
                try {
                    rerequest.get();
                } catch (Exception t) {
                    return 1;
                }
            } else {
                String n = getSuitableName(p.getName());
                createPlaylist(n);
                if (pass.equals("")) {
                    return 1;
                }
            }
            error = addTracks(pltx);
            if (localFiles > 0 && b) {
                reorder(max, p.getId());
            }
        } catch (Exception e) {
            return 1;
        }
        if (localFiles > 0 && !b) {
            return 3;
        }
        return error;
    }

    /**
     * Creates a new playlist for the user
     *
     * @param String name
     */
    private static void createPlaylist(String name) {
        final SettableFuture<com.wrapper.spotify.models.Playlist> playlistr = api.createPlaylist(MainLogic.getUsername(), name).publicAccess(true).build().getAsync();
        Futures.addCallback(playlistr, new FutureCallback<com.wrapper.spotify.models.Playlist>() {
            @Override
            public void onSuccess(com.wrapper.spotify.models.Playlist playlistr) {
                pass = playlistr.getId();
            }

            @Override
            public void onFailure(Throwable throwable) {
            }

        });
    }

    /**
     * Finds a suitable non-duplicate name for the new playlist by checking the
     * list of playlists
     *
     * @param n String original playlist's name
     * @return suitable name
     */
    private static String getSuitableName(String n) {
        String[] nwords = n.split(" ");
        String[] names = MainLogic.getPlaylistNames();
        String ln = nwords[nwords.length - 1];
        int st = 2;
        n = "";
        if (ln.equals("(Shuffled)")) {
            for (int i = 0; i < nwords.length - 1; i++) {
                n += nwords[i];
                n += " ";
            }
        } else if (nwords.length >= 2 && nwords[nwords.length - 2].equals("(Shuffled") && ln.charAt(ln.length() - 1) == ')') {
            try {
                st = Integer.parseInt(ln.substring(0, ln.length() - 1)) + 1;
                for (int i = 0; i < nwords.length - 2; i++) {
                    n += nwords[i];
                    n += " ";
                }
            } catch (NumberFormatException nfe) {
                for (int i = 0; i < nwords.length; i++) {
                    n += nwords[i];
                    n += " ";
                }
            }
        } else {
            for (int i = 0; i < nwords.length; i++) {
                n += nwords[i];
                n += " ";
            }
        }
        ArrayList<Integer> forbidden = new ArrayList();
        for (int i = 0; i < names.length; i++) {
            nwords = names[i].split(" ");
            if (nwords.length >= 1 && nwords[nwords.length - 1].equals("(Shuffled)")) {
                forbidden.add(-1);
            } else if (nwords.length >= 2 && nwords[nwords.length - 2].equals("(Shuffled") && nwords[nwords.length - 1].charAt(nwords[nwords.length - 1].length() - 1) == ')') {
                try {
                    forbidden.add(Integer.parseInt(nwords[nwords.length - 1].substring(0, nwords[nwords.length - 1].length() - 1)));
                } catch (NumberFormatException nfe) {
                }
            }
        }
        if (!forbidden.contains(-1)) {
            return n + "(Shuffled)";
        }
        for (int f : forbidden) {
            if (forbidden.contains(st)) {
                st++;
            }
        }
        return n + "(Shuffled " + st + ")";

    }

    /**
     * Randomizes the order of a list of tracks and prepares it for further use
     *
     * @param pltz list to be shuffled
     * @return shuffled list
     */
    private static List<String> randomize(List<PlaylistTrack> pltz) {
        PlaylistTrack[] plt = new PlaylistTrack[pltz.size()];
        int j = 0;
        for (PlaylistTrack t : pltz) {
            plt[j] = t;
            j++;
        }
        List<String> pltx = new ArrayList();
        int n = plt.length;
        for (int i = 0; i < n; i++) {
            int r = i + (int) (Math.random() * (n - i));
            PlaylistTrack x = plt[r];
            plt[r] = plt[i];
            plt[i] = x;
        }
        String url = "";
        for (int i = 0; i < plt.length; i++) {
            url = plt[i].getTrack().getUri();
            if (url.contains("spotify:local:")) {
                localFiles++;
            } else {
                pltx.add(plt[i].getTrack().getUri());
            }
        }
        return pltx;
    }

    /**
     * Builds and api with the static class parameters
     *
     * @param type
     * @return built api
     */
    public static Api returnApi(int type) {
        try {
            if (type == 1) {
                return Api.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectURI(redirectUri)
                        .build();
            } else if (type == 2) {
                return Api.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .build();
            } else if (type == 3) {
                return Api.builder().accessToken(accessToken).build();
            }
        } catch (Exception e) {
            MainLogic.print(e.getMessage());
        }
        return null;
    }

    /**
     * Removes a playlist
     * @param plId playlist's id
     * @return
     */
    public static int removePlaylist(String plId) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.spotify.com/v1/users/" + MainLogic.getUsername() + "/playlists/" + plId + "/followers");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Authorization",
                    "Bearer " + accessToken);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.close();

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return 0;
        } catch (Exception e) {
            return 1;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static List<PlaylistTrack> getTracks(List<PlaylistTrack> plt, String id) {
        int of = 100;
        while (true) {
            try {
                PlaylistTracksRequest request = api.getPlaylistTracks(MainLogic.getUsername(), id).offset(of).build();
                Page<PlaylistTrack> tracks = request.get();
                List<PlaylistTrack> pltt = tracks.getItems();
                if (pltt.isEmpty()) {
                    break;
                } else {
                    for (PlaylistTrack plst : pltt) {
                        plt.add(plst);
                    }
                }
                of += 100;
            } catch (Exception t) {
                break;
            }
        }
        return plt;
    }

    private static void removeTracks(List<String> plt, String id) {
        int index = plt.size() - 1;
        List<PlaylistTrackPosition> positions = new ArrayList();
        while (true) {
            try {
                if (index < 0) {
                    break;
                }
                int c = 0;
                for (int i = index; i >= 0; i--) {
                    index--;
                    positions.add(new PlaylistTrackPosition(plt.get(index)));
                    c++;
                    if (c > 99) {
                        break;
                    }
                }
                RemoveTrackFromPlaylistRequest request = api.removeTrackFromPlaylist(MainLogic.getUsername(), id, positions).tracks(positions).build();
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                }
                request.get();
                positions.clear();
            } catch (Exception t) {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                }
                break;
            }
        }
    }

    private static int addTracks(List<String> pltx) {
        List<String> pltr = new ArrayList();
        int error = 0;
        while (true) {
            int k = pltx.size();
            if (k > 100) {
                for (int i = k - 1; i >= k - 100; i--) {
                    pltr.add(pltx.get(i));
                    pltx.remove(i);
                }
                AddTrackToPlaylistRequest arequest = api.addTracksToPlaylist(MainLogic.getUsername(), pass, pltr).position(0).build();
                try {
                    arequest.get();
                } catch (Exception t) {
                    error--;
                }
            } else {
                AddTrackToPlaylistRequest arequest = api.addTracksToPlaylist(MainLogic.getUsername(), pass, pltx).position(0).build();
                try {
                    arequest.get();
                } catch (Exception t) {
                    return error--;
                }
                break;
            }
            pltr.clear();
        }
        return error;
    }

    private static void reorder(int max, String id) {
        for (int i = 0; i < localFiles; i++) {
            int r = i + (int) (Math.random() * (max - i));
            ReorderPlaylistTracksRequest request = api.reorderPlaylistTracks(MainLogic.getUsername(), id).order(i, r).build();
            try {
                request.get();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Manually set access token
     *
     * @param t
     */
    public static void setAToken(String t) {
        accessToken = t;
    }

    /**
     * Manually set refresh token
     *
     * @param t
     */
    public static void setRToken(String t) {
        refreshToken = t;
    }

}
