package Machine.Common.Network;

import Machine.Common.Constants;
import Machine.Common.Constants.*;
import Machine.rpi.HoneybadgerV6;

import java.util.HashMap;

import static Machine.Common.Constants.getActivePlatform;
import static Machine.Common.Utils.Log;

/**
 * Class used by the RPi to send regular updates about it's state
 */
public class StatusMessage extends BaseMsg{
    public static HashMap<Integer,String> StatusMap;

    private PLATFORM sendingPlatform;


    public StatusMessage(String message){
        payload = message;
        sendingPlatform = Constants.getActivePlatform();
    }

    public void appendDeviceStatus(HoneybadgerV6 badger){
        //Maybe someday kid, maybe someday
    }

    public void Execute(Object context){
        Log(String.format("%s: \'%s\'",sendingPlatform.toString(),payload));
    }
}
