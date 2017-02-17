/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotifysystemTest.logic;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import spotifysystem.logic.MainLogic;
import spotifysystem.logic.WebSite;
import static org.junit.Assert.*;
import org.junit.ClassRule;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

/**
 *
 * @author Owner
 */
public class MainLogicTest {

    public MainLogicTest() {

    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        MainLogic.go();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void msgLogTest() {
        MainLogic.print("test1");
        assertTrue(MainLogic.returnLastLine().equals("test1"));
    }

    @Test
    public void logInUsernameTest() {
        MainLogic.logIn("absolutelyfalseusername", new char[]{1, 2, 3});
        assertTrue(MainLogic.getUsername().equals("absolutelyfalseusername"));
    }

    @Test
    public void logInFalseCodeTest() {
        MainLogic.logIn("absolutelyfalseusername", new char[]{1, 2, 3});
        assertTrue(MainLogic.getLog().equals("Incorrect username or password"));
    }

    @Test
    public void logInErrorTest() {
        MainLogic.logIn("absolutelyfalseusername", new char[]{1, 2, 3});
        assertTrue(WebSite.getCode() == "");
    }

    @Test
    public void playlistUpdateWorks() {
        MainLogic.logIn("absolutelyfalseusername", new char[]{1, 2, 3});
        assertTrue(WebSite.getCode() == "");
    }
}
