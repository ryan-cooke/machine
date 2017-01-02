package Machine.Common.Network.Command;

import Machine.rpi.HoneybadgerV6;
import Machine.rpi.hw.BadgerMotorController;
import Machine.rpi.hw.BadgerPWMProvider;
import Machine.rpi.hw.RPI;
import com.pi4j.io.gpio.Pin;

import java.util.Arrays;

import static Machine.Common.Utils.Log;

/**
 * Namespace class for classes implementing the IBadgerFunction interface
 */
public class CMD {
    public static abstract class CheckedFunction implements IBadgerFunction{
        protected BadgerMotorController BMC;

        protected boolean areParametersValid(String[] params){
            return params.length>=MinimumParameterNum();
        }

        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            BMC = badger.getMotorController();
            if(!areParametersValid(params)){
                //Error and stop
                Log(String.format("Unable to invoke \'%s\' with params \'%s\'",
                        this.getClass().getSimpleName(),
                        Arrays.toString(params)));
                return false;
            }
            return true;
        }
    }

    public static class setGPIO extends CheckedFunction {
        public setGPIO(){}

        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            if(!super.Invoke(badger,params)){
                return false;
            }

            Pin namedPin = RPI.getPinByStandardNumber(Integer.parseInt(params[0]));
            int value = Integer.parseInt(params[1]);
            BMC.setPWM(namedPin, value);
            return true;
        }

        @Override
        public String Explain() {
            return "\"CMD setGPIO <GPIO Standard pin num> <1||0>\"";
        }

        @Override
        public int MinimumParameterNum() {
            return 2;
        }
    }

    public static class setPWM extends CheckedFunction {
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

    public static class setAbsPWM extends CheckedFunction {
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

    public static class sweepPWM extends CheckedFunction {
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

    public static class setMotor extends CheckedFunction {
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

            badger.setDriveMotor(motorGPIO,motorPWM,direction,throttle);
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

    public static class servo extends CheckedFunction {
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

    public static class setConveyor extends CheckedFunction {
        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            if(!super.Invoke(badger,params)){
                return false;
            }

            int direction = Integer.parseInt(params[0]);
            float throttle = Float.parseFloat(params[1]);

            badger.setConveyor(direction,throttle);
            return true;
        }

        @Override
        public String Explain() {
            return "\"setConveyor <Direction (0,1)> <Throttle percent>\"";
        }

        @Override
        public int MinimumParameterNum() {
            return 2;
        }
    }

    public static class setAllDriveMotors extends CheckedFunction {
        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            if(!super.Invoke(badger,params)){
                return false;
            }

            float throttle = Float.parseFloat(params[0]);
            for(Pin motor : BadgerPWMProvider.DriveMotors){
                BMC.setPWM(motor,throttle);
            }

            return true;
        }

        @Override
        public String Explain() {
            return "\"setAllDriveMotors <Throttle percent>\"";
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

    public static class ack implements IBadgerFunction{
        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            badger.sendDebugMessageToDesktop(Arrays.toString(params));
            return true;
        }

        @Override
        public String Explain() {
            return "\'ack <anything>\'";
        }

        @Override
        public int MinimumParameterNum() {
            return 1;
        }
    }

    //Implements basic movement
    public static class move extends CheckedFunction{
        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            if(!super.Invoke(badger,params)){
                return false;
            }
            char direction = params[0].toLowerCase().charAt(0);
            float throttle = Float.parseFloat(params[1]);

            switch (direction){
                case 'f':{
                    badger.moveForward(throttle);
                    break;
                }
                case 'b':{
                    badger.moveBackward(throttle);
                    break;
                }
                case 'l':{
                    badger.strafeLeft(throttle);
                    break;
                }
                case 'r':{
                    badger.strafeRight(throttle);
                    break;
                }
                default:{
                    badger.moveForward(0);
                    break;
                }
            }

            return true;
        }

        @Override
        public String Explain() {
            return "\'move <F,B,L,R> <Throttle Percent>\'";
        }

        @Override
        public int MinimumParameterNum() {
            return 2;
        }
    }

    public static class primeCannon extends CheckedFunction{
        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            if(!super.Invoke(badger,params)){
                return false;
            }
            int input = Integer.parseInt(params[0]);
            if(input!=0 && input!=1){
                return false;
            }

            boolean useFlywheel = input==1;
            if(useFlywheel){
                try {
                    badger.sendDebugMessageToDesktop("Activating flywheel and speeding up to safe max");
                    badger.armFlywheel();
                    Thread.sleep(1000);
                    for (int i = 0; i < 70; i++) {
                        badger.updateFlywheel(1.f);
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            else{
                badger.disarmFlywheel();
            }

            return true;
        }

        @Override
        public String Explain() {
            return "\'primeCannon <1: arm and speed up | 0: disarm>\'";
        }

        @Override
        public int MinimumParameterNum() {
            return 1;
        }
    }

    public static class rampFlywheel extends CheckedFunction{
        @Override
        public boolean Invoke(HoneybadgerV6 badger, String[] params) {
            if(!super.Invoke(badger,params)){
                return false;
            }
            int input = Integer.parseInt(params[0]);
            if(input!=0 && input!=1){
                return false;
            }

            float updateFactor = (float) input;

            try {
                badger.sendDebugMessageToDesktop("Ramping flywheel");
                Thread.sleep(1000);
                for (int i = 0; i < 70; i++) {
                    badger.updateFlywheel(updateFactor);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        public String Explain() {
            return "\'rampFlywheel <1: ramp up | 0: ramp down>\'";
        }

        @Override
        public int MinimumParameterNum() {
            return 1;
        }
    }
}
