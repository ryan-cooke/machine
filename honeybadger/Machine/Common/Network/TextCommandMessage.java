package Machine.Common.Network;

import Machine.Common.Utils;
import Machine.rpi.HoneybadgerV6;
import Machine.rpi.hw.BadgerMotorController;
import com.pi4j.io.gpio.Pin;
import sun.rmi.runtime.Log;

import java.util.HashMap;

/**
 * Class to send debug, plain-text commands.
 *  A lot simpler/more reliable than ReflectionMessage.
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
        BadgerMotorController BMC = badger.getMotorController();
        switch (Command[0]){
            case "setPWM": {
                //Next param is pin name and float
                int PWMnum = Integer.parseInt(Command[1]);
                Pin namedPin = BMC.getPWMPin(PWMnum);
                float value = Float.parseFloat(Command[2]);

                BMC.setPWM(namedPin, value);
                break;
            }

            case "setAbsPWM": {
                //Next param is pin name and float
                int PWMnum = Integer.parseInt(Command[1]);
                Pin namedPin = BMC.getPWMPin(PWMnum);
                int value = Integer.parseInt(Command[2]);

                BMC.setAbsPWM(namedPin, value);
                break;
            }

            case "sweepPWM":{ //TODO: USE and DEBUG
                //Next param is pin name and float
                int PWMnum = Integer.parseInt(Command[1]);
                Pin namedPin = BMC.getPWMPin(PWMnum);

                float minVal = Float.parseFloat(Command[2]);
                float maxVal = Float.parseFloat(Command[3]);
                float step = 5.f;
                if(Command.length>4){
                    step = Float.parseFloat(Command[4]);
                }

                //Sweep!
                for (float i = minVal; i < maxVal; i+=step) {
                    BMC.setPWM(namedPin, i);
                    try{
                        Thread.sleep(1000);
                    }catch (Exception e){}
                }

                break;
            }

            case "setMotor":{ //e.g. "CMD setMotor FL 0 100"
                String motorName = Command[1];
                Pin motorPWM = BMC.getPWMPin(motorName);
                Pin motorGPIO = BMC.getGPIOPin(motorName);
                int direction = Integer.parseInt(Command[2]);
                float throttle = Float.parseFloat(Command[3]);

                badger.SetMotor(motorGPIO,motorPWM,direction,throttle);
                break;
            }

            //TODO: add more cases!
        }
    }
}