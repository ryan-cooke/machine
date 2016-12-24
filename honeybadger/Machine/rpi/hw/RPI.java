package Machine.rpi.hw;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Contains definitions of each pin used on the RaspberryPI
 */
public class RPI {
    // GPIO 19 == Pi4J 24 -> BACK_RIGHT
    // GPIO 13 == Pi4J 23 -> BACK_LEFT
    // GPIO 6 == PI4J 22 -> FRONT_RIGHT
    // GPIO 5 == PI4J 21 -> FRONT_LEFT
    public static Pin DRIVE_FRONT_LEFT = RaspiPin.GPIO_21;
    public static Pin DRIVE_FRONT_RIGHT = RaspiPin.GPIO_22;
    public static Pin DRIVE_BACK_LEFT = RaspiPin.GPIO_23;
    public static Pin DRIVE_BACK_RIGHT = RaspiPin.GPIO_24;

    //TODO: MAP these out
//    public static Pin CONVEYOR_A = RaspiPin.GPIO_23;
//    public static Pin CONVEYOR_B = RaspiPin.GPIO_24;
//    public static Pin VACUUM_ROLLER = RaspiPin.GPIO_21;
}
