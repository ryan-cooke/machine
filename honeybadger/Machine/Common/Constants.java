package Machine.Common;

import java.util.concurrent.TimeUnit;

/**
 * Container class for querying some Runtime constants to evaluate functionality
 */
public class Constants {
    public enum PLATFORM{
        NO_INFO,
        BADGER_PI,
        MOCK_PI,
        DESKTOP,
        DESKTOP_GUI,
    }

    /**
     * Simple variable to keep track of which platform we're running on.
     * Will become useful for limiting some logging functionality on the RPi
     */
    protected static PLATFORM ACTIVE_PLATFORM = PLATFORM.NO_INFO;

    /**
     * Check to see if debug mode is on.
     */
    public static final boolean DEBUG_MODE_ON = true;

    /**
     * Captures and logs all incoming message.
     * Use for debug only.
     */
    public static final boolean VERBOSE_MESSAGING = false;

    public static final int UPDATE_SPEED = 100; //milliseconds. Can't go lower than 13ms

    public static final TimeUnit UPDATE_TIME_UNIT = TimeUnit.MILLISECONDS;

    public static void setActivePlatform(PLATFORM current){
        if(ACTIVE_PLATFORM==PLATFORM.NO_INFO){
            ACTIVE_PLATFORM=current;
        }
    }

    public static PLATFORM getActivePlatform(){return ACTIVE_PLATFORM;}
}
