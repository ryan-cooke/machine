package Machine.rpi.hw;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import java.util.HashMap;

/**
 * Contains definitions of each pin used on the RaspberryPI
 */
public class RPI {
    //Drive Motors
    public static Pin DRIVE_FRONT_LEFT = RaspiPin.GPIO_21;
    public static Pin DRIVE_FRONT_RIGHT = RaspiPin.GPIO_22;
    public static Pin DRIVE_BACK_LEFT = RaspiPin.GPIO_23;
    public static Pin DRIVE_BACK_RIGHT = RaspiPin.GPIO_24;

    //Conveyors
    public static Pin CONVEYOR_A = RaspiPin.GPIO_00;
    public static Pin CONVEYOR_B = RaspiPin.GPIO_02;

    //Vacuum roller
    public static Pin VACUUM_ROLLER = RaspiPin.GPIO_15;

    //Climbing
    public static Pin CLIMBING_MOTOR = RaspiPin.GPIO_12;

    protected static HashMap<String,Pin> PinNameMap;
    protected static HashMap<Integer,Pin> StandardPinNumberMap;

    protected static void CreateNameMap(){
        PinNameMap = new HashMap<>(4);
        PinNameMap.put("FL",DRIVE_FRONT_LEFT);
        PinNameMap.put("FR",DRIVE_FRONT_RIGHT);
        PinNameMap.put("BL",DRIVE_BACK_LEFT);
        PinNameMap.put("BR",DRIVE_BACK_RIGHT);

        PinNameMap.put("CONVEYOR_A",CONVEYOR_A);
        PinNameMap.put("CONVEYOR_B",CONVEYOR_B);
    }

    protected static void CreateRegularNumberMapping(){
        StandardPinNumberMap = new HashMap<>();
        StandardPinNumberMap.put(19,DRIVE_BACK_RIGHT);
        StandardPinNumberMap.put(13,DRIVE_BACK_LEFT);
        StandardPinNumberMap.put(6,DRIVE_FRONT_RIGHT);
        StandardPinNumberMap.put(5,DRIVE_FRONT_LEFT);
    }

    public static Pin getPinByName(String str){
        if(PinNameMap==null){
            CreateNameMap();
        }
        return PinNameMap.get(str);
    }

    public static Pin getPinByStandardNumber(int num){
        if(StandardPinNumberMap==null){
            CreateRegularNumberMapping();
        }
        return StandardPinNumberMap.get(num);
    }
}
