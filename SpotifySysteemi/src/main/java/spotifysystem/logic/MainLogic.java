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
        print("Logging in...");
        String msg = WebSite.logIn(un, pw);
        if (msg.equals("")) {
            int status = ApiFunctionHandler.getTokens(WebSite.getCode());
            if (status > 1) {
                MainGUI.playListTab(true);
                ApiFunctionHandler.refresh();
                playlistUpdate(false);
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
    public static void playlistUpdate(boolean b) {
        playlists = ApiFunctionHandler.getPlaylists();
        String[] names = getPlaylistNames();
        MainGUI.playlistUpdate(names);
        if (b) {
            MainGUI.printMessage("List updated");
        }
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
        ArrayList<Integer> s = new ArrayList();
        for (int j = 0; j < i.length; j++) {
            int d = ApiFunctionHandler.shufflePlaylist(playlists.get(i[j]), b);
//            if (d > 0) {
//                MainLogic.print(d + "");
//                return;
//            }
            s.add(d);
        }
        if (s.size() == 1) {
            if (s.get(0) == 0) {
                print("Playlist shuffled");
            } else if (s.get(0) == 2) {
                print("Playlist is empty");
            } else {
                print("Unable to shuffle playlist");
            }
        } else {
            int x = 0;
            for (int z : s) {
                if (z == 0) {
                    x++;
                }
            }
            if (x == 0) {
                print("Unable to shuffle playlists");
            } else if (x == s.size()) {
                print("All " + s.size() + " playlists shuffled");
            } else {
                print(x + " of " + s.size() + " playlists shuffled succesfully");
            }
        }
        playlistUpdate(false);
    }
    
    public static void delete(int[] i, boolean b) {
        if (i == null) {
            return;
        }
        ArrayList<Integer> s = new ArrayList();
        for (int j = 0; j < i.length; j++) {
            s.add(ApiFunctionHandler.removePlaylist(playlists.get(i[j]).getId()));
        }
        if (s.size() == 1) {
            if (s.get(0) == 0) {
                print("Playlist deleted");
            } else {
                print("Unable to delete playlist");
            }
        } else {
            int x = 0;
            for (int z : s) {
                if (z == 0) {
                    x++;
                }
            }
            if (x == 0) {
                print("Unable to delete playlists");
            } else if (x == s.size()) {
                print("All " + s.size() + " playlists deleted");
            } else {
                print(x + " of " + s.size() + " playlists deleted succesfully");
            }
        }
        playlistUpdate(false);
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
        playlistUpdate(false);
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
