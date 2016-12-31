package Machine.rpi.hw;

import Machine.rpi.HoneybadgerV6;
import Machine.rpi.NetworkDebuggable;
import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

import static Machine.Common.Utils.Log;

/***
 * Simple class to manage the I2C communication with the PCA9685.
 */
public class BadgerMotorController extends NetworkDebuggable{
    /**
     * Boolean to check that the expected RPi hardware is present and ready
     */
    private boolean IsReady;

    /**
     * Determines whether to limit the drive motor speed to "safe" voltages
     */
    private boolean DriveMotorLimiting;

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
    private BadgerPWMProvider PWMProvider;

    /**
     * The Daisy-chained smart servo provider.
     */
    private BadgerSmartServoProvider SerialServo;

    /**
     * Object that represents the RaspberryPI. Is used mostly to set the PinState of it's pins (HIGH or LOW)
     */
    private GpioProvider RPIProvider;


    private GpioController GPIO;

    /**
     * Reference MAX PWM for the Drive Motors
     */
    private static final int DRIVE_PWM_MAX = 3245;

    /**
     * Reference MIN PWM for the Drive motors
     * Any value lower than this will set the motors to OVERDRIVE
     */
    private static final int DRIVE_PWM_MIN = 850;

    /**
     * Lowest percent for Flywheel PWM to be set.
     * Calling setPWM on the flywheel motors with this value will arm them.
     * Subsequent calls to setPWM  with this value will stop the motors from spinning.
     */
    public static final int FLYWHEEL_PERCENT_MIN = 10;

