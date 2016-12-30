package Machine.rpi.hw;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CBus;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * A Specific implementation of the PCA9685 chip which is more personalized and fixes some issues in the PCA9685 provider
 */
public class BadgerPWMProvider extends PCA9685GpioProvider{
    public static final int PWM_MAX = 4095;

    public static Pin DRIVE_FRONT_LEFT = PCA9685Pin.PWM_00;
    public static Pin DRIVE_FRONT_RIGHT = PCA9685Pin.PWM_01;
    public static Pin DRIVE_BACK_LEFT = PCA9685Pin.PWM_02;
    public static Pin DRIVE_BACK_RIGHT = PCA9685Pin.PWM_03;

    public static Pin[] DriveMotors = {
            DRIVE_FRONT_LEFT,DRIVE_FRONT_RIGHT,
            DRIVE_BACK_LEFT,DRIVE_BACK_RIGHT
    };

    public static Pin FLYWHEEL_A = PCA9685Pin.PWM_09;
    public static Pin FLYWHEEL_B =PCA9685Pin.PWM_10;


    //TODO: MAP and VERIFY!
    public static Pin CONVEYOR_A = PCA9685Pin.PWM_04;
    public static Pin CONVEYOR_B = PCA9685Pin.PWM_05;
    public static Pin VACUUM_ROLLER = PCA9685Pin.PWM_06;
    public static Pin CLIMBING_WHEEL = PCA9685Pin.PWM_07;

//    public static Pin CLIMBING_ARM = PCA9685Pin.PWM_09;
//    public static Pin CLIMBING_WRIST = PCA9685Pin.PWM_10;
//    public static Pin SHOOTING_AIM_ADJUST = PCA9685Pin.PWM_11;
    //End MAP and VERIFY range.


    public static Pin[] OtherMotors = {
            CONVEYOR_A,
            CONVEYOR_B,
            VACUUM_ROLLER,
            FLYWHEEL_A,
            FLYWHEEL_B,
//            CLIMBING_ARM,
//            CLIMBING_WRIST,
//            SHOOTING_AIM_ADJUST,
    };

    protected static HashMap<Integer,Pin> PinNumberMap;
    protected static HashMap<String,Pin> PinNameMap;

    public BadgerPWMProvider(I2CBus bus, int address) throws IOException{
        super(bus,address,new BigDecimal(490.00));

        PinNumberMap = new HashMap<>(10);
        PinNumberMap.put(0,PCA9685Pin.PWM_00);
        PinNumberMap.put(1,PCA9685Pin.PWM_01);
        PinNumberMap.put(2,PCA9685Pin.PWM_02);
        PinNumberMap.put(3,PCA9685Pin.PWM_03);
        PinNumberMap.put(4,PCA9685Pin.PWM_04);
        PinNumberMap.put(5,PCA9685Pin.PWM_05);
        PinNumberMap.put(6,PCA9685Pin.PWM_06);
        PinNumberMap.put(8,PCA9685Pin.PWM_08);
        PinNumberMap.put(9,PCA9685Pin.PWM_09);
        PinNumberMap.put(10,PCA9685Pin.PWM_10);
        PinNumberMap.put(11,PCA9685Pin.PWM_11);
        PinNumberMap.put(12,PCA9685Pin.PWM_12);
        PinNumberMap.put(13,PCA9685Pin.PWM_13);
        PinNumberMap.put(14,PCA9685Pin.PWM_14);
        PinNumberMap.put(15,PCA9685Pin.PWM_15);

        PinNameMap = new HashMap<>(4);
        PinNameMap.put("FL",DRIVE_FRONT_LEFT);
        PinNameMap.put("FR",DRIVE_FRONT_RIGHT);
        PinNameMap.put("BL",DRIVE_BACK_LEFT);
        PinNameMap.put("BR",DRIVE_BACK_RIGHT);
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

    public Pin getPinByNumber(int num){
        return PinNumberMap.get(num);
    }

    public Pin getPinByName(String str){
        return PinNameMap.get(str);
    }
}
