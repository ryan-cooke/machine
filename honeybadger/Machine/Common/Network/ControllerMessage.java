package Machine.Common.Network;

import java.util.HashMap;

/**
 * Handles everything related to networking the controller
 */
public class ControllerMessage extends BaseMsg {
    private int buttonsPressed; // Number of buttons currently pressed
    public enum Button {A, B, X, Y, BACK, START, RBUMPER, LBUMPER, RTHUMB,
        LTHUMB, NDPAD, EDPAD, SDPAD, WDPAD, LTRIGGER, RTRIGGER}

    private HashMap<Button, Boolean> buttons; // The 16 buttons pressed if true

    private double leftThumb;
    private char leftThumbDir; // N, E, S, W for directions.
    private double rightThumb;
    private char rightThumbDir;
    private double leftMag;
    private double rightMag;

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
        buttons.put(Button.LTRIGGER, false);
        buttons.put(Button.RTRIGGER, false);

        buttonsPressed = 0;
        leftThumbDir = 'Z';
        rightThumbDir = 'Z';
        leftMag = 0.0;
        rightMag = 0.0;
        rightThumb = 0.0;
        leftThumb=0.0;
    }

    public int getButtonsPressed() {
        return buttonsPressed;
    }

    public  HashMap<Button, Boolean> getButtons() {
        return buttons;
    }

    public  double getLeftThumb() {
        return leftThumb;
    }

    public  char getLeftThumbDir() {
        return leftThumbDir;
    }

    public  double getRightThumb() {
        return rightThumb;
    }

    public  char getRightThumbDir() {
        return rightThumbDir;
    }

    public  double getLeftMag() {
        return leftMag;
    }

    public double getRightMag() {
        return rightMag;
    }

    public void setButtonsPressed(int buttonsPressed) {
        this.buttonsPressed = buttonsPressed;
    }

    public void setButtons(HashMap<Button, Boolean> buttons) {
        this.buttons = buttons;
    }

    public void setLeftThumb(double leftThumb) {
        this.leftThumb = leftThumb;
    }

    public void setLeftThumbDir(char leftThumbDir) {
        this.leftThumbDir = leftThumbDir;
    }

    public void setRightThumb(double rightThumb) {
        this.rightThumb = rightThumb;
    }

    public void setRightThumbDir(char rightThumbDir) {
        this.rightThumbDir = rightThumbDir;
    }

    public void setLeftMag(double leftMag) {
        this.leftMag = leftMag;
    }

    public void setRightMag(double rightMag) {
        this.rightMag = rightMag;
    }
}
