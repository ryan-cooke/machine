package Machine.rpi;

import Machine.rpi.hw.BadgerMotorController;
import Machine.rpi.hw.PCAChip;
import Machine.rpi.hw.RPI;

import com.pi4j.io.gpio.Pin;

import static Machine.Common.Utils.Log;

/**
 * Contains methods that represent all the physical actions the badger can execute
 */
public class Badger {
    private static Badger Singleton;

    //Interact with the hardware
    private BadgerMotorController motorController;

    //Send and receive messages over the network being hosted on the pi
    private BadgerNetworkServer networkServer;

    /**
     * Makes a new Badger, guaranteed not to give a shit
     * @throws Exception But honey badger don't give a shit
     */
    private Badger() throws Exception {
        motorController = new BadgerMotorController();
        networkServer = new BadgerNetworkServer(this);
        Log("Made the BadgerV6");
    }

    public BadgerNetworkServer getNetworkServer(){
        return networkServer;
    }

    public static Badger getInstance() throws Exception{
        if(Singleton==null){
            Singleton = new Badger();
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
        motorController.setTLEMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_LEFT, throttle);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_LEFT, throttle);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_RIGHT, throttle);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_RIGHT, throttle);
    }

    public void STOP(){
        motorController.STOP(PCAChip.DRIVE_FRONT_LEFT);
        motorController.STOP(PCAChip.DRIVE_BACK_LEFT);
        motorController.STOP(PCAChip.DRIVE_FRONT_RIGHT);
        motorController.STOP(PCAChip.DRIVE_BACK_RIGHT);
    }

    /**
     * Sets the direction of the badger's movement to backwards at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackward(float throttle) {
        motorController.setTLEMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_LEFT, throttle);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_LEFT, throttle);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_RIGHT, throttle);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_RIGHT, throttle);
    }

    /**
     * Sets the direction of the badger's movement to spin to the right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void spinRight(int speed) {
        motorController.setTLEMotorDirection(PCAChip.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCAChip.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCAChip.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCAChip.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_LEFT, speed);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_LEFT, speed);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_RIGHT, speed);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to spin left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void spinLeft(int speed) {
        motorController.setTLEMotorDirection(PCAChip.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCAChip.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCAChip.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCAChip.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_LEFT, speed);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_LEFT, speed);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_RIGHT, speed);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to strafe left at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void strafeLeft(float throttle) {
        motorController.setTLEMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_LEFT, throttle);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_LEFT, throttle);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_RIGHT, throttle);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_RIGHT, throttle);
    }

    /**
     * Sets the direction of the badger's movement to strafe right at the given speed percentage
     * @param throttle Int value between 0 (no motion) and 100 (max speed)
     */
    public void strafeRight(float throttle) {
        motorController.setTLEMotorDirection(RPI.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(RPI.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(RPI.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(RPI.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_LEFT, throttle);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_LEFT, throttle);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_RIGHT, throttle);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_RIGHT, throttle);
    }

    public void SetMotor(Pin DirPin, Pin PWMPin, int direction, float throttle){
        motorController.setTLEMotorDirection(DirPin, direction);
        motorController.setTLEMotorSpeed(PWMPin, throttle);
    }

    /**
     * Sets the direction of the badger's movement to diagonally forward and to the right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveForwardRight(int speed) {
        motorController.setTLEMotorDirection(PCAChip.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCAChip.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_LEFT, speed);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_LEFT, 0);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_RIGHT, 0);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to diagonally backwards and to the left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackwardLeft(int speed) {
        motorController.setTLEMotorDirection(PCAChip.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCAChip.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_LEFT, speed);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_LEFT, 0);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_RIGHT, 0);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to diagonally forward and to the left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveFowardLeft(int speed) {
        motorController.setTLEMotorDirection(PCAChip.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCAChip.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_LEFT, 0);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_LEFT, speed);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_RIGHT, speed);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_RIGHT, 0);
    }

    /**
     * Sets the direction of the badger's movement to backwards and to the right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackwardRight(int speed) {
        motorController.setTLEMotorDirection(PCAChip.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCAChip.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_LEFT, 0);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_LEFT, speed);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_FRONT_RIGHT, speed);
        motorController.setTLEMotorSpeed(PCAChip.DRIVE_BACK_RIGHT, 0);
    }
}
