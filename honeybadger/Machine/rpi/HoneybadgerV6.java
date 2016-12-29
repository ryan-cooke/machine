package Machine.rpi;

import Machine.Common.Network.BaseMsg;
import Machine.rpi.hw.BadgerMotorController;
import Machine.rpi.hw.BadgerPWMProvider;
import Machine.rpi.hw.RPI;

import com.pi4j.io.gpio.Pin;

import java.io.PrintWriter;
import java.io.StringWriter;

import static Machine.Common.Utils.Log;

/**
 * Contains methods that represent all the physical actions the badger can execute
 */
public class HoneybadgerV6 {
    private static HoneybadgerV6 Singleton;

    //Interact with the hardware
    private BadgerMotorController motorController;

    //Send and receive messages over the network being hosted on the pi
    private BadgerNetworkServer networkServer;

    /**
     * Makes a new Honeybadger (this is version 6). Guaranteed not to give a shit
     * @throws Exception But honey badger don't give a shit
     */
    private HoneybadgerV6() throws Exception {
        motorController = new BadgerMotorController();
        networkServer = new BadgerNetworkServer(this);
        Log("Made the BadgerV6");
    }

    public BadgerNetworkServer getNetworkServer(){
        return networkServer;
    }

    public BadgerMotorController getMotorController() { return motorController; }

    public static HoneybadgerV6 getInstance() throws Exception{
        if(Singleton==null){
            Singleton = new HoneybadgerV6();
        }

        return Singleton;
    }

    private void shutdown() {
        this.motorController.shutdown();
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
        motorController.STOP(BadgerPWMProvider.DRIVE_FRONT_LEFT);
        motorController.STOP(BadgerPWMProvider.DRIVE_BACK_LEFT);
        motorController.STOP(BadgerPWMProvider.DRIVE_FRONT_RIGHT);
        motorController.STOP(BadgerPWMProvider.DRIVE_BACK_RIGHT);

        //Stop the flywheels
        motorController.setPWM(BadgerPWMProvider.FLYWHEEL_A,BadgerPWMProvider.FLYWHEEL_PWM_MIN);
        motorController.setPWM(BadgerPWMProvider.FLYWHEEL_B,BadgerPWMProvider.FLYWHEEL_PWM_MIN);
    }

    public void SetMotor(Pin DirPin, Pin PWMPin, int direction, float throttle){
        motorController.setDriveMotorDirection(DirPin, direction);
        motorController.setDriveMotorSpeed(PWMPin, throttle);
    }

    public void setFlywheelSpeed(float speed){
        motorController.setFlywheelSpeed(BadgerPWMProvider.FLYWHEEL_A,speed);
        motorController.setFlywheelSpeed(BadgerPWMProvider.FLYWHEEL_B,speed);
    }

    public void sendMessageToDesktop(String msg){
        networkServer.SendMessage(new BaseMsg(msg));
    }

    public void sendDebugMessageToDesktop(String msg){
        //TODO: Implement
    }

    public void sendCriticalMessageToDesktop(String msg, Exception except){
        StringWriter errors = new StringWriter();
        except.printStackTrace(new PrintWriter(errors));
        String stackTrace = errors.toString();

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
