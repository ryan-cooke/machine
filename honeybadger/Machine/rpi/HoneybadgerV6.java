package Machine.rpi;

import Machine.Common.Network.BaseMsg;
import Machine.Common.Network.ErrorMessage;
import Machine.Common.Network.StatusMessage;
import Machine.rpi.hw.BadgerMotorController;
import Machine.rpi.hw.BadgerPWMProvider;
import Machine.rpi.hw.RPI;
import Machine.Common.Utils.Button;


import com.pi4j.io.gpio.Pin;

import java.util.HashMap;

import static Machine.Common.Utils.Log;

/**
 * Contains methods that represent all the physical actions the badger can execute
 */
public class HoneybadgerV6 {
    /**
     * The one and only Honeybadger
     */
    private static HoneybadgerV6 Singleton;

    /**
     * Object used to interact with most of the underlying hardware
     */
    private BadgerMotorController motorController;

    /**
     * Object used to create and manage networking capabilities
     */
    private BadgerNetworkServer networkServer;

    private float FlywheelThrottleA;

    private float FlywheelThrottleB;

    private boolean isMoving;

    public boolean allowController;

    /**
     * Makes a new Honeybadger (this is version 6). Guaranteed not to give a shit
     * @throws Exception But honey badger don't give a shit
     */
    private HoneybadgerV6() throws Exception {
        motorController = new BadgerMotorController();
        networkServer = new BadgerNetworkServer(this);

        FlywheelThrottleA = 0.f;
        FlywheelThrottleB = 0.f;
        isMoving = false;
        allowController = true;

        Log("Made the BadgerV6");
    }

    public BadgerNetworkServer getNetworkServer(){
        return networkServer;
    }

