package Machine.rpi.hw;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Contains definitions of each pin used on the RaspberryPI
 */
public class RPI {
    public static Pin DRIVE_FRONT_LEFT = RaspiPin.GPIO_00;
    public static Pin DRIVE_FRONT_RIGHT = RaspiPin.GPIO_02;
    public static Pin DRIVE_BACK_LEFT = RaspiPin.GPIO_03;
    public static Pin DRIVE_BACK_RIGHT = RaspiPin.GPIO_22;
    public static Pin CONVEYOR_A = RaspiPin.GPIO_23;
    public static Pin CONVEYOR_B = RaspiPin.GPIO_24;
    public static Pin VACUUM_ROLLER = RaspiPin.GPIO_21;
}
