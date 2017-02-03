package spotifysystem.logic;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import spotifysystem.gui.MainGUI;

public class WebSite {

    private final static String AUTHPAGE = "https://accounts.spotify.com/en/authorize?client_id=cb4d60eaad584defba20088354bf6bbc&response_type=code&redirect_uri=http:%2F%2Flocalhost:8888%2Fcallback&scope=playlist-modify-public%20playlist-modify-private%20user-read-private%20user-library-modify%20user-library-read&state=nostate";
    private static WebClient webClient;
    private static HtmlPage page = null;
    private static String code;

    public static String logIn(String un, String pw) {
        setUp();
        code = "";
        if (!(openPage())) {
            return "Unable to connect to Spotify";
        }
        int i = 0;
        while (i < 14) {
            int state = checkState();
            MainLogic.print(state + "");
            MainLogic.print(page.asXml());
            if (state == 0) {
                break;
            } else if (state == 1) {
                if (connectToSpotify()) {
                    i += 3;
                } else {
                    return "Unable to connect to Spotify";
                }
            } else if (state == 2) {
                i += 2;
                if (!(logInToSpotify(un, pw))) {
                    return "Unable to log in to Spotify";
                }
                String e = credentialsCheck();
                if (e.equals("l")) {
                    i += 1;
                } else if (!e.equals("s")) {
                    return e;
                }
            } else if (state == 3) {
                String url = authorize();
                if (url.equals("l")) {
                    return "Spotify is not responding. Please try again later.";
                } else {
                    code = url.substring(url.indexOf("=") + 1, url.indexOf("&"));
                    return url;
                }
            } else if (state == 4) {
                openPage();
                i += 3;
            } else {
                break;
            }
        }
        return "Spotify is currently stuck. Please try again later.";
    }

    private static int checkState() {
        try {
            HtmlAnchor a = (HtmlAnchor) page.getFirstByXPath("//a[text()='Log in to Spotify']");
            if (a != null) {
                return 1;
            }
            HtmlButton login = (HtmlButton) page.getFirstByXPath("//button[text()='Log In']");
            if (login != null) {
                return 2;
            }
            HtmlButton b = page.getFirstByXPath("//button[text()='Okay']");
            if (b != null) {
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

    private static boolean openPage() {
        try {
            page = (HtmlPage) webClient.getPage(AUTHPAGE);
            webClient.waitForBackgroundJavaScript(50000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean connectToSpotify() {
        try {
            HtmlAnchor a = (HtmlAnchor) page.getAnchorByText("Log in to Spotify");
            page = a.click();
            webClient.waitForBackgroundJavaScript(50000);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            return true;
        }
    }

    private static boolean logInToSpotify(String un, String pw) {
        try {
            HtmlInput username = page.getHtmlElementById("login-username");
            username.setValueAttribute(un);
            HtmlInput password = page.getHtmlElementById("login-password");
            password.setValueAttribute(pw);
            HtmlButton login = (HtmlButton) page.getFirstByXPath("//button[text()='Log In']");
            page = login.click();
            webClient.waitForBackgroundJavaScript(50000);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static String credentialsCheck() {
        try {
            webClient.waitForBackgroundJavaScript(50000);
            HtmlSpan s = page.getFirstByXPath("//span[text()='Incorrect username or password.']");
            if (s != null) {
                return "Incorrect credentials";
            }
            s = page.getFirstByXPath("//span[text()='Your request failed. Please try again.']");
            if (s != null) {
                return "l";
            }
            return "s";
        } catch (Exception e) {
            return "Unable to complete login";
        }
    }

    private static String authorize() {
        try {
            HtmlButton b = page.getFirstByXPath("//button[text()='Okay']");
            page = b.click();
            webClient.waitForBackgroundJavaScript(50000);
            return page.toString();
        } catch (Exception e) {
            return "l";
        }
    }

    private static void setUp() {
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(true);
        webClient.setCssErrorHandler(new SilentCssErrorHandler());
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setRedirectEnabled(false);
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setPopupBlockerEnabled(true);
        webClient.getOptions().setTimeout(5000);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);
    }

    public static String getCode() {
        return code;
    }

    private static void debugmonster(String un, String pw) {
        try {
            page = (HtmlPage) webClient.getPage(AUTHPAGE);
            webClient.waitForBackgroundJavaScript(50000);
            HtmlAnchor a = (HtmlAnchor) page.getAnchorByText("Log in to Spotify");
            page = a.click();
            webClient.waitForBackgroundJavaScript(50000);
            HtmlInput username = page.getHtmlElementById("login-username");
            username.setValueAttribute(un);
            HtmlInput password = page.getHtmlElementById("login-password");
            password.setValueAttribute(pw);
            HtmlButton login = (HtmlButton) page.getFirstByXPath("//button[text()='Log In']");
            page = login.click();
            webClient.waitForBackgroundJavaScript(50000);
            page = (HtmlPage) webClient.getPage(AUTHPAGE);
            webClient.waitForBackgroundJavaScript(50000);
            HtmlButton b = page.getFirstByXPath("//button[text()='Okay']");
            if (b != null) {
                MainLogic.print(pw);
            }
            page = b.click();
            webClient.waitForBackgroundJavaScript(50000);
            MainLogic.print(page.toString());
        } catch (Exception e) {

        }
    }
}
