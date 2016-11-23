package Machine.rpi.hw;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

/***
 * Simple class to manage the I2C communication with the PCA9685.
 */
public class BadgerMotorController{

    /**
     * Array of each of the provisioned PCA9685 PWM Outputs
     */
    private GpioPinPwmOutput[] PWMOutputs;

    /**
     * Array of each of the provisioned RaspberryPI Digital outputs
     */
    private GpioPinDigitalOutput[] DigitalOutputs;

    /**
     * Object that represents the PCA9685. Is used mostly to set PWM of it's pins
     */
    private PCA9685GpioProvider PCAprovider;

    /**
     * Object that represents the RaspberryPI. Is used mostly to set the PinState of it's pins (HIGH or LOW)
     */
    private GpioProvider RPIProvider;

    /**
     * Absolute max ON PWN value. Essentially caps PWM signal for drive motors at 80%
     */
    private static final int MaxONPWM = 3275;

    /**
     * Constant that defines integer representation of clockwise rotation
     */
    public static final int CLOCKWISE = 0;

    /**
     * Constant that defines integer representation of counter clockwise rotation
     */
    public static final int COUNTER_CLOCKWISE = 1;

    /**
     * Initializes the BadgerI2C class and creates it's provider
     * @throws Exception Yes, this may throw some exception (See random sample code from internet for more details)
     */
    @SuppressWarnings("WeakerAccess")
    public BadgerMotorController() throws Exception{
        I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
        PCAprovider = new PCA9685GpioProvider(bus, 0x40);
        RPIProvider = new RaspiGpioProvider();
        this.provisionPwmOutputs();
        this.provisionDigitalOutputs();
        this.PCAprovider.reset();
    }

    /**
     * Closes the bus for I2C comms and effectively shuts down communication with the PCA9685
     */
    public void shutdown() {
        PCAprovider.shutdown();
    }

    /**
     * Provisions the PWM Pins and gives them descriptive names, because why not
     */
    private void provisionPwmOutputs() {
        GpioController PCAGpio = GpioFactory.getInstance();
        GpioPinPwmOutput[] myOutputs;
        myOutputs = new GpioPinPwmOutput[]{
                PCAGpio.provisionPwmOutputPin(PCAprovider, PCAChip.DRIVE_FRONT_LEFT, "Front Left - FL-H"),
                PCAGpio.provisionPwmOutputPin(PCAprovider, PCAChip.DRIVE_FRONT_RIGHT, "Front Right - FR-H"),
                PCAGpio.provisionPwmOutputPin(PCAprovider, PCAChip.DRIVE_BACK_LEFT, "Back Left - BL-H"),
                PCAGpio.provisionPwmOutputPin(PCAprovider, PCAChip.DRIVE_BACK_RIGHT, "Back Right - BR-H"),
                PCAGpio.provisionPwmOutputPin(PCAprovider, PCAChip.CONVEYOR_A, "Conveyor A - CV1"),
                PCAGpio.provisionPwmOutputPin(PCAprovider, PCAChip.CONVEYOR_B, "Conveyor B - CV2"),
                PCAGpio.provisionPwmOutputPin(PCAprovider, PCAChip.VACUUM_ROLLER, "Vacuum Roller - VAC"),
                PCAGpio.provisionPwmOutputPin(PCAprovider, PCAChip.FLYWHEEL_A, "Flywheel A - BS1"),
                PCAGpio.provisionPwmOutputPin(PCAprovider, PCAChip.FLYWHEEL_B, "Flywheel B - BS2"),
                PCAGpio.provisionPwmOutputPin(PCAprovider, PCAChip.CLIMBING_ARM, "Climbing Arm - RND1"),
                PCAGpio.provisionPwmOutputPin(PCAprovider, PCAChip.CLIMBING_WRIST, "Climbing Wrist - RND2"),
                PCAGpio.provisionPwmOutputPin(PCAprovider, PCAChip.SHOOTING_AIM_ADJUST, "Shooting aim adjust - RND3")
        };
        this.PWMOutputs = myOutputs;
    }

    /**
     * Provisions the Digital Pins on the RaspberryPI and gives them descriptive names, because why not
     */
    private void provisionDigitalOutputs() {
        GpioController RPIGpio = GpioFactory.getInstance();
        GpioPinDigitalOutput[] myOutputs;
        myOutputs = new GpioPinDigitalOutput[]{
                RPIGpio.provisionDigitalOutputPin(RPIProvider, RPI.DRIVE_FRONT_LEFT, "Front Left - FL-L"),
                RPIGpio.provisionDigitalOutputPin(RPIProvider, RPI.DRIVE_FRONT_RIGHT, "Front Right - FR-L"),
                RPIGpio.provisionDigitalOutputPin(RPIProvider, RPI.DRIVE_BACK_LEFT, "Back Left - BL-L"),
                RPIGpio.provisionDigitalOutputPin(RPIProvider, RPI.DRIVE_BACK_RIGHT, "Back Right - BR-L")
                //TODO: Provision the rest of the digital pins
        };
        this.DigitalOutputs = myOutputs;
    }

    /**
     * Sets the speed percentage, value from 0 to 100, for a given TLE chip-controlled motor.
     *
     * Actual speed of the motor is maxed out at at 80% at Ryan's request
     * therefore, percentage parameter is used to scale motor speed based on the Max PWM value.
     * @param pin PCA9685Pin for the motor whose speed will be set
     * @param speed Speed to set the motor too, int value from 0 to 100
     */
    //TODO: TEST THIS METHOD WITH A MOTOR
    public void setTLEMotorSpeed(Pin pin, int speed) {

        if (speed < 0 || speed > 100){
            System.out.println("[BadgerMotorController.setDriveSpeed] Speed percentage out of range. Must be INT between 0 and " +
                    "100");
            return;
        }

        //Get the scaled PWM value based on the MaxONPWM value;
        int PWMOnTime = MaxONPWM * (speed/100);
        int PWMOffTime = 4095-MaxONPWM;

        this.PCAprovider.setPwm(pin, PWMOnTime, PWMOffTime);

    }

    /**
     * Sets the direction of rotation of the motor, either BadgerMotorController.CLOCKWISE or BadgerMotorController.COUNTER_CLOCKWISE
     * @param pin RaspberryPI pin that controlling direction of the desired motor. EX: RPI.DRIVE_FRONT_LEFT
     * @param direction Direction in which the motor will rotate. EX: BadgerMotorController.CLOCKWISE or BadgerMotorController.COUNTER_CLOCKWISE
     */
    //TODO: TEST THIS METHOD WITH A MOTOR
    public void setTLEMotorDirection(Pin pin, int direction) {
        if (direction == CLOCKWISE)
            this.RPIProvider.setState(pin, PinState.LOW);
        else if (direction == COUNTER_CLOCKWISE)
            this.RPIProvider.setState(pin, PinState.HIGH);
        else
            System.out.println("[BadgerMotorController.setMotorDirection] Invalid motor direction given");
    }


}
