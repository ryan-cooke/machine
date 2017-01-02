package Machine.rpi;

import Machine.Common.Network.BaseMsg;
import Machine.Common.Network.ErrorMessage;
import Machine.Common.Network.StatusMessage;
import Machine.Common.Utils;
import Machine.rpi.hw.BadgerMotorController;
import Machine.rpi.hw.BadgerPWMProvider;
import Machine.rpi.hw.RPI;

import com.pi4j.io.gpio.Pin;

import static Machine.Common.Utils.ErrorLog;
import static Machine.Common.Utils.Log;

/**
 * Contains methods that represent all the physical actions the badger can execute
 */
public class HoneybadgerV6 {
    /**
     * The one and only Honeybadger. It doesn't care that singletons are considered anti-patterns.
     */
    private static HoneybadgerV6 Singleton;

    /**
     * Object used to interact with most of the underlying hardware
     */
    private BadgerMotorController MotorController;

    /**
     * Object used to create and manage networking capabilities
     */
    private BadgerNetworkServer NetworkServer;

    private boolean IsListeningToController;

    private boolean IsMoving;

    private boolean FlywheelIsReady;

    private float FlywheelThrottleA;

    private float FlywheelThrottleB;

    /**
     * Makes a new Honeybadger (this is version 6). Guaranteed not to give a shit
     * @throws Exception But honey badger don't give a shit
     */
    private HoneybadgerV6() throws Exception {
        MotorController = new BadgerMotorController();
        NetworkServer = new BadgerNetworkServer(this);

        IsMoving = false;
        IsListeningToController = false;

        FlywheelThrottleA = 0.f;
        FlywheelThrottleB = 0.f;
        FlywheelIsReady = false;

        Log("Made the BadgerV6");
    }

    public BadgerNetworkServer getNetworkServer(){
        return NetworkServer;
    }

    public BadgerMotorController getMotorController() { return MotorController; }

    /**
     * Retrieve the single instance of the Honeybadger V6
     * @return the only honeybadger instance.
     */
    public static HoneybadgerV6 getInstance(){
        if(Singleton==null){
            try {
                Singleton = new HoneybadgerV6();
            }
            catch (Exception e){
                Log("Encountered Unhandled Exception while making the honeybadger");
                System.err.flush();
                System.out.flush();
                Log("HALT AND CATCH FIRE.");
            }
        }

        return Singleton;
    }

    private void shutdown() {
        this.MotorController.shutdown();
    }

    /**
     * Notify the badger whether or not it should be listening for controller messages.
     * @param shouldListen
     */
    public void listenToController(boolean shouldListen){
        IsListeningToController = shouldListen;
    }

    /**
     * Receive controller update to change movement and control speed
     * @param dir Single character representing the direction (N,S,E,W or Z)
     * @param throttle a float between 0.0 and 1.0, as given by controller input (for example)
     */
    public void updateMovement(char dir, float throttle){
        //Ignore method call if we're not explicitly listening for a controller
        if(!IsListeningToController){
            return;
        }

        //Normalize the throttle to 0 to 100%
        throttle = Utils.Clamp(throttle*100.f,0.f,100.f);

        switch (dir){
            case 'N':{ //up
                IsMoving = true;
                moveForward(throttle);
                break;
            }
            case 'W':{ //left
                IsMoving = true;
                strafeLeft(throttle);
                break;
            }
            case 'E':{ //right
                IsMoving = true;
                strafeRight(throttle);
                break;
            }
            case 'S':{ //down
                IsMoving = true;
                moveBackward(throttle);
                break;
            }
            case 'Z':{ //no dir
                moveForward(0);
                IsMoving = false;
                break;
            }
            default:{
                sendDebugMessageToDesktop("Movement update not understood!");
                IsMoving = false;
                break;
            }
        }
    }

    /**
     * Receive controller update to move directions
     * @param dir Single character representing the direction (N,S,E,W or Z)
     * @param throttle a float between 0.0 and 1.0, as given by controller input (for example)
     */
    public void updateRotation(char dir, float throttle){
        if(!IsListeningToController){
            return;
        }

        //Normalize the throttle to 0 to 100%
        throttle = Utils.Clamp(throttle*100.f,0.f,100.f);

        if( IsMoving == false){
            switch (dir){
                case 'N':{
                    break;
                }
                case 'W':{
                    //spinLeft(throttle);
                    break;
                }
                case 'E':{
                    //spinRight(throttle);
                    break;
                }
                case 'S':{
                    break;
                }
                case 'Z':{
                    break;
                }
                default:{
                    sendDebugMessageToDesktop("Rotation update not understood");
                }
            }
        }
    }

