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
        pw = "";
        if (msg.equals("")) {
            MainGUI.usrPrint(un, false);
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
    
    public static void logOut() {
        MainGUI.playListTab(false);
        print("Logged out");
        MainGUI.usrPrint("Not logged in", true);
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
     *
     * @param b whether or not new playlists will be created
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
            s.add(d);
        }
        if (s.size() == 1) {
            if (s.get(0) == 0) {
                print("Playlist shuffled");
            } else if (s.get(0) == 2) {
                print("Playlist is empty");
            } else if (s.get(0) == 3) {
                print("Playlist shuffled without local tracks");
            } else if (s.get(0) == 4) {
                print("Shuffling playlists with local tracks is currently not possible");
            } else {
                print("Unable to shuffle playlist");
            }
        } else {
            int x = 0;
            int le = 0;
            int les = 0;
            for (int z : s) {
                if (z == 0) {
                    x++;
                } else if (z == 4) {
                    le++;
                } else if (z == 3) {
                    les++;
                }
            }
            if (x == 0) {
                print("Unable to shuffle playlists");
            } else if (le == 1) {
                print("1 of " + s.size() + " playlists was not shuffled due to local tracks");
            } else if (le > 1) {
                print(le + " of " + s.size() + " playlists were not shuffled due to local tracks");
            } else if (les > 0) {
                print(les + " of " + s.size() + " playlists shuffled without their local tracks");
            } else if (x == s.size()) {
                print("All " + s.size() + " playlists shuffled");
            } else {
                print(x + " of " + s.size() + " playlists shuffled succesfully");
            }
        }
        playlistUpdate(
                false);
    }

    /**
     * Deletes playlists
     *
     * @param i selected indices
     */
    public static void delete(int[] i) {
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

    /**
     *
     * @return
     */
    public static String getUsername() {
        return un;
    }

    /**
     *
     * @return
     */
    public static String getPassword() {
        return pw;
    }

    /**
     *
     * @return
     */
    public static String getLog() {
        return gui.returnLog();
    }

    /**
     *
     * @return
     */
    public static String returnLastLine() {
        return gui.returnLastLine();
    }

    /**
     *
     * @return
     */
    public static ArrayList<Playlist> getPlaylists() {
        playlistUpdate(false);
        return playlists;
    }

    /**
     *
     * @return
     */
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
