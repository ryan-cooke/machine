package Machine.rpi.hw;

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

    private BadgerSmartServoProvider SerialServo;

    /**
     * Object that represents the RaspberryPI. Is used mostly to set the PinState of it's pins (HIGH or LOW)
     */
    private GpioProvider RPIProvider;

    private GpioController GPIO;

    /**
     * Absolute max ON PWN value. Essentially caps PWM signal for drive motors at 80%
     */
    private static final int DriveMaxPWM = 3245;

    /**
     * Absolute max ON PWN value. Essentially caps PWM signal for drive motors at 80%
     */
    private static final int DriveMinPWM = 850;

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

            //Log("Arming flywheels");
            //this.setPWM(BadgerPWMProvider.FLYWHEEL_A,10);
            //this.setPWM(BadgerPWMProvider.FLYWHEEL_B,10);

            SerialServo = new BadgerSmartServoProvider();

            IsReady = true;
            Log("Badger Motor Controller Ready");
        }
        catch (Exception e){
            //TODO: Should be a harder warning
            e.printStackTrace();
            System.out.println("ERROR: THE EXPECTED DEVICES WERE NOT AVAILABLE");
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
                GPIO.provisionDigitalOutputPin(RPIProvider, RPI.DRIVE_BACK_RIGHT, "Back Right - BR-L")
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

        boolean shouldMovePin = (pin==BadgerPWMProvider.DRIVE_BACK_LEFT || pin==BadgerPWMProvider.DRIVE_BACK_RIGHT
                || pin ==BadgerPWMProvider.DRIVE_FRONT_LEFT || pin==BadgerPWMProvider.DRIVE_FRONT_RIGHT);

        if(!shouldMovePin){
            return;
        }

        if (speedPercent < 0 || speedPercent > 100){
            Log("[BadgerMotorController.setDriveSpeed] Speed percentage out of range. Must be INT between 0 and " +
                    "100");
            return;
        }

        //TODO: REVIEW!!!
        float percent = speedPercent/100.f;
        int PWMtime = (int)(DriveMaxPWM - ((1-percent)*DriveMaxPWM));
        //Overdrive option
        if(DriveMotorLimiting){
            PWMtime += DriveMinPWM;
        }

        this.PWMProvider.setPwm(pin, 0, PWMtime);
    }

    public void STOP(Pin pin){
        this.PWMProvider.setAlwaysOn(pin);
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

    public void setFlywheelSpeed(Pin pin, float throttle){
        if(!IsReady){
            return;
        }
        //Get the scaled PWM value based on the MaxONPWM value;
        float scaledThrottle = 4095*throttle/100.f;
        int PWMOffTime = (int)scaledThrottle;
        int PWMOnTime = 4095-PWMOffTime;
        Log(String.format("PWM OFF: %d | PWM ON %d", PWMOffTime, PWMOnTime));

        SendDebugMessage(String.format("FLYWHEEL PWM OFF: %d | PWM ON %d", PWMOffTime, PWMOnTime));

        this.PWMProvider.setPwm(pin, PWMOnTime, PWMOffTime);
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

    public void setAbsPWM(Pin pin, int val){
        if(!IsReady){
            return;
        }
        this.PWMProvider.setPwm(pin,val);
    }

    public Pin getPWMPin(int num){
        return PWMProvider.getPinByNumber(num);
    }

    public Pin getPWMPin(String str){
        return PWMProvider.getPinByName(str);
    }

    public Pin getGPIOPin(int num){
        return null;
    }

    public Pin getGPIOPin(String str){
        return RPI.getPinByName(str);
    }

    public void setServoPosition(int servoID, int position){
        if(!IsReady){
            return;
        }
        this.SerialServo.SetPosition((byte)(servoID&0xFF),250,position);
    }
}
