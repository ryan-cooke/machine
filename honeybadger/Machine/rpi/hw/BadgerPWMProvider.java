package Machine.rpi.hw;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CBus;

import java.io.IOException;

/**
 * A Specific implementation of the PCA9685 chip which is more personalized and fixes some issues in the PCA9685 provider
 */
public class BadgerPWMProvider extends PCA9685GpioProvider{
    public static Pin DRIVE_FRONT_LEFT = PCA9685Pin.PWM_00;
    public static Pin DRIVE_FRONT_RIGHT = PCA9685Pin.PWM_01;
    public static Pin DRIVE_BACK_LEFT = PCA9685Pin.PWM_02;
    public static Pin DRIVE_BACK_RIGHT = PCA9685Pin.PWM_03;

    public static Pin[] DriveMotors = {
            DRIVE_FRONT_LEFT,DRIVE_FRONT_RIGHT,
            DRIVE_BACK_LEFT,DRIVE_BACK_RIGHT
    };

    public static Pin CONVEYOR_A = PCA9685Pin.PWM_04;
    public static Pin CONVEYOR_B = PCA9685Pin.PWM_05;
    public static Pin VACUUM_ROLLER = PCA9685Pin.PWM_06;
    public static Pin FLYWHEEL_A = PCA9685Pin.PWM_07;
    public static Pin FLYWHEEL_B =PCA9685Pin.PWM_08;
    public static Pin CLIMBING_ARM = PCA9685Pin.PWM_09;
    public static Pin CLIMBING_WRIST = PCA9685Pin.PWM_10;
    public static Pin SHOOTING_AIM_ADJUST = PCA9685Pin.PWM_11;

    public static Pin[] OtherMotors = {
            CONVEYOR_A,
            CONVEYOR_B,
            VACUUM_ROLLER,
            FLYWHEEL_A,
            FLYWHEEL_B,
            CLIMBING_ARM,
            CLIMBING_WRIST,
            SHOOTING_AIM_ADJUST,
    };

    public BadgerPWMProvider(I2CBus bus, int address) throws IOException{
        super(bus,address);
    }

    @Override
    public void reset(){
        //Remember to set the Drive Motors to 4096!
        for(Pin pin : DriveMotors){
            setAlwaysOn(pin);
        }

        //The rest can be the way they are
        for(Pin pin : OtherMotors){
            setAlwaysOff(pin);
        }
    }
}
