/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotifysystem.logic;

import java.util.Timer;
import java.util.TimerTask;

public class AuthTimer extends Timer {

    public TimerTask timerTask;

    public void start(int t) {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                AuthHandler.refresh();
            }
        };
        this.schedule(timerTask, toMS(t));
    }

    public void restart(int t) {
        timerTask.cancel();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                AuthHandler.refresh();
            }
        };
        this.schedule(timerTask, toMS(t));
    }
    
    public int toMS(int t) {
        if (t < 0) {
            return 0;
        }
        return t * 1000;
    }
}
