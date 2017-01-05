package Machine.desktop;

import Machine.Common.Constants;
import Machine.Common.Network.ControllerMessage;
import ch.aplu.xboxcontroller.*;

import static Machine.Common.Utils.Log;
import Machine.Common.Utils.Button;


public class Controller extends XboxControllerAdapter {

    private XboxController xboxController;

    private ControllerMessage controllerState;

    public ControllerMessage getControllerState() {
        return controllerState;
    }

    public XboxController getXboxController() {
        return xboxController;
    }
    private String dllPath;

    private void press(Button button) {
        controllerState.setButtonsPressed(controllerState.getButtonsPressed() + 1);
        controllerState.buttons.replace(button, true);
    }

    private void depress(Button button) {
        controllerState.setButtonsPressed(controllerState.getButtonsPressed() - 1);
        controllerState.buttons.replace(button, false);
    }

    public void buttonA(boolean pressed) {
        if (pressed) {
            press(Button.A);
        } else {
            depress(Button.A);
        }
    }

    public void buttonB(boolean pressed) {
        if (pressed) {
            press(Button.B);
        } else {
            depress(Button.B);
        }
    }

    public void buttonX(boolean pressed) {
        if (pressed) {
            press(Button.X);
        } else {
            depress(Button.X);
        }
    }

    public void buttonY(boolean pressed) {
        if (pressed) {
            press(Button.Y);
        } else {
            depress(Button.Y);
        }
    }

    public void back(boolean pressed) {
        if (pressed) {
            press(Button.BACK);
        } else {
            depress(Button.BACK);
        }
    }

    public void start(boolean pressed) {
        if (pressed) {
            press(Button.START);
        } else {
            depress(Button.START);
        }
    }

    public void leftShoulder(boolean pressed) {
        if (pressed) {
            press(Button.LBUMPER);
        } else {
            depress(Button.LBUMPER);
        }
    }

    public void rightShoulder(boolean pressed) {
        if (pressed) {
            press(Button.RBUMPER);
        } else {
            depress(Button.RBUMPER);
        }
    }

    public void leftThumb(boolean pressed) {
        if (pressed) {
            press(Button.LTHUMB);
        } else {
            depress(Button.LTHUMB);
        }
    }

    public void rightThumb(boolean pressed) {
        if (pressed) {
            press(Button.RTHUMB);
        } else {
            depress(Button.RTHUMB);
        }
    }

    public void dpad(int direction, boolean pressed) {
        if (pressed) {
            switch (direction) {
                case 0:
                    // N
                    press(Button.NDPAD);
                    break;
                case 1:
                    // NE
                    break;
                case 2:
                    // E
                    press(Button.EDPAD);
                    break;
                case 3:
                    // SE
                    break;
                case 4:
                    // S
                    press(Button.SDPAD);
                    break;
                case 5:
                    // SW
                    break;
                case 6:
                    // W
                    press(Button.WDPAD);
                    break;
                case 7:
                    // NW
                    break;
            }
        } else {
            switch (direction) {
                case 0:
                    // N
                    depress(Button.NDPAD);
                    break;
                case 1:
                    // NE
                    break;
                case 2:
                    // E
                    depress(Button.EDPAD);
                    break;
                case 3:
                    // SE
                    break;
                case 4:
                    // S
                    depress(Button.SDPAD);
                    break;
                case 5:
                    // SW
                    break;
                case 6:
                    // W
                    depress(Button.WDPAD);
                    break;
                case 7:
                    // NW
                    break;
            }
        }
    }

    public void leftTrigger(double value) {
        //value is how hard you press. Between 0-1.0
        controllerState.leftTriggerMagnitude = value;
    }

    public void rightTrigger(double value) {
        //value is how hard you press. Between 0-1.0
        controllerState.rightTriggerMagnitude = value;
    }

    public void leftThumbMagnitude(double magnitude) {
        //magnitude is how hard you press. Between 0-1.0
        controllerState.leftThumbstickMagnitude = magnitude;
        if (magnitude < 0.2) {
            controllerState.leftThumbstickDirection = 'Z';
            controllerState.leftThumbstickRotation = 0.0;
        }
    }

    public void leftThumbDirection(double direction) {
        //direction is angle. Between 0-360.0, at top
        controllerState.leftThumbstickRotation = (direction);
        if (direction < 45 || direction > 315) {
            controllerState.setLeftThumbDir('N');
        } else if (direction < 135) {
            controllerState.setLeftThumbDir('E');
        } else if (direction < 225) {
            controllerState.setLeftThumbDir('S');
        } else if (direction < 315) {
            controllerState.setLeftThumbDir('W');
        } else {
            controllerState.setLeftThumbDir('Z');
        }
    }

    public void rightThumbMagnitude(double magnitude) {
        //magnitude is how hard you press. Between 0-1.0
        controllerState.rightThumbstickMagnitude = magnitude;
        if (magnitude < 0.2) {
            controllerState.rightThumbstickDirection = 'Z';
            controllerState.rightThumbstickRotation = 0.0;
        }
    }

    public void rightThumbDirection(double direction) {
        //direction is angle. Between 0-360.0, at top
        controllerState.rightThumbstickRotation = (direction);
        if (direction < 45 || direction > 315) {
            controllerState.setRightThumbstickDirection('N');
        } else if (direction < 135) {
            controllerState.setRightThumbstickDirection('E');
        } else if (direction < 225) {
            controllerState.setRightThumbstickDirection('S');
        } else if (direction < 315) {
            controllerState.setRightThumbstickDirection('W');
        } else {
            controllerState.setRightThumbstickDirection('Z');
        }
    }

    public void isConnected() {
        if (xboxController.isConnected()) {
            Log(" - Controller connected");
        } else {
            Log(" - Controller disconnected");
        }
    }

    public Controller(ControllerMessage controllerState) {

        String baseDir = System.getProperty("user.dir");
        if (baseDir.endsWith("honeybadger")) {
            baseDir += "\\executable";
        }
        baseDir += "\\bin";
        String arch = System.getProperty("os.arch");
        System.out.println(arch);
        if(dllPath == null) {
            dllPath = String.format("%s\\%s", baseDir,
                    arch.contains("x86") ? "xboxcontroller.dll" : "xboxcontroller64.dll");
        }
        xboxController = new XboxController(
                dllPath,
                1,
                50,
                50);

        isConnected();

        this.controllerState = controllerState;

        xboxController.addXboxControllerListener(this);
        xboxController.setLeftThumbDeadZone(0.2);
        xboxController.setRightThumbDeadZone(0.2);

    }
}