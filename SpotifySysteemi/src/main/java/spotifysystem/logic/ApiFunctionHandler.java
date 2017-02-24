package spotifysystem.logic;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.AddTrackToPlaylistRequest;
import com.wrapper.spotify.methods.PlaylistTracksRequest;
import com.wrapper.spotify.methods.ReplaceTracksInPlaylistRequest;
import com.wrapper.spotify.methods.UserPlaylistsRequest;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.RefreshAccessTokenCredentials;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.PlaylistTrackPosition;
import com.wrapper.spotify.models.SimplePlaylist;
import com.wrapper.spotify.models.SnapshotResult;
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
    private static AuthTimer timer = null;

    /**
     * Gets authorization tokens from spotify using a code
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
                MainLogic.print(throwable.getMessage());
            }

        });
        return status;
    }

    /**
     * Updates the spotify web api accesstoken by using the old access- and refreshtokens
     * refresh tokens
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
     * @param p playlist to be shuffled
     * @param b boolean, whether or not a new playlist is to be created
     * @return error code, 0 if successfull
     */
    public static int shufflePlaylist(Playlist p, boolean b) {
        api = returnApi(3);
        pass = "";
        status = 0;
        try {
            final PlaylistTracksRequest request = api.getPlaylistTracks(MainLogic.getUsername(), p.getId()).build();
            final Page<PlaylistTrack> tracks = request.get();
            List<PlaylistTrack> plt = tracks.getItems();
            List<String> pltx = randomize(plt);
            if (b) {
                pass = p.getId();
                List<PlaylistTrackPosition> pltr = new ArrayList();
                final ReplaceTracksInPlaylistRequest rerequest = api.replaceTracksInPlaylist(MainLogic.getUsername(), pass, pltx).build();
                try {
                    rerequest.get();
                } catch (Exception t) {
                    MainLogic.print(t.toString());
                }
            } else {
                String n = getSuitableName(p.getName());
                MainLogic.print(n);
                createPlaylist(n);
                ArrayList<Playlist> playlists = MainLogic.getPlaylists();
                if (pass.equals("")) {
                    return 1;
                }
            }
            final AddTrackToPlaylistRequest arequest = api.addTracksToPlaylist(MainLogic.getUsername(), pass, pltx).position(0).build();
            try {
                arequest.get();
            } catch (Exception t) {
                MainLogic.print(t.toString());
            }
        } catch (Exception e) {
            return 0;
        }
        return status;
    }

    /**
     * Creates a new playlist for the user
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
     * Finds a suitable non-duplicate name for the new playlist by checking the list of playlists
     * @param n String original playlist's name
     * @return suitable name
     */
    private static String getSuitableName(String n) {
        String[] nwords = n.split(" ");
        String[] names = MainLogic.getPlaylistNames();
        ArrayList<String> matching = new ArrayList();
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
        for (int i = 0; i < plt.length; i++) {
            pltx.add(plt[i].getTrack().getUri());
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
     * Manually set access token
     * @param t
     */
    public static void setAToken(String t) {
        accessToken = t;
    }

    /**
     * Manually set refresh token
     * @param t
     */
    public static void setRToken(String t) {
        refreshToken = t;
    }

}
