package Machine.desktop;

import Machine.Common.Network.ControllerMessage;
import Machine.Common.Utils;

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
    public void TakeOver() {
        moveBadger(0.6, 0.6, 2000, 0.5);
        shootBalls(1.0, 3000);
    }
    public void moveBadger(double first, double second, long time, double rotate){
        strafeLeft(0.5, 1000);
        goForward(first);
        strafeRight(second, time);
        rotateLeftToPole(rotate);
    }

    public void moveBadger(double first, long time1, double second, long time2, double rotate, long time3){
        strafeLeft(0.5, 1000);
        goForward(first, time1);
        strafeRight(second, time2);
        rotateLeft(rotate, time3);
    }

    public void goForward(double throttle){
        moveForward(throttle);
        while(!JPanelOpenCV.isTgReached());
        clear();
    }

    public void goForward(double throttle, long time){
        moveForward(throttle);
        long currentTime = System.currentTimeMillis();
        while(currentTime+time > System.currentTimeMillis());
        clear();
    }

    public void goBackwards(double throttle, long time){
        moveBackwards(throttle);
        long currentTime = System.currentTimeMillis();
        while(currentTime+time > System.currentTimeMillis());
        clear();

    }
    public void strafeRight(double throttle, long time){
        strafeRight(throttle);
        long currentTime = System.currentTimeMillis();
        while(currentTime+time > System.currentTimeMillis());
        clear();
    }

    public void strafeLeft(double throttle, long time){
        strafeLeft(throttle);
        long currentTime = System.currentTimeMillis();
        while(currentTime+time > System.currentTimeMillis());
        clear();
    }

    public void rotateLeftToPole(double throttle){
        rotateLeft(throttle);
        while(!JPanelOpenCV.isBlueTarget());
        clear();
    }

    public void rotateLeft(double throttle, long time){
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

    public void rotateRight(double throttle, long time){
        rotateRight(throttle);
        long currentTime = System.currentTimeMillis();
        while(currentTime+time > System.currentTimeMillis());
        clear();
    }
    public void shootBalls(double conveyors){
        shootBalls(conveyors, 5000);
    }

    public void shootBalls(double x, long time){
        armFlywheel();
        shoot(x);
        long currentTime = System.currentTimeMillis();
        while(currentTime+5000 > System.currentTimeMillis());
        conveyors();
        currentTime = System.currentTimeMillis();
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

    public void conveyors(){
        controllerState.buttons.replace(Utils.Button.RBUMPER, true);
    }

    public void shoot(double flywheel){
        clear();
        controllerState.rightTriggerMagnitude = flywheel;
    }

    public void armFlywheel(){
        clear();
        controllerState.buttons.replace(Utils.Button.A, true);
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
        controllerState.buttons.replace(Utils.Button.A, false);
        controllerState.buttons.replace(Utils.Button.RBUMPER, false);


    }

}
