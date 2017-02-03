/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotifysystemTest.gui;

import spotifysystem.gui.MainGUI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import spotifysystem.gui.MainGUI;
import static org.junit.Assert.*;

/**
 *
 * @author Owner
 */
public class MainGUITest {

    private static MainGUI gui;

    public MainGUITest() {
    }

    @BeforeClass
    public static void setUpClass() {
        gui.start();
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        gui = new MainGUI();
        gui.start();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void logPrints() {
        gui.printMessage("test");
        assertTrue(gui.returnLog().equals("test"));
    }
}
