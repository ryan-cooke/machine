package Machine.rpi;

import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.Pin;

/**
 * Contains definitions of each pin used on the PCA9685
 */
public interface PCA {
    Pin DRIVE_FRONT_LEFT = PCA9685Pin.PWM_00;
    Pin DRIVE_FRONT_RIGHT = PCA9685Pin.PWM_01;
    Pin DRIVE_BACK_LEFT = PCA9685Pin.PWM_02;
    Pin DRIVE_BACK_RIGHT = PCA9685Pin.PWM_03;
    Pin CONVEYOR_A = PCA9685Pin.PWM_04;
    Pin CONVEYOR_B = PCA9685Pin.PWM_05;
    Pin VACUUM_ROLLER = PCA9685Pin.PWM_06;
    Pin FLYWHEEL_A = PCA9685Pin.PWM_07;
    Pin FLYWHEEL_B =PCA9685Pin.PWM_08;
    Pin CLIMBING_ARM = PCA9685Pin.PWM_09;
    Pin CLIMBING_WRIST = PCA9685Pin.PWM_10;
    Pin SHOOTING_AIM_ADJUST = PCA9685Pin.PWM_11;
}
