package Machine.rpi.hw;

import Machine.Common.Constants;
import Machine.Common.Utils;
import Machine.rpi.HoneybadgerV6;

import com.pi4j.io.gpio.*;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

import static Machine.Common.Utils.ErrorLog;
import static Machine.Common.Utils.Log;

/***
 * Simple class to manage the I2C communication with the PCA9685.
 */
public class BadgerMotorController {
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

    /**
     * Object that wraps the interface to Hardware GPIO pins.
     */
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
     * The ID of the servo controlling the angle of the cannon
     */
    public static final byte FLYWHEEL_SERVO_ID = BadgerSmartServoProvider.SERVO_A;

    /**
     * Lowest percent for Flywheel PWM to be set.
     * Calling setPWM on the flywheel motors with this value will arm them.
     * Subsequent calls to setPWM  with this value will stop the motors from spinning.
     */
    public static final float FLYWHEEL_PERCENT_MIN = 10.f;

    /**
     * Highest percent for Flywheel PWM to be set.
     * DANGER! NEVER SET AT THIS VALUE IMMEDIATELY IN ONE SHOT!
     */
    public static final float FLYWHEEL_PERCENT_MAX = 90.f;

    /**
     * Lowest angle we can do in terms of the servo position
     * TODO: SET EMPIRICALLY
     */
    public static final int FLYWHEEL_ANGLE_LOWEST = 100;

    /**
     * Highest angle we can do
     * TODO: SET EMPIRICALLY
     */
    public static final int FLYWHEEL_ANGLE_HIGHEST = 420;

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

        //@foxtrot94: added for quick iteration on desktop
        if(Constants.getActivePlatform()== Constants.PLATFORM.MOCK_PI){
            Log("Abstracting hardware for working with Mock Pi");
            return;
        }

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
            e.printStackTrace();
            String message = "ERROR: THE EXPECTED DEVICES WERE NOT AVAILABLE";
            ErrorLog(message);
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

                GPIO.provisionPwmOutputPin(PWMProvider, BadgerPWMProvider.CLIMBING_MOTOR,"Climbing Motor - CM"),
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
     * Actual speed of the motor is limited to only 80% of the maximum output to avoid passing an overvoltage
     * therefore, percentage parameter is used to scale motor speed based on the Max PWM value.
     * @param pin PCA9685Pin for the motor whose speed will be set
     * @param speedPercent Speed to set the motor too, int value from 0 to 100
     */
    public void setDriveMotorSpeed(Pin pin, float speedPercent) {
        if(!IsReady||pin==null){
            return;
        }

        if (speedPercent < 0 || speedPercent > 100){
            Log("[BadgerMotorController.setDriveSpeed] Speed percentage out of range. Clamping to 0.f or 100.f");
            speedPercent = Utils.Clamp(speedPercent,0.f,100.f);
        }

        float percent = speedPercent/100.f;
        int PWMtime = (int)(DRIVE_PWM_MAX - ((percent)* DRIVE_PWM_MAX));

        //Overdrive option
        if(DriveMotorLimiting){
            PWMtime += DRIVE_PWM_MIN;
        }

        this.PWMProvider.setPwm(pin, 0, PWMtime);
    }

    /**
     * Automatically stop all known Drive Motors hooked up to the TLE H-Bridge chip
     */
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
    public void setDriveMotorDirection(Pin pin, int direction) {
        if(!IsReady || pin==null){
            return;
        }

        if (direction == CLOCKWISE)
            this.RPIProvider.setState(pin, PinState.LOW);
        else if (direction == COUNTER_CLOCKWISE)
            this.RPIProvider.setState(pin, PinState.HIGH);
        else
            Log("[BadgerMotorController.setMotorDirection] Invalid motor direction given");
    }

    /**
     * Set a percentage value on a PWM Provider pin instance. The percentage is a raw value
     * @param pin The pin object, as seen by the PWM Provider
     * @param value Percentage between 0.0% and 100% to which to set the value
     */
    public void setPWM(Pin pin, float value){
        if(!IsReady || pin==null){
            return;
        }

        float scaledThrottle = BadgerPWMProvider.PWM_MAX * (value / 100.f);
        int PWMOffTime = (int)scaledThrottle;
        PWMOffTime = PWMOffTime<1? 1 : PWMOffTime;

        this.PWMProvider.setPwm(pin, 0, PWMOffTime);
    }

    /**
     * Send a position command to a known servo over serial UART
     * @param servoID The Unique ID of the servo being referred to
     * @param position A position, between 0 and 1023, to which to set the servo
     */
    public void setServoPosition(int servoID, int position){
        if(!IsReady){
            return;
        }
        this.SerialServo.SetPosition((byte)(servoID&0xFF),250,position);
    }

    /**
     * Set an absolute time for a PWM signal to fire
     * @param pin The pin object, as seen by the PWM Provider
     * @param val Time in microseconds
     */
    public void setAbsPWM(Pin pin, int val){
        if(!IsReady||pin==null){
            return;
        }
        this.PWMProvider.setPwm(pin,val);
    }

    /**
     * Sets the overdrive value on the motor driven by the TLE H-Bridge
     * WARNING: By using overdrive for extended periods of time, there is a risk of irreversibly damaging the motors
     * @param active Indicate whether overdrive should be active or not.
     */
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
