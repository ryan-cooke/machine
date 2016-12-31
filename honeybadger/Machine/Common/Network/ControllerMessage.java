package Machine.Common.Network;

import java.util.HashMap;

import static Machine.Common.Utils.Log;

/**
 * Handles everything related to networking the controller
 */
public class ControllerMessage extends BaseMsg {
    public int buttonsPressed; // Number of buttons currently pressed
    public enum Button {A, B, X, Y, BACK, START, RBUMPER, LBUMPER, RTHUMB,
        LTHUMB, NDPAD, EDPAD, SDPAD, WDPAD, LTRIGGER, RTRIGGER}

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

    public ControllerMessage(){
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
        super.Execute(context);

        //
        Log(this.toString());
    }

    @Override
    public String getPayload() {
        return "ControllerMessage";
    }

    @Override
    public String toString(){
        //Return the Controller state
        StringBuffer buffer = new StringBuffer();
        for(Button aButton : this.buttons.keySet()){
            buffer.append(aButton.toString());
            buffer.append("=");
            buffer.append(this.buttons.get(aButton)?"1":"0");
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
