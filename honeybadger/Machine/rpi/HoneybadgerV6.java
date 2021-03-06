package Machine.rpi;

import Machine.Common.Constants;
import Machine.Common.Network.BaseMsg;
import Machine.Common.Network.ErrorMessage;
import Machine.Common.Network.StatusMessage;
import Machine.Common.Utils;
import Machine.rpi.hw.BadgerMotorController;
import Machine.rpi.hw.BadgerPWMProvider;
import Machine.rpi.hw.RPI;
import Machine.Common.Utils.Button;

import com.pi4j.component.motor.Motor;
import com.pi4j.io.gpio.Pin;

import java.util.HashMap;

import static Machine.Common.Utils.Clamp;
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

    private int FlywheelCannonAngle;

    //Values determined empirically.
    public static float MaxFlywheelPowerA = 50.f;
    public static float MaxFlywheelPowerB = 50.f;

    public static float BACKWARDS_COMPENSATION_FACTOR = 1; //(5/3)

    private boolean RotatedControls = false;

    /**
     * Makes a new Honeybadger (this is version 6). Guaranteed not to give a shit
     * @throws Exception But honey badger don't give a shit
     */
    private HoneybadgerV6() throws Exception {
        MotorController = new BadgerMotorController();
        NetworkServer = new BadgerNetworkServer(this);

        IsMoving = false;
        IsListeningToController = true;

        FlywheelThrottleA = 0.f;
        FlywheelThrottleB = 0.f;
        FlywheelCannonAngle = BadgerMotorController.FLYWHEEL_ANGLE_LOWEST;
        FlywheelIsReady = false;

        Log("Made the Badger V6.");
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


    public boolean isListeningToController() {
        return IsListeningToController;
    }

    /**
     * Receive controller update to change movement and control speed
     * @param dir Single character representing the direction (N,S,E,W or Z)
     * @param throttle a float between 0.0 and 1.0, as given by controller input (for example)
     */
    public void updateMovement(char dir, float throttle){
        sendAckMessageToDesktop(String.format("Moving direction %s - throttle %f",dir,throttle));

        //Change to a map with lambdas or something...
        if (!RotatedControls) {
            switch (dir){
                case 'N':{ //up
                    IsMoving = true;
                    moveBackward(throttle);
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
                    moveForward(throttle);
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
        } else {
            switch (dir){
                case 'N':{ //up
                    IsMoving = true;
                    strafeRight(throttle);
                    break;
                }
                case 'E':{ //right
                    IsMoving = true;
                    moveBackward(throttle);
                    break;
                }
                case 'S':{ //down
                    IsMoving = true;
                    strafeLeft(throttle);
                    break;
                }
                case 'W':{ //left
                    IsMoving = true;
                    moveForward(throttle);
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
    }

    /**
     * Receive controller update to move directions
     * @param dir Single character representing the direction (N,S,E,W or Z)
     * @param throttle a float between 0.0 and 1.0, as given by controller input (for example)
     */
    public void updateRotation(char dir, float throttle){
        sendAckMessageToDesktop(String.format("Rotating in direction %s - throttle %f",dir,throttle));

        if( !IsMoving){
            switch (dir){
                case 'N':{
                    raiseShootingAngle(throttle);
                    break;
                }
                case 'W':{
                    spinLeft(throttle);
                    break;
                }
                case 'E':{
                    spinRight(throttle);
                    break;
                }
                case 'S':{
                    lowerShootingAngle(throttle);
                    break;
                }
                case 'Z':{
                    //Do nothing.
                    break;
                }
                default:{
                    sendDebugMessageToDesktop("Rotation update not understood");
                }
            }
        }
    }


    public void handleButtonPress(HashMap<Button, Boolean> buttons){
        if (buttons.get(Button.A)){
            handleA();
        }
        if (buttons.get(Button.B)){
            handleB();
        }
        handleX(buttons.get(Button.X));
        if (buttons.get(Button.Y)){
            handleY();
        }
        if (buttons.get(Button.BACK)){
            handleBack();
        }
        if (buttons.get(Button.START)){
            handleStart();
        }
        if (buttons.get(Button.RBUMPER)){
            handleRBumper();
        }
        if (buttons.get(Button.LBUMPER)){
            handleLBumper();
        }
        if(!buttons.get(Button.RBUMPER) && !buttons.get(Button.LBUMPER)){
            setConveyor(1,0);
            stopVacuumRoller();
        }
        if (buttons.get(Button.RTHUMB)){
            handleRThumb();
        }
        if (buttons.get(Button.LTHUMB)){
            handleLThumb();
        }
        if (buttons.get(Button.NDPAD)){
            handleNDPad();
        }
        if (buttons.get(Button.EDPAD)){
            handleEDPad();
        }
        if (buttons.get(Button.WDPAD)){
            handleWDPad();
        }
        if (buttons.get(Button.SDPAD)){
            handleSDPad();
        }
    }

    private void handleA(){
        armFlywheel();
    }

    private void handleB(){
        disarmFlywheel();
    }

    private void handleX(Boolean isPressed){
        RotatedControls = isPressed;
    }

    private void handleY(){
        startVacuumRoller();
    }

    private void handleBack(){
        listenToController(false);
    }

    private void handleStart(){
        listenToController(true);
    }

    private void handleRBumper(){
        setConveyor(BadgerMotorController.FORWARD,100.f);
        startVacuumRoller();
    }

    private void handleLBumper(){
        setConveyor(BadgerMotorController.BACKWARD,60.f);
        stopVacuumRoller();
    }

    private void handleRThumb(){
        BREAK();
    }

    private void handleLThumb(){
        STOP();
    }

    private void handleNDPad(){
        setConveyor(BadgerMotorController.BACKWARD,60.f);
    }

    private void handleEDPad(){
        stopVacuumRoller();
    }

    private void handleWDPad(){
        startVacuumRoller();
    }

    private void handleSDPad() {
        setConveyor(BadgerMotorController.FORWARD,100.f);
    }



    /**
     * Move the conveyor in one direction
     * @param throttle a float between 0.0 and 1.0, as given by controller input (for example)
     */
    public void updateConveyor (float throttle){
        throttle = Utils.Clamp(throttle*100.f,0.f,100.f);
        sendAckMessageToDesktop(String.format("Conveyor speed %f",throttle));
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.CONVEYOR_A,throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.CONVEYOR_B,throttle);
    }

    /**
     * Update the flywheel cannon speed by a regular step
     * @param updateFactor a float between 0.0 and 1.0 that will be used to determine the step update, as given by controller input
     * @param wantsAdditional5Percent boolean that will be true if the leftTrigger is pressed.
     */
    public void updateFlywheel(float updateFactor, boolean wantsAdditional5Percent){
        final float minFlywheelPower = BadgerMotorController.FLYWHEEL_PERCENT_MIN;

        //Values determined empirically.
        float maxFlywheelPowerA = MaxFlywheelPowerA;
        float maxFlywheelPowerB = MaxFlywheelPowerB;

        if (updateFactor > 0.1 && wantsAdditional5Percent) {
            maxFlywheelPowerA = MaxFlywheelPowerA+5.f;
        } else if (updateFactor < 0.1 && wantsAdditional5Percent) {
            maxFlywheelPowerA = 5.f;
            maxFlywheelPowerB = 5.f;
        }

        final float step = 0.22f;

        //The step is used to determine whether the flywheel speed should decrease, increase or stay the same.
        float delta = 0.f;
        if(updateFactor<0.3f){ //decrease
            delta -= step;
        }
        else if(updateFactor > 0.56f && updateFactor<1.1f){
            delta += step*updateFactor;
        }

        //Update and keep it in the safe ranges.
        FlywheelThrottleA = Utils.Clamp(FlywheelThrottleA+delta ,minFlywheelPower,maxFlywheelPowerA);
        FlywheelThrottleB = Utils.Clamp(FlywheelThrottleB+delta ,minFlywheelPower,maxFlywheelPowerB);

        if(FlywheelIsReady) {
            sendAckMessageToDesktop(String.format("Flywheel speed A:%f B:%f",FlywheelThrottleA,FlywheelThrottleB));
            MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_A, FlywheelThrottleA); //THIS IS THE LOWER ONE!!!!
//            MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_B, FlywheelThrottleB); //THis is the upper one
        }
    }

    public void armFlywheel(){
        if(FlywheelIsReady){
            //Everything should be ready.
            return;
        }
        sendAckMessageToDesktop("Flywheel Armed");
        MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_A,BadgerMotorController.FLYWHEEL_PERCENT_MIN);
        MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_B,BadgerMotorController.FLYWHEEL_PERCENT_MIN);
        FlywheelIsReady = true;
    }

    public void disarmFlywheel(){
        if(!FlywheelIsReady){
            //Already disarmed
            return;
        }
        sendAckMessageToDesktop("Flywheel Disarmed");
        MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_A,0);
        MotorController.setPWM(BadgerPWMProvider.FLYWHEEL_A,0);
        FlywheelIsReady = false;
    }

    /**
     * Sets the direction of the badger's movement to forward at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackward(float throttle) {
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.BACKWARD);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle);
    }

    /**
     * Sets the direction of the badger's movement to backwards at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveForward(float throttle) {
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.FORWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.FORWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.FORWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.FORWARD);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle);
    }

    /**
     * Sets the direction of the badger's movement to spin to the right at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void spinLeft(float throttle) {
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.FORWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.FORWARD);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle*BACKWARDS_COMPENSATION_FACTOR);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle*BACKWARDS_COMPENSATION_FACTOR);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle);
    }

    /**
     * Sets the direction of the badger's movement to spin left at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void spinRight(float throttle) {
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.FORWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.FORWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.BACKWARD);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle*BACKWARDS_COMPENSATION_FACTOR);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle*BACKWARDS_COMPENSATION_FACTOR);
    }

    /**
     * Sets the direction of the badger's movement to strafe left at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void strafeRight(float throttle) {
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.FORWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.FORWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.BACKWARD);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle);
    }

    /**
     * Sets the direction of the badger's movement to strafe right at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void strafeLeft(float throttle) {
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.FORWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.FORWARD);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle);
    }

    private void raiseShootingAngle(float throttle){
        if(FlywheelCannonAngle < BadgerMotorController.FLYWHEEL_ANGLE_HIGHEST) {
            FlywheelCannonAngle += Utils.Clamp((10*throttle), 0, 10);

            if(FlywheelCannonAngle > BadgerMotorController.FLYWHEEL_ANGLE_HIGHEST){
                FlywheelCannonAngle = BadgerMotorController.FLYWHEEL_ANGLE_HIGHEST;
            }
            MotorController.setServoPosition(BadgerMotorController.FLYWHEEL_SERVO_ID, FlywheelCannonAngle);
        }
    }

    private void lowerShootingAngle(float throttle){
        if(FlywheelCannonAngle > BadgerMotorController.FLYWHEEL_ANGLE_LOWEST) {
            FlywheelCannonAngle -= Utils.Clamp((10*throttle), 0, 10);
            if(FlywheelCannonAngle < BadgerMotorController.FLYWHEEL_ANGLE_LOWEST){
                FlywheelCannonAngle = BadgerMotorController.FLYWHEEL_ANGLE_LOWEST;
            }
            MotorController.setServoPosition(BadgerMotorController.FLYWHEEL_SERVO_ID, FlywheelCannonAngle);
        }
    }

    /**
     *
     */
    private void resetFlywheelAngle(){
        MotorController.setServoPosition(
                BadgerMotorController.FLYWHEEL_SERVO_ID,
                BadgerMotorController.FLYWHEEL_ANGLE_START);
        FlywheelCannonAngle = BadgerMotorController.FLYWHEEL_ANGLE_START;
    }

    public void STOP(){
        sendDebugMessageToDesktop("Motor Controllers stopped.");

        MotorController.stopDriveMotors();
        resetFlywheelAngle();

        this.IsListeningToController = false;
        this.IsMoving = false;
    }

    public void BREAK() {
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.BACKWARD);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, 0);
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
        sendAckMessageToDesktop(String.format("Conveyors moving %s at %f", direction==1? "FWD":"BCK",throttle));

        //Both go in same direction.
        MotorController.setDriveMotorDirection(RPI.CONVEYOR_A,direction);
        MotorController.setDriveMotorDirection(RPI.CONVEYOR_B,direction);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.CONVEYOR_A,throttle);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.CONVEYOR_B,throttle);
    }

    public void startVacuumRoller(){
        MotorController.setDriveMotorDirection(RPI.VACUUM_ROLLER,BadgerMotorController.FORWARD);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.VACUUM_ROLLER,100.f);
    }

    public void stopVacuumRoller(){
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.VACUUM_ROLLER,0.f);
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
//        message.appendDeviceStatus(this);

        NetworkServer.SendMessage(message);
    }

    public void sendAckMessageToDesktop(String msg){
        if(Constants.DEBUG_MODE_ON){
            sendDebugMessageToDesktop(msg);
        }
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
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_LEFT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_RIGHT, BadgerMotorController.FORWARD);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, speed*BACKWARDS_COMPENSATION_FACTOR);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to diagonally backwards and to the left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackwardLeft(int speed) {
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_LEFT, BadgerMotorController.FORWARD);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_RIGHT, BadgerMotorController.BACKWARD);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, speed*BACKWARDS_COMPENSATION_FACTOR);
    }

    /**
     * Sets the direction of the badger's movement to diagonally forward and to the left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveFowardLeft(int speed) {
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_LEFT, BadgerMotorController.BACKWARD);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_RIGHT, BadgerMotorController.FORWARD);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, speed*BACKWARDS_COMPENSATION_FACTOR);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, 0);
    }

    /**
     * Sets the direction of the badger's movement to backwards and to the right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackwardRight(int speed) {
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_LEFT, BadgerMotorController.FORWARD);
        MotorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_RIGHT, BadgerMotorController.BACKWARD);

        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, 0);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, speed);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, speed*BACKWARDS_COMPENSATION_FACTOR);
        MotorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, 0);
    }
}
