package Machine.Common.Network;

import Machine.desktop.Controller;

import java.io.Serializable;
import java.util.HashMap;

import static Machine.Common.Utils.Log;

/**
 * Handles everything related to networking the controller
 */
public class ControllerMessage extends BaseMsg implements Serializable {
    public enum Button {A, B, X, Y, BACK, START, RBUMPER, LBUMPER, RTHUMB,
        LTHUMB, NDPAD, EDPAD, SDPAD, WDPAD, LTRIGGER, RTRIGGER}

    public int buttonsPressed; // Number of buttons currently pressed

    public HashMap<Button, Boolean> buttons; // The 16 buttons pressed if true

    public double leftThumbstickDirection;
    public char leftThumbDir; // N, E, S, W for directions.
    public double rightThumb;
    public char rightThumbstickDirection;

    public double leftThumbstickMagnitude;
    public double rightThumbstickMagnitude;

    public double leftTriggerMag;
    public double rightTriggerMag;

//    private Utils.Vector2D RightThumbstick;
//    private Utils.Vector2D LeftThumbstick;

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
//        buttons.put(Button.LTRIGGER, false);
//        buttons.put(Button.RTRIGGER, false);

        buttonsPressed = 0;
        leftThumbDir = 'Z';
        rightThumbstickDirection = 'Z';
        leftThumbstickMagnitude = 0.0;
        rightThumbstickMagnitude = 0.0;
        rightThumb = 0.0;
        leftThumbstickDirection =0.0;

        leftTriggerMag = 0.0;
        rightTriggerMag = 0.0;
    }

    public ControllerMessage(){
        //Empty constructor
    }

    public ControllerMessage(
            String payload,
            HashMap<Button,Boolean> buttons,
            int buttonsPressed,
            double leftThumbstickDirection,
            char leftThumbDir,
            double rightThumb,
            char rightThumbstickDirection,
            double leftThumbstickMagnitude,
            double rightThumbstickMagnitude,
            double leftTriggerMag,
            double rightTriggerMag
    ){
        super(payload);
        this.buttons = buttons;
        this.buttonsPressed = buttonsPressed;
        this.leftThumbstickDirection = leftThumbstickDirection;
        this.leftThumbDir = leftThumbDir;
        this.rightThumb = rightThumb;
        this.rightThumbstickDirection = rightThumbstickDirection;
        this.leftThumbstickMagnitude = leftThumbstickMagnitude;
        this.rightThumbstickMagnitude = rightThumbstickMagnitude;
        this.leftTriggerMag = leftTriggerMag;
        this.rightTriggerMag = rightTriggerMag;
    }

    public ControllerMessage(ControllerMessage that){
        this.buttons = new HashMap<>(that.buttons);
        this.buttonsPressed = that.buttonsPressed;
        this.leftThumbstickDirection = that.leftThumbstickDirection;
        this.leftThumbDir = that.leftThumbDir;
        this.rightThumb = that.rightThumb;
        this.rightThumbstickDirection = that.rightThumbstickDirection;
        this.leftThumbstickMagnitude = that.leftThumbstickMagnitude;
        this.rightThumbstickMagnitude = that.rightThumbstickMagnitude;
        this.leftTriggerMag = that.leftTriggerMag;
        this.rightTriggerMag = that.rightTriggerMag;
    }

    public int getButtonsPressed() {
        return buttonsPressed;
    }

    public void setButtonsPressed(int buttonsPressed) {
        this.buttonsPressed = buttonsPressed;
    }

    public void setLeftThumbDir(char leftThumbDir) {
        this.leftThumbDir = leftThumbDir;
    }

    public void setRightThumbstickDirection(char rightThumbstickDirection) {
        this.rightThumbstickDirection = rightThumbstickDirection;
    }

    @Override
    public void Execute(Object context) {
        Log(this.toString());
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
        buffer.append(this.leftTriggerMag);
        buffer.append(" | ");
        buffer.append("R=");
        buffer.append(this.rightTriggerMag);
        buffer.append("\n");

        //Thumbsticks
        buffer.append("Thumbsticks\n");
        buffer.append("Left: Mag=");
        buffer.append(this.leftThumbstickMagnitude);
        buffer.append(" | Dir=");
        buffer.append(this.leftThumbstickDirection);
        buffer.append("\n");

        buffer.append("Right: Mag=");
        buffer.append(this.rightThumbstickMagnitude);
        buffer.append(" | Dir=");
        buffer.append(this.rightThumbstickDirection);
        buffer.append("\n");

        return buffer.toString();
    }
}
