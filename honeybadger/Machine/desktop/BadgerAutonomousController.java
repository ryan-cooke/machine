package Machine.desktop;

import Machine.Common.Network.ControllerMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static Machine.Common.Utils.Log;

/**
 * The autonomous controller class that will run at the beggining of the match
 */
public class BadgerAutonomousController {
//    private NetworkConnector

    private ControllerMessage controllerState;

    public ControllerMessage getControllerState() {
        return controllerState;
    }

    public BadgerAutonomousController(ControllerMessage controllerState){
        this.controllerState = controllerState;
    }

    /**
     * Runs the autonomous script that begins sending messages to the RPi
     */
    public void TakeOver(){

    }

    /**
     * Helper methods to move the badger in given directions, and to activate the motors.
     * @param throttle: between 0-1.0, throttle they will be given.
     */
    public void moveForward(double throttle){
        clear();
        controllerState.leftThumbstickDirection = 'N';
        controllerState.leftThumbstickMagnitude = throttle;
    }

    public void strafeLeft(double throttle){
        clear();
        controllerState.leftThumbstickDirection = 'W';
        controllerState.leftThumbstickMagnitude = throttle;
    }

    public void strafeRight(double throttle){
        clear();
        controllerState.leftThumbstickDirection = 'E';
        controllerState.leftThumbstickMagnitude = throttle;
    }

    public void moveBackwards(double throttle){
        clear();
        controllerState.leftThumbstickDirection = 'S';
        controllerState.leftThumbstickMagnitude = throttle;
    }

    public void rotateRight(double throttle){
        clear();
        controllerState.rightThumbstickDirection = 'E';
        controllerState.rightThumbstickMagnitude = throttle;
    }

    public void rotateLeft(double throttle){
        clear();
        controllerState.rightThumbstickDirection = 'W';
        controllerState.rightThumbstickMagnitude = throttle;
    }

    public void flywheel(double throttle){
        clear();
        controllerState.rightTriggerMagnitude = throttle;
    }

    public void conveyors(double throttle){
        clear();
        controllerState.leftTriggerMagnitude = throttle;
    }

    public void clear(){
        controllerState.rightThumbstickDirection = 'Z';
        controllerState.leftThumbstickDirection = 'Z';
        controllerState.rightThumbstickMagnitude = 0.0;
        controllerState.leftThumbstickMagnitude = 0.0;
        controllerState.leftTriggerMagnitude = 0.0;
        controllerState.rightTriggerMagnitude = 0.0;
    }

}
