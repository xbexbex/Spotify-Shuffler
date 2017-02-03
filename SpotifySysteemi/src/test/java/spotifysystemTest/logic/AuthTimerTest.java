/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotifysystemTest.logic;

import spotifysystem.logic.AuthTimer;
import java.util.TimerTask;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import spotifysystem.logic.AuthTimer;
import spotifysystem.logic.AuthTimer;
import static org.junit.Assert.*;

/**
 *
 * @author Owner
 */
public class AuthTimerTest {

    private static boolean status;
    private AuthTimer timer;

    public AuthTimerTest() {
    }

    public static void setSuccess() {
        status = true;
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        status = false;
        timer = new AuthTimer() {
            @Override
            public void start(int t) {
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        AuthTimerTest.setSuccess();
                    }
                };
                this.schedule(timerTask, t);
            }

            @Override
            public void restart(int t) {
                timerTask.cancel();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        AuthTimerTest.setSuccess();
                    }
                };
                this.schedule(timerTask, t);
            }
        };
    }

    @After
    public void tearDown() {
    }

    @Test
    public void checkIfConversionToMillisecondsWorks() {
        int toMS = timer.toMS(1);
        assertTrue(toMS == 1000);
    }
    
    @Test
    public void checkIfConversionToMillisecondsWorksWithZero() {
        int toMS = timer.toMS(0);
        assertTrue(toMS == 0);
    }
    
    @Test
    public void checkIfConversionToMillisecondsWorksWithNegatives() {
        int toMS = timer.toMS(-100);
        assertTrue(toMS == 0);
    }

    @Test
    public void checkIfTimerStartWorks() throws Exception {
        timer.start(1000);
        Thread.sleep(1005);
        if (status == true) {
            status = false;
            assertTrue(true);
        }
        assertFalse(true);
    }
    
    @Test
    public void checkIfTimerRestartWorks() throws Exception {
        timer.start(1000000000);
        Thread.sleep(1000);
        timer.restart(1000);
        Thread.sleep(1005);
        if (status == true) {
            status = false;
            assertTrue(true);
        }
        assertFalse(true);
    }
    
    @Test
    public void checkIfTimerRestartWorks2() throws Exception {
        timer.start(1000);
        timer.restart(100000);
        Thread.sleep(1005);
        if (status == true) {
            status = false;
            assertTrue(false);
        }
        assertFalse(false);
    }
}
