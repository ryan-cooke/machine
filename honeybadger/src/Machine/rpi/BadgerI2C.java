/**
 * Created by alexs on 2016-11-19.
 */
package Machine.rpi;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

/***
 * Simple class to manage the I2C communication with the PCA9685.
 */
public class BadgerI2C {

    /**
     * Array of each of the PCA9685 Outputs
     */
    private GpioPinPwmOutput[] PWMOutputs;
    /**
     * Object that represents the PCA9685. Is used mostly to set PWM of it's pins
     */
    private PCA9685GpioProvider provider;

    /**
     * Absolute max ON PWN value. Essentially caps PWM signal for drive motors at 80%
     */
    private final int MaxONPWM = 3275;

    /**
     * Intiializes the BadgerI2C class and creates it's provider
     * @throws Exception
     */
    public BadgerI2C() throws Exception{
        I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
        provider = new PCA9685GpioProvider(bus, 0x40);
        this.provisionPwmOutputs();
        this.provider.reset();
    }

    /**
     * Closes the bus for I2C comms and effectively shuts down communication with the PCA9685
     */
    public void shutdown() {
        provider.shutdown();
    }

    /**
     * Provisions the PWM Pins and gives them descriptive names, because why not
     */
    private void provisionPwmOutputs() {
        GpioController gpio = GpioFactory.getInstance();
        GpioPinPwmOutput[] myOutputs;
        myOutputs = new GpioPinPwmOutput[]{
                gpio.provisionPwmOutputPin(provider, PCA9685Pin.PWM_00, "Front Left - FL-H"),
                gpio.provisionPwmOutputPin(provider, PCA9685Pin.PWM_01, "Front Right - FR-H"),
                gpio.provisionPwmOutputPin(provider, PCA9685Pin.PWM_02, "Back Left - BL-H"),
                gpio.provisionPwmOutputPin(provider, PCA9685Pin.PWM_03, "Back Right - BR-H"),
                gpio.provisionPwmOutputPin(provider, PCA9685Pin.PWM_04, "Conveyor 1 - CV2"),
                gpio.provisionPwmOutputPin(provider, PCA9685Pin.PWM_05, "Conveyor 2 - CV1"),
                gpio.provisionPwmOutputPin(provider, PCA9685Pin.PWM_06, "Vacuum Roller - VAC"),
                gpio.provisionPwmOutputPin(provider, PCA9685Pin.PWM_07, "Flywheel 1 - BS1"),
                gpio.provisionPwmOutputPin(provider, PCA9685Pin.PWM_08, "Flywheel 2 - BS2"),
                gpio.provisionPwmOutputPin(provider, PCA9685Pin.PWM_09, "Climbing Arm - RND1"),
                gpio.provisionPwmOutputPin(provider, PCA9685Pin.PWM_10, "Climbing Wrist - RND2"),
                gpio.provisionPwmOutputPin(provider, PCA9685Pin.PWM_11, "Shooting aim adjust - RND3")
        };
        this.PWMOutputs = myOutputs;
    }

    /**
     * Sets the speed percentage, value from 0 to 100, for a given drive motor.
     *
     * Actual speed of the motor is maxed out at at 80% at Ryan's request (Max ON PWM Value of 3275)
     * therefore, percentage parameter is used to scale motor speed based on the Max PWM value.
     * @param pin
     * @param speed
     * @param direction
     */
    public void setDriveMotorSpeed(Pin pin, int speed, int direction) {
        //TODO: Make this function do shit when ryan confirms how the chip works.
    }


}
