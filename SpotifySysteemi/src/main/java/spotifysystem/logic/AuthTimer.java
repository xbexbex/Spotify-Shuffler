
package spotifysystem.logic;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer running for AuthHandler
 * @author xbexbex
 */
public class AuthTimer extends Timer {

    public TimerTask timerTask;

    /**
     * Creates a timer which calls AuthHandler's refresh method when the time runs out.
     * @param t time in seconds
     * @see AuthHandler.refresh()
     */
    public void start(int t) {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                AuthHandler.refresh();
            }
        };
        this.schedule(timerTask, toMS(t));
    }

    /**
     * Restarts the timer with new time.
     * @param t time in seconds
     * @see start()
     * @see AuthHandler.refresh()
     */
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
    
    /**
     * Seconds to milliseconds
     * @param t time in seconds
     * @return time in milliseconds
     */
    public int toMS(int t) {
        if (t < 0) {
            return 0;
        }
        return t * 1000;
    }
}
