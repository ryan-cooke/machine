package Machine.Common.Network;

import Machine.Common.Utils;
import Machine.desktop.Controller;
import Machine.rpi.HoneybadgerV6;

import java.io.Serializable;
import java.util.HashMap;

import static Machine.Common.Utils.Log;
import Machine.Common.Utils.Button;

/**
 * Handles everything related to networking the controller
 */
public class ControllerMessage extends BaseMsg implements Serializable {

    public int buttonsPressed; // Number of buttons currently pressed

    public HashMap<Button, Boolean> buttons; // The 14 buttons pressed if true

    public double leftThumbstickRotation;
    public char leftThumbstickDirection; // N, E, S, W for directions.
    public double rightThumbstickRotation;
    public char rightThumbstickDirection;

    public double leftThumbstickMagnitude;
    public double rightThumbstickMagnitude;

    public double leftTriggerMagnitude;
    public double rightTriggerMagnitude;

    public Utils.Vector2D RightThumbstick;
    public Utils.Vector2D LeftThumbstick;

    public void Initialize(){
        buttons = new HashMap<>();
        buttons.put(Button.A, false);
        buttons.put(Button.B, false);
        buttons.put(Button.X, false);
        buttons.put(Button.Y, false);
        buttons.put(Button.BACK, false);
        buttons.put(Button.START, false);
        buttons.put(Button.RBUMPER, false);
        buttons.put(Button.LBUMPER, false);
        buttons.put(Button.RTHUMB, false);
        buttons.put(Button.LTHUMB, false);
        buttons.put(Button.NDPAD, false);
        buttons.put(Button.EDPAD, false);
        buttons.put(Button.SDPAD, false);
        buttons.put(Button.WDPAD, false);

        buttonsPressed = 0;
        leftThumbstickDirection = 'Z';
        rightThumbstickDirection = 'Z';
        leftThumbstickMagnitude = 0.0;
        rightThumbstickMagnitude = 0.0;
        rightThumbstickRotation = 0.0;
        leftThumbstickRotation =0.0;

        leftTriggerMagnitude = 0.0;
        leftTriggerMagnitude = 0.0;
    }

    public ControllerMessage(){
        //Empty constructor
    }

    public ControllerMessage(
            String payload,
            HashMap<Button,Boolean> buttons,
            int buttonsPressed,
            char leftThumbstickDirection,
            char rightThumbstickDirection,
            double rightThumbstickRotation,
            double leftThumbstickRotation,
            double leftThumbstickMagnitude,
            double rightThumbstickMagnitude,
            double leftTriggerMagnitude,
            double rightTriggerMagnitude
    ){
        super(payload);
        this.buttons = buttons;
        this.buttonsPressed = buttonsPressed;
        this.leftThumbstickDirection = leftThumbstickDirection;
        this.rightThumbstickDirection = rightThumbstickDirection;
        this.leftThumbstickRotation = leftThumbstickRotation;
        this.rightThumbstickRotation = rightThumbstickRotation;
        this.leftThumbstickMagnitude = leftThumbstickMagnitude;
        this.rightThumbstickMagnitude = rightThumbstickMagnitude;
        this.leftTriggerMagnitude = leftTriggerMagnitude;
        this.rightTriggerMagnitude = rightTriggerMagnitude;
    }

    public ControllerMessage(ControllerMessage that){
        this.buttons = new HashMap<>(that.buttons);
        this.buttonsPressed = that.buttonsPressed;
        this.leftThumbstickDirection = that.leftThumbstickDirection;
        this.rightThumbstickDirection = that.rightThumbstickDirection;
        this.leftThumbstickMagnitude = that.leftThumbstickMagnitude;
        this.rightThumbstickMagnitude = that.rightThumbstickMagnitude;
        this.leftThumbstickRotation = that.leftThumbstickRotation;
        this.rightThumbstickRotation = that.rightThumbstickRotation;
        this.leftTriggerMagnitude = that.leftTriggerMagnitude;
        this.rightTriggerMagnitude = that.rightTriggerMagnitude;
    }

    public int getButtonsPressed() {
        return buttonsPressed;
    }

    public void setButtonsPressed(int buttonsPressed) {
        this.buttonsPressed = buttonsPressed;
    }

    public void setLeftThumbDir(char leftThumbDir) {
        this.leftThumbstickDirection = leftThumbDir;
    }

    public void setRightThumbstickDirection(char rightThumbstickDirection) {
        this.rightThumbstickDirection = rightThumbstickDirection;
    }

    @Override
    public void Execute(Object context) {
        HoneybadgerV6 badger = (HoneybadgerV6) context;
        if (badger == null) {
            Log(this.toString());
            return;
        }

        //If the badger wasn't null, do actions dependent on the object
        if (buttons.get(Button.START)){
            badger.listenToController(true);
        }

        if(badger.isListeningToController()) {
            badger.updateMovement(leftThumbstickDirection, (float) leftThumbstickMagnitude * 100.f);
            badger.updateRotation(rightThumbstickDirection, (int) rightThumbstickMagnitude * 100);
            badger.updateFlywheel((float) rightTriggerMagnitude);
            badger.updateConveyor((float) leftTriggerMagnitude);
            badger.handleButtonPress(buttons);
        }
    }

    @Override
    public String getPayload() {
        return "ControllerMessage";
    }

    public String toString(){
        //Return the Controller state
        StringBuffer buffer = new StringBuffer();
        for(Button aButton : Button.values()){
            if(!this.buttons.containsKey(aButton)){
                continue;
            }

            boolean value = this.buttons.get(aButton);
            buffer.append(aButton.toString());
            buffer.append("=");
            buffer.append(value?"1":"0");
            buffer.append("; ");
        }
        buffer.append("\n");

        //Triggers
        buffer.append("Triggers: ");
        buffer.append("L=");
        buffer.append(this.leftTriggerMagnitude);
        buffer.append(" | ");
        buffer.append("R=");
        buffer.append(this.rightTriggerMagnitude);
        buffer.append("\n");

        //Thumbsticks
        buffer.append("Thumbsticks\n");
        buffer.append("Left: Mag=");
        buffer.append(this.leftThumbstickMagnitude);
        buffer.append(" | Dir=");
        buffer.append(this.leftThumbstickDirection);
        buffer.append(" | Rot=");
        buffer.append(this.leftThumbstickRotation);
        buffer.append("\n");

        buffer.append("Right: Mag=");
        buffer.append(this.rightThumbstickMagnitude);
        buffer.append(" | Dir=");
        buffer.append(this.rightThumbstickDirection);
        buffer.append(" | Rot=");
        buffer.append(this.rightThumbstickRotation);
        buffer.append("\n");

        return buffer.toString();
    }
}
