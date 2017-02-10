package spotifysystem.logic;

import spotifysystem.gui.MainGUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * Handles general logic related tasks
 * @author xbexbex
 */
public class MainLogic {

    private static MainGUI gui;
    private static String pw = "";
    private static String un;

    /**
     * Initializes the program by creating a new GUI
     */
    public static void go() {
        gui = new MainGUI();
        gui.start();
    }

    /**
     * Logs in to spotify with username and password
     * @param username
     * @param password
     * @see WebSite.logIn()
     */
    public static void logIn(String username, char[] password) {
        un = username;
        for (int i = 0; i < password.length; i++) {
            pw += password[i];
        }
        logIn();
    }

    /**
     * Logs in to spotify with previously saved username and password
     */
    public static void logIn() {
        String msg = WebSite.logIn(un, pw);
        if (msg.equals("")) {
            print(WebSite.getCode());
            int status = AuthHandler.getTokens(WebSite.getCode());
        } else {
            print(msg);
        }
    }

    /**
     * Tells GUI to print a message
     * @param m message
     */
    public static void print(String m) {
        gui.printMessage(m);
    }

    /**
     * Tells Config to load settings from a configuration file
     */
    public static void loadConfig() {
        Config conf = new Config();
        conf.init();
        conf.printProperties();
    }

    public static String getUsername() {
        return un;
    }

    public static String getPassword() {
        return pw;
    }

    public static String getLog() {
        return gui.returnLog();
    }

    public static String returnLastLine() {
        return gui.returnLastLine();
    }

    public static void getPlaylists() {
        AuthHandler.refresh();
    }

    public static void killGUI() {
        gui.dispose();
    }

    public static void exit() {
        System.exit(0);
    }
}