    /**
     * Highest percent for Flywheel PWM to be set.
     * DANGER! NEVER SET AT THIS VALUE IMMEDIATELY IN ONE SHOT!
     */
    public static final int FLYWHEEL_PERCENT_MAX = 90;

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
     */
    public BadgerMotorController(){
        IsReady = false;
        DriveMotorLimiting = true;
        try {
            Log("Enabling I2C Bus");
            I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);

            Log("Enabling GPIO");
            this.GPIO = GpioFactory.getInstance();

            Log("Locking GPIO Provider");
            RPIProvider = GpioFactory.getDefaultProvider();

            Log("Creating BadgerPWM Provider");
            PWMProvider = new BadgerPWMProvider(bus, 0x40);

            Log("Provisioning Outputs");
            this.provisionPwmOutputs();
            this.provisionDigitalOutputs();
            PWMProvider.reset();

            SerialServo = new BadgerSmartServoProvider();

            IsReady = true;
            Log("Badger Motor Controller Ready");
        }
        catch (Exception e){
            //TODO: Should be a harder warning
            e.printStackTrace();
            String message = "ERROR: THE EXPECTED DEVICES WERE NOT AVAILABLE";
            HoneybadgerV6.getInstance().sendCriticalMessageToDesktop(message,e);
            Log(message);
        }
    }

    /**
     * Closes the bus for I2C comms and effectively shuts down communication with the PCA9685
     */
    public void shutdown() {
        PWMProvider.shutdown();
    }

    /**
     * Provisions the PWM Pins and gives them descriptive names, because why not
     */
    private void provisionPwmOutputs() {
        this.PWMOutputs = new GpioPinPwmOutput[]{
                GPIO.provisionPwmOutputPin(PWMProvider, BadgerPWMProvider.DRIVE_FRONT_LEFT, "Front Left - FL-H"),
                GPIO.provisionPwmOutputPin(PWMProvider, BadgerPWMProvider.DRIVE_FRONT_RIGHT, "Front Right - FR-H"),
                GPIO.provisionPwmOutputPin(PWMProvider, BadgerPWMProvider.DRIVE_BACK_LEFT, "Back Left - BL-H"),
                GPIO.provisionPwmOutputPin(PWMProvider, BadgerPWMProvider.DRIVE_BACK_RIGHT, "Back Right - BR-H"),

                GPIO.provisionPwmOutputPin(PWMProvider, BadgerPWMProvider.CONVEYOR_A, "Conveyor A - CV1"),
                GPIO.provisionPwmOutputPin(PWMProvider, BadgerPWMProvider.CONVEYOR_B, "Conveyor B - CV2"),

                GPIO.provisionPwmOutputPin(PWMProvider, BadgerPWMProvider.VACUUM_ROLLER, "Vacuum Roller - VAC"),
                GPIO.provisionPwmOutputPin(PWMProvider, BadgerPWMProvider.FLYWHEEL_A, "Flywheel A - BS1"),
                GPIO.provisionPwmOutputPin(PWMProvider, BadgerPWMProvider.FLYWHEEL_B, "Flywheel B - BS2"),
//                GPIO.provisionPwmOutputPin(PWMProvider, BadgerPWMProvider.CLIMBING_ARM, "Climbing Arm - RND1"),
//                GPIO.provisionPwmOutputPin(PWMProvider, BadgerPWMProvider.CLIMBING_WRIST, "Climbing Wrist - RND2"),
//                GPIO.provisionPwmOutputPin(PWMProvider, BadgerPWMProvider.SHOOTING_AIM_ADJUST, "Shooting aim adjust - RND3")
        };
    }

    /**
     * Provisions the Digital Pins on the RaspberryPI and gives them descriptive names, because why not
     */
    private void provisionDigitalOutputs() {
        GpioPinDigitalOutput[] myOutputs;
        myOutputs = new GpioPinDigitalOutput[]{
                GPIO.provisionDigitalOutputPin(RPIProvider, RPI.DRIVE_FRONT_LEFT, "Front Left - FL-L"),
                GPIO.provisionDigitalOutputPin(RPIProvider, RPI.DRIVE_FRONT_RIGHT, "Front Right - FR-L"),
                GPIO.provisionDigitalOutputPin(RPIProvider, RPI.DRIVE_BACK_LEFT, "Back Left - BL-L"),
                GPIO.provisionDigitalOutputPin(RPIProvider, RPI.DRIVE_BACK_RIGHT, "Back Right - BR-L"),

                GPIO.provisionDigitalOutputPin(RPIProvider, RPI.CONVEYOR_A, "Conveyor A"),
                GPIO.provisionDigitalOutputPin(RPIProvider, RPI.CONVEYOR_B, "Conveyor B"),
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
     * @param speedPercent Speed to set the motor too, int value from 0 to 100
     */
    public void setDriveMotorSpeed(Pin pin, float speedPercent) {
        if(!IsReady){
            return;
        }

        if (speedPercent < 0 || speedPercent > 100){
            Log("[BadgerMotorController.setDriveSpeed] Speed percentage out of range. Must be INT between 0 and " +
                    "100");
            return;
        }

        //TODO: REVIEW!!!
        float percent = speedPercent/100.f;
        int PWMtime = (int)(DRIVE_PWM_MAX - ((percent)* DRIVE_PWM_MAX));
        //Overdrive option
        if(DriveMotorLimiting){
            PWMtime += DRIVE_PWM_MIN;
        }

        this.PWMProvider.setPwm(pin, 0, PWMtime);
    }

    public void stopDriveMotors(){
        for(Pin motor : BadgerPWMProvider.DriveMotors) {
            this.PWMProvider.setAlwaysOn(motor);
        }
    }

    /**
     * Sets the direction of rotation of the motor, either BadgerMotorController.CLOCKWISE or BadgerMotorController.COUNTER_CLOCKWISE
     * @param pin RaspberryPI pin that controlling direction of the desired motor. EX: RPI.DRIVE_FRONT_LEFT
     * @param direction Direction in which the motor will rotate. EX: BadgerMotorController.CLOCKWISE or BadgerMotorController.COUNTER_CLOCKWISE
     */
    //TODO: TEST THIS METHOD WITH A MOTOR
    public void setDriveMotorDirection(Pin pin, int direction) {
        if(!IsReady){
            return;
        }

        if (direction == CLOCKWISE)
            this.RPIProvider.setState(pin, PinState.LOW);
        else if (direction == COUNTER_CLOCKWISE)
            this.RPIProvider.setState(pin, PinState.HIGH);
        else
            System.out.println("[BadgerMotorController.setMotorDirection] Invalid motor direction given");
    }

    public void setPWM(Pin pin, float value){
        if(!IsReady){
            return;
        }

        float scaledThrottle = BadgerPWMProvider.PWM_MAX * (value / 100.f);
        int PWMOffTime = (int)scaledThrottle;
        PWMOffTime = PWMOffTime<1? 1 : PWMOffTime;

        SendDebugMessage(String.format("PWM:%s - value: %f",pin.getName(),value));
        this.PWMProvider.setPwm(pin, 0, PWMOffTime);
    }

    public void setServoPosition(int servoID, int position){
        if(!IsReady){
            return;
        }
        this.SerialServo.SetPosition((byte)(servoID&0xFF),250,position);
    }

    public void setAbsPWM(Pin pin, int val){
        if(!IsReady){
            return;
        }
        this.PWMProvider.setPwm(pin,val);
    }

    public void setOverdrive(boolean active){
        DriveMotorLimiting = !active;
    }

    public Pin getPWMPin(int num){
        return PWMProvider.getPinByNumber(num);
    }

    public Pin getPWMPin(String str){
        return PWMProvider.getPinByName(str);
    }

    public Pin getGPIOPin(int num){
        return RPI.getPinByStandardNumber(num);
    }

    public Pin getGPIOPin(String str){
        return RPI.getPinByName(str);
    }
}