    /**
     * Move the conveyor in one direction
     * @param throttle a float between 0.0 and 1.0, as given by controller input (for example)
     */
    public void updateConveyor(float throttle){
        throttle = Utils.Clamp(throttle*100.f,0.f,100.f);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.CONVEYOR_A,throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.CONVEYOR_B,throttle);
    }

    /**
     * Update the flywheel cannon speed by a regular step
     * @param updateFactor a float between 0.0 and 1.0 that will be used to determine the step update, as given by controller input
     */
    public void updateFlywheel(float updateFactor){
        final float minFlywheelPower = BadgerMotorController.FLYWHEEL_PERCENT_MIN;

        //Values determined empirically.
        final float maxFlywheelPowerA = 25.f;
        final float maxFlywheelPowerB = 20.f;

        final float step = 0.1f;

        //The step is used to determine whether the flywheel speed should decrease, increase or stay the same.
        float delta = 0.f;
        if(updateFactor>0.001f && updateFactor<0.3f){ //decrease
            delta -= step;
        }
        else if(updateFactor > 0.56f && updateFactor<1.1f){
            delta += step*updateFactor;
        }

        //Update and keep it in the safe ranges.
        FlywheelThrottleA = Utils.Clamp(FlywheelThrottleA+delta ,minFlywheelPower,maxFlywheelPowerA);
        FlywheelThrottleB = Utils.Clamp(FlywheelThrottleB+delta ,minFlywheelPower,maxFlywheelPowerB);

        if(FlywheelIsReady) {
            MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_A, FlywheelThrottleA);
            MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_B, FlywheelThrottleB);
        }
    }

    public void armFlywheel(){
        if(FlywheelIsReady){
            //Everything should be ready.
            return;
        }

        MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_A,BadgerMotorController.FLYWHEEL_PERCENT_MIN);
        MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_B,BadgerMotorController.FLYWHEEL_PERCENT_MIN);
    }

    public void disarmFlywheel(){
        if(!FlywheelIsReady){
            //Already disarmed
            return;
        }

        MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_A,0);
        MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_A,0);
    }

    /**
     * Sets the direction of the badger's movement to forward at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveForward(float throttle) {
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle);
    }

    /**
     * Sets the direction of the badger's movement to backwards at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackward(float throttle) {
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle);
    }

    /**
     * Sets the direction of the badger's movement to spin to the right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void spinRight(int speed) {
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to spin left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void spinLeft(int speed) {
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to strafe left at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void strafeLeft(float throttle) {
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle);
    }

    /**
     * Sets the direction of the badger's movement to strafe right at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void strafeRight(float throttle) {
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle);
    }

    public void STOP(){
        //KILL the Drive Motors
        MotorController.stopDriveMotors();

        //Stop the flywheels
        MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_A, BadgerMotorController.FLYWHEEL_PERCENT_MIN);
        MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_B, BadgerMotorController.FLYWHEEL_PERCENT_MIN);
    }

    /**
     * Move an individual drive motor
     * @param DirPin
     * @param PWMPin
     * @param direction
     * @param throttle
     */
    public void setDriveMotor(Pin DirPin, Pin PWMPin, int direction, float throttle){
        MotorController.setDriveMotorDirection(DirPin, direction);
        MotorController.setDriveMotorSpeed(PWMPin, throttle);
    }

    public void setConveyor(int direction, float throttle){
        int opposingDir = direction==0? 1 : 0;
        MotorController.setDriveMotorDirection(RPI.CONVEYOR_A,direction);
        MotorController.setDriveMotorDirection(RPI.CONVEYOR_B,opposingDir);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.CONVEYOR_A,throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.CONVEYOR_B,throttle);
    }

    /**
     * Send a simple message to the desktop
     * @param msg String with a message
     */
    public void sendMessageToDesktop(String msg){
        NetworkServer.SendMessage(new BaseMsg(msg));
    }

    /**
     * Send a Debug message to the desktop that will appear
     * @param msg String with a message
     */
    public void sendDebugMessageToDesktop(String msg){
        StatusMessage message = new StatusMessage(msg);
        message.appendDeviceStatus(this);

        NetworkServer.SendMessage(message);
    }

    /**
     * Notify of a critical, exception causing message.
     * @param msg String with attached info regarding the critical situation
     * @param except A copy of the known exception
     */
    public void sendCriticalMessageToDesktop(String msg, Exception except){
        NetworkServer.SendMessage(new ErrorMessage(msg,except));
    }

    /**
     * Sets the direction of the badger's movement to diagonally forward and to the right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveForwardRight(int speed) {
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to diagonally backwards and to the left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackwardLeft(int speed) {
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to diagonally forward and to the left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveFowardLeft(int speed) {
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, 0);
    }

    /**
     * Sets the direction of the badger's movement to backwards and to the right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackwardRight(int speed) {
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, 0);
    }
}
