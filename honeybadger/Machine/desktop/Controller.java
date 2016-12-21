package Machine.desktop;

import java.lang.Math;

import Machine.Common.Network.BaseMsg;
import Machine.Common.Network.ControllerMessage;
import ch.aplu.xboxcontroller.*;

public class Controller extends XboxControllerAdapter{

    String path = "xboxcontroller64.dll";
    NetworkConnector connector;
    XboxController connectedController;

    double rightThumbstickAngle;
    double leftThumbstickAngle;

    private void SendMessage(String msg){
        connector.SendMessage(msg);
    }

    private void SendMessage(BaseMsg msg){
        connector.SendMessage(msg);
    }

    public void buttonA(boolean pressed)
    {
        if(pressed){
            SendMessage(new ControllerMessage(new ControllerMessage.MoveForward(100)));
        }
    }

    public void buttonB(boolean pressed)
    {
        if(pressed){
            SendMessage("Pressed B");
        }
    }

    public void buttonX(boolean pressed)
    {
        if(pressed){
            SendMessage("Pressed X");
        }
    }

    public void buttonY(boolean pressed)
    {
        if (pressed){
            SendMessage("Pressed Y");
        }
    }

    public void back(boolean pressed)
    {
        if (pressed){
            SendMessage("Pressed back button");
        }
    }

    public void start(boolean pressed)
    {
        if (pressed){
            SendMessage("Pressed start button");
        }
    }

    public void leftShoulder(boolean pressed)
    {
        if (pressed){
            SendMessage("Pressed left bumper");
        }
    }

    public void rightShoulder(boolean pressed)
    {
        if (pressed){
            SendMessage("Pressed right bumper");
        }
    }

    public void leftThumb(boolean pressed)
    {
        if (pressed){
            SendMessage("Pressed left thumbstick");
        }
    }

    public void rightThumb(boolean pressed)
    {
        if (pressed){
            SendMessage("Pressed right thumbstick");
        }
    }

    public void dpad(int direction, boolean pressed) {
        if (pressed) {
            SendMessage("Pressed dpad direction "+direction);
            switch (direction) {
                case 0:
                    // N
                    break;
                case 1:
                    // NE
                    break;
                case 2:
                    // E
                    break;
                case 3:
                    // SE
                    break;
                case 4:
                    // S
                    break;
                case 5:
                    // SW
                    break;
                case 6:
                    // W
                    break;
                case 7:
                    // NW
                    break;
            }
        }
    }

    public void leftTrigger(double value)
    {
        //value is how hard you press. Between 0-1.0
        SendMessage("leftTrigger: "+value);
    }

    public void rightTrigger(double value)
    {
        //value is how hard you press. Between 0-1.0
        SendMessage("rightTrigger: "+value);
    }

    public void leftThumbMagnitude(double magnitude)
    {
        //magnitude is how hard you press. Between 0-1.0
        double x = magnitude*Math.sin(leftThumbstickAngle);
        double y = magnitude*Math.cos(leftThumbstickAngle);
        SendMessage("leftThumbsticks: "+x+" "+y);
    }

    public void leftThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
        leftThumbstickAngle = Math.toRadians(direction);
    }

    public void rightThumbMagnitude(double magnitude)
    {
        //magnitude is how hard you press. Between 0-1.0
        double x = magnitude*Math.sin(rightThumbstickAngle);
        double y = magnitude*Math.cos(rightThumbstickAngle);
        SendMessage("leftThumbsticks: "+x+" "+y);
    }

    public void rightThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
        rightThumbstickAngle =  Math.toRadians(direction);
    }

    public void isConnected()
    {
        if (connectedController.isConnected()) {
            System.out.println(" - Controller connected");
            SendMessage(" - Controller connected");
        }
        else {
            System.out.println(" - Controller disconnected");
            SendMessage(" - Controller connected");
        }
    }

    public Controller(NetworkConnector messageConnector)
    {
        connector = messageConnector;
        connectedController = new XboxController(System.getProperty("user.dir") +"\\"+path ,1,50,50);
        isConnected();
        connectedController.addXboxControllerListener(this);
        connectedController.setLeftThumbDeadZone(0.2);
        connectedController.setRightThumbDeadZone(0.2);

        rightThumbstickAngle = 0.0;
        leftThumbstickAngle = 0.0;
    }
}
