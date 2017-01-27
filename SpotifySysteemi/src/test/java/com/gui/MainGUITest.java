/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gui;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Owner
 */
public class MainGUITest {

    private MainGUI gui;

    public MainGUITest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        gui = new MainGUI();
    }

    @After
    public void tearDown() {
    }

//    @Test
//    public void runWorks() {
//        gui.run();
//        assertTrue(gui.isRunning());
//    }
}
