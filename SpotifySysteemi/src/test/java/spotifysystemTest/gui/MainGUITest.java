
package spotifysystemTest.gui;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import spotifysystem.gui.MainGUI;
import static org.junit.Assert.*;
import org.junit.ClassRule;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import spotifysystem.logic.MainLogic;


public class MainGUITest {

    private static MainGUI gui;

    public MainGUITest() {
    }
    

    @BeforeClass
    public static void setUpClass() {
        gui = new MainGUI();
        gui.start();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void logPrints() {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {

        }
        gui.printMessage("test");
        assertTrue(gui.returnLog().equals("test"));
    }
}
