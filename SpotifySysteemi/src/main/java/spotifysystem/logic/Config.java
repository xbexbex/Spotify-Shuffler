package spotifysystem.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Handles saving and loading configuration settings
 * @author xbexbex
 */
public class Config {

    static Properties p;
    static boolean changed = false;

    /**
     * Checks if properties file exists and is modifiable, calls the appropriate methods to create the file
     * @see reset()
     * @see save()
     */
    public void init() {
        p = new Properties();
        InputStream s = null;
        try {
            File f = new File("sys.properties");
            s = new FileInputStream(f);
        } catch (Exception e) {
            s = null;
        }
        try {
            if (s == null) {
                s = getClass().getResourceAsStream("sys.properties");
            }
            p.load(s);
        } catch (Exception e) {
            if (changed = false) {
                reset();
            }
            save();
        }
    }

    /**
     * Changes the value of key in parameters
     * @param k key
     * @param v value
     */
    public void change(String k, String v) {
        InputStream in = getClass().getResourceAsStream("sys.properties");
        try {
            p.load(in);
        } catch (Exception e) {
            MainLogic.print(e.getMessage());
        }
    }

    /**
     * Resets the values to default
     */
    public void reset() {
        p.setProperty("asdf", "1");
        p.setProperty("fasdf", "2");
        save();
    }

    /**
     * Prints a list of properties
     */
    public void printProperties() {
        MainLogic.print(p.getProperty("asdf"));
    }

    /**
     * Saves all settings to the configuration file
     */
    public void save() {
        try {
            File f = new File("sys.properties");
            OutputStream o = new FileOutputStream(f);
            p.store(o, "stuff");
            o.close();
            changed = false;
        } catch (Exception e) {
            MainLogic.print("Unable to save settings. Make sure that application has permissions.");
        }
    }
}
