package Machine.Common.Network;

import Machine.Common.Utils;
import Machine.rpi.HoneybadgerV6;
import Machine.rpi.hw.BadgerMotorController;
import com.pi4j.io.gpio.Pin;

import java.util.HashMap;

/**
 * Created by Javier Fajardo on 2016-12-27.
 */
public class TextCommandMessage extends BaseMsg {
    public TextCommandMessage(String msg){
        payload = msg;
    }

    @Override
    public void Execute(Object context) {
        HoneybadgerV6 badger = (HoneybadgerV6) context;
        if(badger==null || !Utils.DEBUG_MODE_ON){
            return;
        }

        String[] Command = payload.split(" ");
        switch (Command[0]){
            case "setPWM": {
                BadgerMotorController BMC = badger.getMotorController();
                //Next param is pin name and float
                int PWMnum = Integer.parseInt(Command[1]);
                Pin namedPin = BMC.getPWMPin(PWMnum);
                float value = Float.parseFloat(Command[2]);

                BMC.setPWM(namedPin, value);
                break;
            }

            case "setAbsPWM": {
                BadgerMotorController BMC = badger.getMotorController();
                //Next param is pin name and float
                int PWMnum = Integer.parseInt(Command[1]);
                Pin namedPin = BMC.getPWMPin(PWMnum);
                int value = Integer.parseInt(Command[2]);

                BMC.setAbsPWM(namedPin, value);
                break;
            }


            //TODO: add more cases!
        }
    }
}
