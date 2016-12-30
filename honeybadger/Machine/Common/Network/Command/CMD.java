package Machine.Common.Network.Command;

import Machine.rpi.HoneybadgerV6;
import Machine.rpi.hw.BadgerMotorController;
import com.pi4j.io.gpio.Pin;

import java.util.Arrays;

import static Machine.Common.Utils.Log;

/**
 * Namespace class for classes implementing the IBadgerFunction interface
 */
public class CMD {
    public static abstract class MotorFunction implements IBadgerFunction{
        protected BadgerMotorController BMC;

        protected boolean areParametersValid(String[] params){
            return params.length>=MinimumParameterNum();
        }

        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            BMC = badger.getMotorController();
            if(!areParametersValid(params)){
                //Error and stop
                Log(String.format("Unable to invoke setPWM with \'%s\'", Arrays.toString(params)));
                return false;
            }
            return true;
        }
    }


    public static class setPWM extends MotorFunction{
        public setPWM(){}

        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            if(!super.Invoke(badger,params)){
                return false;
            }

            //Next param is pin name and float
            int PWMnum = Integer.parseInt(params[0]);
            Pin namedPin = BMC.getPWMPin(PWMnum);
            float value = Float.parseFloat(params[1]);

            BMC.setPWM(namedPin, value);
            return true;
        }

        @Override
        public String Explain() {
            return "\"CMD setPWM <PWM num> <Percentage>\"";
        }

        @Override
        public int MinimumParameterNum() {
            return 2;
        }
    }

    public static class setAbsPWM extends MotorFunction{
        public setAbsPWM(){}

        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            if(!super.Invoke(badger,params)){
                return false;
            }
            //Next param is pin name and float
            int PWMnum = Integer.parseInt(params[0]);
            Pin namedPin = BMC.getPWMPin(PWMnum);
            int value = Integer.parseInt(params[1]);

            BMC.setAbsPWM(namedPin, value);
            return true;
        }

        @Override
        public String Explain() {
            return "\"CMD setAbsPWM <PWM num> <Time in micro seconds>\"";
        }

        @Override
        public int MinimumParameterNum() {
            return 2;
        }
    }

    public static class sweepPWM extends MotorFunction{
        public sweepPWM(){}

        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            if(!super.Invoke(badger,params)){
                return false;
            }

            int PWMnum = Integer.parseInt(params[0]);
            Pin namedPin = BMC.getPWMPin(PWMnum);

            float minVal = Float.parseFloat(params[1]);
            float maxVal = Float.parseFloat(params[2]);
            float step = 2.5f;

            int sleepTime = 1000;
            if(params.length>MinimumParameterNum()){
                sleepTime = Integer.parseInt(params[3]);
            }
            //Not needed, but nice to have
            int stepCount = (int)((maxVal-minVal)/step);

            //Sweep!
            for (float i = minVal; i < maxVal; i+=step) {
                BMC.setPWM(namedPin, i);
                try{
                    Thread.sleep(sleepTime);
                }catch (Exception e){ Log("DANGER: Interrupted during PWM SWEEP!");}
            }

            return true;
        }

        @Override
        public String Explain() {
            return "\"CMD sweepPWM <PWM num> <min percent> <max percent> <OPTIONAL: time in ms>\"";
        }

        @Override
        public int MinimumParameterNum() {
            return 3;
        }
    }

    public static class setMotor extends MotorFunction{
        public setMotor(){}

        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            if(!super.Invoke(badger,params)){
                return false;
            }

            String motorName = params[0];
            Pin motorPWM = BMC.getPWMPin(motorName);
            Pin motorGPIO = BMC.getGPIOPin(motorName);
            int direction = Integer.parseInt(params[1]);
            float throttle = Float.parseFloat(params[2]);

            badger.SetMotor(motorGPIO,motorPWM,direction,throttle);
            return true;
        }

        @Override
        public String Explain() {
            return "\"CMD setMotor <Drive Motor Name> <Direction (0,1)> <Throttle percent>\"";
        }

        @Override
        public int MinimumParameterNum() {
            return 3;
        }
    }

    public static class servo extends MotorFunction{
        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            if(!super.Invoke(badger,params)){
                return false;
            }

            int servoID = Integer.parseInt(params[0]);
            int position = Integer.parseInt(params[1]);

            BMC.setServoPosition(servoID,position);
            return true;
        }

        @Override
        public String Explain() {
            return "\"servo <ServoID> <Position from 0 to 1023>\"";
        }

        @Override
        public int MinimumParameterNum() {
            return 2;
        }
    }

    public static class flywheel extends MotorFunction{
        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            if(!super.Invoke(badger,params)){
                return false;
            }

            //TODO: Implement
            return true;
        }

        @Override
        public String Explain() {
            return "\"flywheel <Percent throttle>\"";
        }

        @Override
        public int MinimumParameterNum() {
            return 1;
        }
    }

    public static class stop implements IBadgerFunction{
        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            badger.STOP();
            return true;
        }

        @Override
        public String Explain() {
            return "\'stop\'";
        }

        @Override
        public int MinimumParameterNum() {
            return 0;
        }
    }
}