    public BadgerMotorController getMotorController() { return motorController; }

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
        this.motorController.shutdown();
    }

    /**
     * TODO: @foxtrot94
     * @param dir
     * @param throttle
     */
    public void updateMovement(char dir, float throttle){
        //Change to a map with lambdas or something...
        switch (dir){
            case 'N':{ //up
                isMoving = true;
                moveForward(throttle);
                break;
            }
            case 'W':{ //left
                isMoving = true;
                strafeLeft(throttle);
                break;
            }
            case 'E':{ //right
                isMoving = true;
                strafeRight(throttle);
                break;
            }
            case 'S':{ //down
                isMoving = true;
                moveBackward(throttle);
                break;
            }
            case 'Z':{ //no dir
                moveForward(0);
                isMoving = false;
                break;
            }
            default:{
                sendDebugMessageToDesktop("Movement update not understood!");
                isMoving = false;
                break;
            }
        }
    }

    /**
     *  @param dir
     * @param throttle
     */
    public void updateRotation(char dir, int throttle){
        if( !isMoving){
            switch (dir){
                case 'N':{
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


    public void handleButtonPress(HashMap<Button, Boolean> buttons){
        if (buttons.get(Button.A)){
            handleA();
        }
        if (buttons.get(Button.B)){
            handleB();
        }
        if (buttons.get(Button.X)){
            handleX();
        }
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

    public void handleA(){

    }

    public void handleB(){

    }

    public void handleX(){

    }

    public void handleY(){

    }

    public void handleBack(){
        disallowController();
    }

    public void handleStart(){
        allowController();
    }

    public void handleRBumper(){
        armFlywheel();
    }

    public void handleLBumper(){
        disarmFlywheel();
    }

    public void handleRThumb(){

    }

    public void handleLThumb(){

    }

    public void handleNDPad(){

    }

    public void handleEDPad(){

    }

    public void handleWDPad(){

    }

    public void handleSDPad(){

    }

    public void moveConveyor(float throttle){
        if(throttle>75){
            throttle = 75.f;
        }
        motorController.setDriveMotorSpeed(BadgerPWMProvider.CONVEYOR_A,throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.CONVEYOR_B,throttle);
    }

    public void increaseFlywheelSpeed(float step){
        final float minFlywheelPower = 10.f;
        //TODO: verify
        final float maxFlywheelPowerA = 25.f;
        final float maxFlywheelPowerB = 20.f;

        boolean shouldIncrease = step > 0.001f;
        if(shouldIncrease){
            FlywheelThrottleB += step;
            FlywheelThrottleA += step;
        }
        else{
            FlywheelThrottleB -= step;
            FlywheelThrottleA -= step;
        }

        //Verify and clamp
//        FlywheelThrottleA =


        motorController.setPWM(BadgerPWMProvider.FLYWHEEL_A,FlywheelThrottleA);
        motorController.setPWM(BadgerPWMProvider.FLYWHEEL_B,FlywheelThrottleB);
    }

    public void armFlywheel(){
        motorController.setPWM(BadgerPWMProvider.FLYWHEEL_A,BadgerMotorController.FLYWHEEL_PERCENT_MIN);
        motorController.setPWM(BadgerPWMProvider.FLYWHEEL_B,BadgerMotorController.FLYWHEEL_PERCENT_MIN);
    }

    public void disarmFlywheel(){
        motorController.setPWM(BadgerPWMProvider.FLYWHEEL_A,0);
        motorController.setPWM(BadgerPWMProvider.FLYWHEEL_A,0);
    }

    public void allowController(){
        if (!allowController) {
            allowController = true;
        }
    }

    public void disallowController(){
        if(allowController) {
            allowController = false;
        }
    }
    /**
     * Sets the direction of the badger's movement to forward at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveForward(float throttle) {
        motorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);
        motorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle);
    }

    /**
     * Sets the direction of the badger's movement to backwards at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackward(float throttle) {
        motorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle);
    }

    /**
     * Sets the direction of the badger's movement to spin to the right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void spinRight(int speed) {
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, speed);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, speed);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, speed);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to spin left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void spinLeft(int speed) {
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, speed);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, speed);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, speed);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to strafe left at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void strafeLeft(float throttle) {
        motorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle);
    }

    /**
     * Sets the direction of the badger's movement to strafe right at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void strafeRight(float throttle) {
        motorController.setDriveMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setDriveMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setDriveMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);
        motorController.setDriveMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, throttle);
    }

    public void STOP(){
        //KILL the Drive Motors
        motorController.stopDriveMotors();

        //Stop the flywheels
        motorController.setPWM(BadgerPWMProvider.FLYWHEEL_A, BadgerMotorController.FLYWHEEL_PERCENT_MIN);
        motorController.setPWM(BadgerPWMProvider.FLYWHEEL_B, BadgerMotorController.FLYWHEEL_PERCENT_MIN);
    }

    public void setDriveMotor(Pin DirPin, Pin PWMPin, int direction, float throttle){
        motorController.setDriveMotorDirection(DirPin, direction);
        motorController.setDriveMotorSpeed(PWMPin, throttle);
    }

    public void setConveyor(int direction, float throttle){
        int opposingDir = direction==0? 1 : 0;
        motorController.setDriveMotorDirection(RPI.CONVEYOR_A,direction);
        motorController.setDriveMotorDirection(RPI.CONVEYOR_B,opposingDir);

        motorController.setDriveMotorSpeed(BadgerPWMProvider.CONVEYOR_A,throttle);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.CONVEYOR_B,throttle);
    }

    public void setFlywheelSpeed(float speed){
        float range = (BadgerMotorController.FLYWHEEL_PERCENT_MAX -BadgerMotorController.FLYWHEEL_PERCENT_MIN);
        float throttle = range*speed/100.f;

        motorController.setPWM(BadgerPWMProvider.FLYWHEEL_A,throttle);
        motorController.setPWM(BadgerPWMProvider.FLYWHEEL_B,throttle);
    }

    public void sendMessageToDesktop(String msg){
        networkServer.SendMessage(new BaseMsg(msg));
    }

    public void sendDebugMessageToDesktop(String msg){
        StatusMessage message = new StatusMessage(msg);
        message.appendDeviceStatus(this);

        networkServer.SendMessage(message);
    }

    public void sendCriticalMessageToDesktop(String msg, Exception except){
        networkServer.SendMessage(new ErrorMessage(msg,except));
    }

    /**
     * Sets the direction of the badger's movement to diagonally forward and to the right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveForwardRight(int speed) {
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, speed);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, 0);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, 0);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to diagonally backwards and to the left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackwardLeft(int speed) {
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, speed);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, 0);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, 0);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to diagonally forward and to the left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveFowardLeft(int speed) {
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, 0);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, speed);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, speed);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, 0);
    }

    /**
     * Sets the direction of the badger's movement to backwards and to the right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackwardRight(int speed) {
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setDriveMotorDirection(BadgerPWMProvider.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_LEFT, 0);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_LEFT, speed);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_FRONT_RIGHT, speed);
        motorController.setDriveMotorSpeed(BadgerPWMProvider.DRIVE_BACK_RIGHT, 0);
    }
}
