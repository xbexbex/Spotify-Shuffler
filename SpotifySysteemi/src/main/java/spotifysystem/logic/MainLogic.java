package spotifysystem.logic;

import java.util.ArrayList;
import spotifysystem.gui.MainGUI;

/**
 * Handles general logic related tasks
 *
 * @author xbexbex
 */
public class MainLogic {

    private static MainGUI gui;
    private static String pw = "";
    private static String un;
    private static ArrayList<Playlist> playlists;

    /**
     * Initializes the program by creating a new GUI
     */
    public static void go() {
        gui = new MainGUI();
        gui.start();
    }

    /**
     * Logs in to spotify with username and password
     *
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
            int status = ApiFunctionHandler.getTokens(WebSite.getCode());
            if (status > 1) {
                MainGUI.playListTab(true);
                ApiFunctionHandler.refresh();
                playlistUpdate();
            }
        } else {
            print(msg);
        }
    }

    /**
     * Tells GUI to print a message
     *
     * @param m message
     */
    public static void print(String m) {
        gui.printMessage(m);
    }

    /**
     * Tells Config to load settings from a configuration file
     */
//    public static void loadConfig() {
//        Config conf = new Config();
//        conf.init();
//        conf.printProperties();
//    }
    
    /**
     * Updates the list of playlists and sends it to GUI
     */
    public static void playlistUpdate() {
        playlists = ApiFunctionHandler.getPlaylists();
        String[] names = getPlaylistNames();
        MainGUI.playlistUpdate(names);
    }
    
    /**
     * Shuffles the playlist(s)
     *
     * @param i list of selected playlist indices
     * @param b whether or not old list(s) will be used
     */
    public static void shuffle(int[] i, boolean b) {
        if (i == null) {
            return;
        }
        for (int j = 0; j < i.length; j++) {
            ApiFunctionHandler.shufflePlaylist(playlists.get(i[j]), b);
        }
        playlistUpdate();
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

    public static ArrayList<Playlist> getPlaylists() {
        playlistUpdate();
        return playlists;
    }
    
    public static String[] getPlaylistNames() {
        String[] names = new String[playlists.size()];
        int i = 0;
        for (Playlist p : playlists) {
            names[i] = p.getName();
            i++;
        }
        return names;
    }
}
