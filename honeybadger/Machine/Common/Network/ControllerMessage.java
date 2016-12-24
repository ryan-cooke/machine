package Machine.Common.Network;

import Machine.rpi.Badger;
import Machine.rpi.hw.BadgerMotorController;
import Machine.rpi.hw.BadgerPWM;
import Machine.rpi.hw.RPI;

import java.io.Serializable;

/**
 * Created by Javier Fajardo on 2016-12-20.
 */
public class ControllerMessage extends BaseMsg {
    public interface ControllerAction{
        void Do(Badger badger);
    }

    public static class MoveForward implements ControllerAction, Serializable{
        float throttle;

        public MoveForward(float throttle){
            this.throttle=throttle;
        }

        public void Do(Badger badger){
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

        public void Do(Badger badger){
            badger.strafeLeft(throttle);
        }
    }

    public static class MoveRight implements ControllerAction, Serializable{
        float throttle;

        public MoveRight(float throttle){
            this.throttle=throttle;
        }

        public void Do(Badger badger){
            badger.strafeRight(throttle);
        }
    }

    public static class MoveBack implements ControllerAction, Serializable{
        float throttle;

        public MoveBack(float throttle){
            this.throttle=throttle;
        }

        public void Do(Badger badger){
            badger.moveBackward(throttle);
        }
    }

    public static class Stop implements ControllerAction, Serializable{
        public void Do(Badger badger){
            badger.STOP();
        }
    }

    public static class DEBUG_MOTOR_FL implements ControllerAction, Serializable{
        public void Do(Badger badger){
            badger.SetMotor(RPI.DRIVE_FRONT_LEFT, BadgerPWM.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE,100);
        }
    }

    public static class DEBUG_MOTOR_FR implements ControllerAction, Serializable{
        public void Do(Badger badger){
            badger.SetMotor(RPI.DRIVE_FRONT_RIGHT, BadgerPWM.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE,100);
        }
    }
    public static class DEBUG_MOTOR_BL implements ControllerAction, Serializable{
        public void Do(Badger badger){
            badger.SetMotor(RPI.DRIVE_BACK_LEFT, BadgerPWM.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE,100);
        }
    }
    public static class DEBUG_MOTOR_BR implements ControllerAction, Serializable{
        public void Do(Badger badger){
            badger.SetMotor(RPI.DRIVE_BACK_RIGHT, BadgerPWM.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE,100);
        }
    }

    private ControllerAction Action;

    public ControllerMessage(ControllerAction someAction){
        Action = someAction;
        payload = someAction.toString();
    }

    @Override
    public void Execute(Object context) {
        Badger badger = (Badger) context;
        if(badger==null){
            //TODO: Send some error message back
            return;
        }

        // Execute
        Action.Do(badger);
    }
}
