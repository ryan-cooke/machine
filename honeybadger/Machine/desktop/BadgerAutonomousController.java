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

    public void moveBadger(double first, long time1, double second, long time2, double rotate, long time3){
        goToOtherSide(first, time1);
        getToCenter(second, time2);
        rotateLeftToPole(rotate, time3);
    }

    public void placeBadger(double first, double second, long time, double rotate){
        goToOtherSide(first);
        getToCenter(second, time);
        rotateLeftToPole(rotate);
    }

    public void goToOtherSide(double throttle){
        moveForward(throttle);
        while(!JPanelOpenCV.isTgReached());
        clear();
    }

    public void goToOtherSide(double throttle, long time){
        strafeRight(throttle);
        long currentTime = System.currentTimeMillis();
        while(currentTime+time > System.currentTimeMillis());
        clear();
    }

    public void getToCenter(double throttle, long time){
        strafeRight(throttle);
        long currentTime = System.currentTimeMillis();
        while(currentTime+time > System.currentTimeMillis());
        clear();
    }

    public void rotateLeftToPole(double throttle){
        rotateLeft(throttle);
        while(!JPanelOpenCV.isBlueTarget());
        clear();
    }

    public void rotateLeftToPole(double throttle, long time){
        rotateLeft(throttle);
        long currentTime = System.currentTimeMillis();
        while(currentTime+time > System.currentTimeMillis());
        clear();
    }
    public void rotateRightToPole(double throttle){
        rotateRight(throttle);
        while(!JPanelOpenCV.isBlueTarget());
        clear();
    }

    public void shootBalls(double conveyors, double flywheel){
        shootBalls(conveyors, flywheel, 5000);
    }

    public void shootBalls(double flywheel, double conveyors, long time){
        shoot(flywheel, conveyors);
        long currentTime = System.currentTimeMillis();
        while(currentTime+time > System.currentTimeMillis());
        clear();
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

    public void shoot(double flywheel, double conveyors){
        clear();
        controllerState.rightTriggerMagnitude = flywheel;
        controllerState.leftTriggerMagnitude = conveyors;
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
