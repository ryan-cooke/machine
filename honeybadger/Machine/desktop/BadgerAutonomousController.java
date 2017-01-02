package Machine.desktop;

import Machine.Common.Network.ControllerMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * The autonomous controller class that will run at the beggining of the match
 */
public class BadgerAutonomousController {

    private Thread autoThread;

    private NetworkConnector connector;

    private ControllerMessage controllerState;

    public BadgerAutonomousController(NetworkConnector nc){
        connector = nc;
        controllerState = new ControllerMessage();
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
        controllerState.leftThumbstickDirection = 'N';
        controllerState.leftThumbstickMagnitude = throttle;
    }

    public void strafeLeft(double throttle){
        controllerState.leftThumbstickDirection = 'W';
        controllerState.leftThumbstickMagnitude = throttle;
    }

    public void strafeRight(double throttle){
        controllerState.leftThumbstickDirection = 'E';
        controllerState.leftThumbstickMagnitude = throttle;
    }

    public void moveBackwards(double throttle){
        controllerState.leftThumbstickDirection = 'S';
        controllerState.leftThumbstickMagnitude = throttle;
    }

    public void rotateRight(double throttle){
        controllerState.rightThumbstickDirection = 'E';
        controllerState.rightThumbstickMagnitude = throttle;
    }

    public void rotateLeft(double throttle){
        controllerState.rightThumbstickDirection = 'W';
        controllerState.rightThumbstickMagnitude = throttle;
    }

    public void flywheel(double throttle){
        controllerState.rightTriggerMagnitude = throttle;
    }

    public void conveyors(double throttle){
        controllerState.leftTriggerMagnitude = throttle;
    }
}
