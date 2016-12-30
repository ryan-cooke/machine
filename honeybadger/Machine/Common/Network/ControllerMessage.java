package Machine.Common.Network;

import Machine.rpi.HoneybadgerV6;
import Machine.rpi.hw.BadgerMotorController;
import Machine.rpi.hw.BadgerPWMProvider;
import Machine.rpi.hw.RPI;

import java.io.Serializable;

import static Machine.Common.Utils.Log;

/**
 * Handles everything related to networking the controller
 */
public class ControllerMessage extends BaseMsg {
    /**
     * Inner interface to define a new controller action that a honeybadger must do
     */
    public interface ControllerAction{
        void Do(HoneybadgerV6 badger);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // START Controller Action Commands
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static class MoveForward implements ControllerAction, Serializable{
        float throttle;

        public MoveForward(float throttle){
            this.throttle=throttle;
        }

        public void Do(HoneybadgerV6 badger){
            badger.moveForward(throttle);
        }

        @Override
        public String toString(){
            return "Pressed A";
        }
    }

    public static class MoveLeft implements ControllerAction, Serializable{
        float throttle;

        public MoveLeft(float throttle){
            this.throttle=throttle;
        }

        public void Do(HoneybadgerV6 badger){
            badger.strafeLeft(throttle);
        }
    }

    public static class MoveRight implements ControllerAction, Serializable{
        float throttle;

        public MoveRight(float throttle){
            this.throttle=throttle;
        }

        public void Do(HoneybadgerV6 badger){
            badger.strafeRight(throttle);
        }
    }

    public static class MoveBack implements ControllerAction, Serializable{
        float throttle;

        public MoveBack(float throttle){
            this.throttle=throttle;
        }

        public void Do(HoneybadgerV6 badger){
            badger.moveBackward(throttle);
        }
    }

    public static class Shoot implements ControllerAction, Serializable{
        float throttle;

        public Shoot(float throttle){
            this.throttle=throttle;
        }

        public void Do(HoneybadgerV6 badger){
            badger.setFlywheelSpeed(throttle);
        }
    }

    public static class Stop implements ControllerAction, Serializable{
        public void Do(HoneybadgerV6 badger){
            badger.STOP();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // START Debug Commands
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class DEBUG_MOTOR implements ControllerAction, Serializable{
        protected int dir;
        public static float throttle;
        DEBUG_MOTOR(){
            dir=0;
            throttle=100;
        }
        public void Do(HoneybadgerV6 badger){
            badger.setDriveMotor(RPI.DRIVE_FRONT_LEFT, BadgerPWMProvider.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE,throttle);
        }
    }

    public static class DEBUG_MOTOR_FL extends DEBUG_MOTOR{
        public DEBUG_MOTOR_FL(int direction){
            dir = direction;
        }
        public void Do(HoneybadgerV6 badger){
            badger.setDriveMotor(RPI.DRIVE_FRONT_LEFT, BadgerPWMProvider.DRIVE_FRONT_LEFT, dir,throttle);
        }
    }

    public static class DEBUG_MOTOR_FR extends DEBUG_MOTOR{
        public DEBUG_MOTOR_FR(int direction){
            dir = direction;
        }
        public void Do(HoneybadgerV6 badger){
            badger.setDriveMotor(RPI.DRIVE_FRONT_RIGHT, BadgerPWMProvider.DRIVE_FRONT_RIGHT, dir,throttle);
        }
    }
    public static class DEBUG_MOTOR_BL extends DEBUG_MOTOR{
        public DEBUG_MOTOR_BL(int direction){
            dir = direction;
        }
        public void Do(HoneybadgerV6 badger){
            badger.setDriveMotor(RPI.DRIVE_BACK_LEFT, BadgerPWMProvider.DRIVE_BACK_LEFT, dir,throttle);
        }
    }
    public static class DEBUG_MOTOR_BR extends DEBUG_MOTOR{
        public DEBUG_MOTOR_BR(int direction){
            dir = direction;
        }
        public void Do(HoneybadgerV6 badger){
            badger.setDriveMotor(RPI.DRIVE_BACK_RIGHT, BadgerPWMProvider.DRIVE_BACK_RIGHT, dir,throttle);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // END Controller Action Commands
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ControllerAction Action;

    public ControllerMessage(ControllerAction someAction){
        Action = someAction;
        payload = someAction.getClass().getName();
    }

    @Override
    public void Execute(Object context) {
        HoneybadgerV6 badger = (HoneybadgerV6) context;
        if(badger==null){
            Log(String.format("Unable to execute %s",this.payload));
            //TODO: Send some error message back
            return;
        }

        // Execute
        Action.Do(badger);
    }
}
