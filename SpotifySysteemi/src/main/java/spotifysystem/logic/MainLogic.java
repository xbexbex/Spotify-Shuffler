package spotifysystem.logic;

import spotifysystem.gui.MainGUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 *
 * @author Owner
 */
public class MainLogic {

    private static MainGUI gui;
    private static String pw = "";
    private static String un;

    public static void go() {
        gui = new MainGUI();
        gui.start();
    }

    public static void logIn(String username, char[] password) {
        un = username;
        for (int i = 0; i < password.length; i++) {
            pw += password[i];
        }
        logIn();
    }

    public static void logIn() {
        String msg = WebSite.logIn(un, pw);
        if (!(WebSite.getCode().equals(""))) {
            int status = AuthHandler.getTokens(WebSite.getCode());
        } else {
            print(msg);
        }
    }

    public static void print(String m) {
        gui.printMessage(m);
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
}
