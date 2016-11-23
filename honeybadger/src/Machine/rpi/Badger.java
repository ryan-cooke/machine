package Machine.rpi;

/**
 * Contains methods that represent all the physical actions the badger can execute
 */
public class Badger implements PCA, RPI {

    private BadgerMotorController motorController;

    public Badger() throws Exception {
        motorController = new BadgerMotorController();
    }

    /**
     * Sets the direction of the badger's movement to forward at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveForward(int speed) {
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_RIGHT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to backwards at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackward(int speed) {
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_RIGHT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to spin to the right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void spinRight(int speed) {
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_RIGHT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to spin left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void spinLeft(int speed) {
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_RIGHT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to strafe left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void strafeLeft(int speed) {
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_RIGHT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to strafe right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void strafeRight(int speed) {
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_RIGHT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to diagonally forward and to the right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveForwardRight(int speed) {
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_LEFT, 0);
        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_RIGHT, 0);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to diagonally backwards and to the left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackwardLeft(int speed) {
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_LEFT, 0);
        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_RIGHT, 0);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_RIGHT, speed);
    }

    /**
     * Sets the direction of the badger's movement to diagonally forward and to the left at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveFowardLeft(int speed) {
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_LEFT, BadgerMotorController.CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_RIGHT, BadgerMotorController.COUNTER_CLOCKWISE);

        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_LEFT, 0);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_RIGHT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_RIGHT, 0);
    }

    /**
     * Sets the direction of the badger's movement to backwards and to the right at the given speed percentage
     * @param speed Int value between 0 (no motion) and 100 (max speed)
     */
    public void moveBackwardRight(int speed) {
        motorController.setTLEMotorDirection(PCA.DRIVE_BACK_LEFT, BadgerMotorController.COUNTER_CLOCKWISE);
        motorController.setTLEMotorDirection(PCA.DRIVE_FRONT_RIGHT, BadgerMotorController.CLOCKWISE);

        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_LEFT, 0);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_LEFT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_FRONT_RIGHT, speed);
        motorController.setTLEMotorSpeed(PCA.DRIVE_BACK_RIGHT, 0);
    }
}
