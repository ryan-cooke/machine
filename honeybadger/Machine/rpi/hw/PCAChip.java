package Machine.rpi.hw;

import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.Pin;

/**
 * Contains definitions of each pin used on the PCA9685
 */
public class PCAChip {
    public static Pin DRIVE_FRONT_LEFT = PCA9685Pin.PWM_00;
    public static Pin DRIVE_FRONT_RIGHT = PCA9685Pin.PWM_01;
    public static Pin DRIVE_BACK_LEFT = PCA9685Pin.PWM_02;
    public static Pin DRIVE_BACK_RIGHT = PCA9685Pin.PWM_03;
    public static Pin CONVEYOR_A = PCA9685Pin.PWM_04;
    public static Pin CONVEYOR_B = PCA9685Pin.PWM_05;
    public static Pin VACUUM_ROLLER = PCA9685Pin.PWM_06;
    public static Pin FLYWHEEL_A = PCA9685Pin.PWM_07;
    public static Pin FLYWHEEL_B =PCA9685Pin.PWM_08;
    public static Pin CLIMBING_ARM = PCA9685Pin.PWM_09;
    public static Pin CLIMBING_WRIST = PCA9685Pin.PWM_10;
    public static Pin SHOOTING_AIM_ADJUST = PCA9685Pin.PWM_11;
}
