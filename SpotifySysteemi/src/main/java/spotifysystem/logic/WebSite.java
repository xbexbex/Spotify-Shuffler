package spotifysystem.logic;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

/**
 * Handles logging into spotify with a browser
 *
 * @author xbexbex
 */
public class WebSite {

    private final static String AUTHPAGE = "https://accounts.spotify.com/en/authorize?client_id=cb4d60eaad584defba20088354bf6bbc&response_type=code&redirect_uri=http:%2F%2Flocalhost:8888%2Fcallback&scope=playlist-modify-public%20playlist-modify-private%20user-library-modify%20user-library-read%20playlist-read-private%20playlist-read-collaborative&state=nostate";
    private static WebClient webClient;
    private static HtmlPage page = null;
    private static String code;

    /**
     * Main method for logging into spotify
     *
     * @param un
     * @param pw
     * @see checkState()
     * @see setUp()
     * @return error message String, null if successful
     */
    public static String logIn(String un, String pw) {
        code = "";
        try {
            setUp();
            openPage();
            int i = 0;
            while (i < 14) {
                int state = checkState();
                if (state == 1) {
                    connectToSpotify();
                    i += 1;
                } else if (state == 2) {
                    i += 1;
                    logInToSpotify(un, pw);
                } else if (state == 3) {
                    i += 0;
                    authorize();
                } else if (state == 4) {
                    openPage();
                    i += 1;
                } else if (state == 6) {
                    return "Incorrect username or password";
                } else if (state == 7) {
                    openPage();
                    i += 1;
                } else if (state == 5) {
                    break;
                }
            }
            if (code.equals("")) {
                return "Unable to connect to Spotify. Try again later.";
            }
            return "";
        } catch (Exception e) {
            if (code.equals("")) {
                return "Something went wrong. Try again later.";
            } else {
                webClient.close();
                return "";
            }
        }
    }

    /**
     * Checks the current state of the logging in -process and returns the
     * corresponding integer
     */
    private static int checkState() {
        try {
            if (!(code.equals(""))) {
                return 5;
            }
            String url = page.getUrl().toString();
            if (url.contains("callback?code=")) {
                code = url.substring(url.indexOf("?") + 1, url.indexOf("&"));
                return 5;
            }
            HtmlSpan s = page.getFirstByXPath("//span[text()='Incorrect username or password.']");
            if (s != null) {
                return 6;
            }
            s = page.getFirstByXPath("//span[text()='Your request failed. Please try again.']");
            if (s != null) {
                return 7;
            }
            HtmlAnchor a = (HtmlAnchor) page.getFirstByXPath("//a[text()='Log in to Spotify']");
            if (a != null) {
                return 1;
            }
            HtmlButton login = (HtmlButton) page.getFirstByXPath("//button[text()='Log In']");
            if (login != null) {
                return 2;
            }
            login = page.getFirstByXPath("//button[text()='Okay']");
            if (login != null) {
                return 3;
            }
            a = (HtmlAnchor) page.getFirstByXPath("//a[text()='Account Settings']");
            if (a != null) {
                return 4;
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    /**
     * The first step in the login-process, opens the spotify website.
     */
    private static void openPage() throws Exception {
        page = (HtmlPage) webClient.getPage(AUTHPAGE);
        webClient.waitForBackgroundJavaScript(50000);
    }

    /**
     * The second step in the login-process, clicks the log in button
     */
    private static void connectToSpotify() throws Exception {
        HtmlAnchor a = (HtmlAnchor) page.getAnchorByText("Log in to Spotify");
        page = a.click();
        webClient.waitForBackgroundJavaScript(50000);
    }

    /**
     * The third step in the login-process, inputs the user data and logs in
     */
    private static void logInToSpotify(String un, String pw) throws Exception {
        HtmlInput username = page.getHtmlElementById("login-username");
        username.setValueAttribute(un);
        HtmlInput password = page.getHtmlElementById("login-password");
        password.setValueAttribute(pw);
        webClient.getOptions().setRedirectEnabled(true);
        HtmlButton login = (HtmlButton) page.getFirstByXPath("//button[text()='Log In']");
        page = login.click();
        webClient.waitForBackgroundJavaScript(50000);
    }

    /**
     * Clicks the authorize button
     */
    private static void authorize() throws Exception {
        webClient.getOptions().setRedirectEnabled(true);
        HtmlButton b = page.getFirstByXPath("//button[text()='Okay']");
        page = b.click();
        webClient.waitForBackgroundJavaScript(50000);
    }

    /**
     * Sets up the needed settings for the webclient
     */
    private static void setUp() {
        page = null;
        webClient = null;
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setCssEnabled(true);
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setAppletEnabled(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setTimeout(5000);

        new WebConnectionWrapper(webClient) {
            public WebResponse getResponse(WebRequest request) throws IOException {
                WebResponse response = super.getResponse(request);
                try {
                    String s = response.getResponseHeaderValue("Location");
                    if (s.contains("code=")) {
                        webClient.getOptions().setTimeout(100);
                        code = s.substring(s.indexOf("=") + 1, s.indexOf("&"));
                    } else {
                        webClient.getOptions().setTimeout(5000);
                    }
                } catch (Exception e) {

                }
                return response;
            }
        };
    }

    /**
     * Returns the current login code
     *
     * @return
     */
    public static String getCode() {
        return code;
    }
}
