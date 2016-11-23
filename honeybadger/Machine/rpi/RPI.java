package Machine.rpi;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Contains definitions of each pin used on the RaspberryPI
 */
public interface RPI {
    Pin DRIVE_FRONT_LEFT = RaspiPin.GPIO_00;
    Pin DRIVE_FRONT_RIGHT = RaspiPin.GPIO_02;
    Pin DRIVE_BACK_LEFT = RaspiPin.GPIO_03;
    Pin DRIVE_BACK_RIGHT = RaspiPin.GPIO_22;
    Pin CONVEYOR_A = RaspiPin.GPIO_23;
    Pin CONVEYOR_B = RaspiPin.GPIO_24;
    Pin VACUUM_ROLLER = RaspiPin.GPIO_21;
}
