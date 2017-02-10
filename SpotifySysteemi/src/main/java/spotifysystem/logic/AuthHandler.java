package spotifysystem.logic;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.UserPlaylistsRequest;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.SimplePlaylist;

/**
 * Handles all authentication related things with spotify web api
 * @author xbexbex
 */
public class AuthHandler {

    static String clientId = "cb4d60eaad584defba20088354bf6bbc";
    static String clientSecret = "895f9958fdd04170a1095adf5ad83ef3";
    static String code;
    static String redirectUri = "http://localhost:8888/callback";
    static String accessToken;
    static String refreshToken;
    static int status;
    static Api api;
    private static AuthTimer timer = null;

    /**
     *
     * @param s authorization code
     * @return error code, 0 if successfull
     */
    public static int getTokens(String s) {
        status = 0;
        code = s;
        api = returnApi();
        final SettableFuture<AuthorizationCodeCredentials> authorizationCodeCredentialsFuture = api.authorizationCodeGrant(code).build().getAsync();
        Futures.addCallback(authorizationCodeCredentialsFuture, new FutureCallback<AuthorizationCodeCredentials>() {
            @Override
            public void onSuccess(AuthorizationCodeCredentials authorizationCodeCredentials) {
                accessToken = authorizationCodeCredentials.getAccessToken();
                refreshToken = authorizationCodeCredentials.getRefreshToken();
                status = authorizationCodeCredentials.getExpiresIn();
                MainLogic.print("Successfully retrieved an access token! " + accessToken);
                MainLogic.print("The access token expires in " + status + " seconds");
                MainLogic.print("Luckily, I can refresh it using this refresh token! " + refreshToken);
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
     * Updates the spotify web api accesstoken by using the old access and refresh tokens
     */
    public static void refresh() {
        api = Api.builder().accessToken(accessToken).build();
        final UserPlaylistsRequest request = api.getPlaylistsForUser(MainLogic.getUsername()).build();
        try {
            final Page<SimplePlaylist> playlistsPage = request.get();
            for (SimplePlaylist playlist : playlistsPage.getItems()) {
                MainLogic.print(playlist.getName());
            }
        } catch (Exception e) {
            MainLogic.print(e.getMessage());
            int status = getTokens(code);
//            if (status < 2) {
//                MainLogic.logIn();
//            } else {
//                timer.restart(status);
//            }
        }
    }

    /**
     * Builds and api with the static class parameters
     * @return built api
     */
    public static Api returnApi() {
        return Api.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectURI(redirectUri)
                .build();
    }

    public static void setAToken(String t) {
        accessToken = t;
    }

    public static void setRToken(String t) {
        refreshToken = t;
    }

}
