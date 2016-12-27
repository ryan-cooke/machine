package Machine.desktop;

import Machine.Common.Network.BaseMsg;
import Machine.Common.Network.ControllerMessage;
import Machine.Common.Utils;

import Machine.rpi.hw.BadgerMotorController;
import ch.aplu.xboxcontroller.*;

import java.nio.file.SecureDirectoryStream;


public class Controller extends XboxControllerAdapter{

    private String path = "xboxcontroller64.dll";
    private NetworkConnector connector;
    private XboxController connectedController;

    private Utils.Vector2D RightThumbstick;
    private Utils.Vector2D LeftThumbstick;

    private float FlywheelSpeed;

    private void SendMessage(String msg){
        connector.SendMessage(msg);
    }

    private void SendMessage(BaseMsg msg){
        connector.SendMessage(msg);
    }

    public void buttonA(boolean pressed)
    {
        if(pressed){
            SendMessage(new ControllerMessage(new ControllerMessage.MoveBack(100)));
        }
    }

    public void buttonB(boolean pressed)
    {
        if(pressed){
            SendMessage(new ControllerMessage(new ControllerMessage.MoveRight(100)));
        }
    }

    public void buttonX(boolean pressed)
    {
        if(pressed){
            SendMessage(new ControllerMessage(new ControllerMessage.MoveLeft(100)));
        }
    }

    public void buttonY(boolean pressed)
    {
        if (pressed){
            SendMessage(new ControllerMessage(new ControllerMessage.MoveForward(100)));
        }
    }

    public void back(boolean pressed)
    {
        if (pressed){
            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
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
            ControllerMessage.DEBUG_MOTOR.throttle = ControllerMessage.DEBUG_MOTOR.throttle <100?
                    ControllerMessage.DEBUG_MOTOR.throttle - 5.0f : ControllerMessage.DEBUG_MOTOR.throttle;
            SendMessage("Pressed left bumper");
        }
    }

    public void rightShoulder(boolean pressed)
    {
        if (pressed){
            ControllerMessage.DEBUG_MOTOR.throttle = ControllerMessage.DEBUG_MOTOR.throttle <100?
                    ControllerMessage.DEBUG_MOTOR.throttle + 5.0f : ControllerMessage.DEBUG_MOTOR.throttle;

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
                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 1:
                    // NE
                    break;
                case 2:
                    // E
                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_BR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 3:
                    // SE
                    break;
                case 4:
                    // S
                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.COUNTER_CLOCKWISE)));
                    break;
                case 5:
                    // SW
                    break;
                case 6:
                    // W
                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_BR(BadgerMotorController.COUNTER_CLOCKWISE)));
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
        FlywheelSpeed += value;
        if(FlywheelSpeed>100){
            FlywheelSpeed=100;
        }
        SendMessage(new ControllerMessage(new ControllerMessage.Shoot((float)value)));
    }

    public void rightTrigger(double value)
    {
        //value is how hard you press. Between 0-1.0
        SendMessage(new ControllerMessage(new ControllerMessage.Shoot((float)value)));
    }

    public void leftThumbMagnitude(double magnitude)
    {
        //magnitude is how hard you press. Between 0-1.0
        LeftThumbstick.UpdateMagnitude(magnitude);
        SendMessage(LeftThumbstick.toString());
    }

    public void leftThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
        LeftThumbstick.UpdateAngleDegrees(direction);
        SendMessage(LeftThumbstick.toString());
    }

    public void rightThumbMagnitude(double magnitude)
    {
        //magnitude is how hard you press. Between 0-1.0
        RightThumbstick.UpdateMagnitude(magnitude);
        SendMessage(RightThumbstick.toString());
    }

    public void rightThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
        RightThumbstick.UpdateAngleDegrees(direction);
        SendMessage(RightThumbstick.toString());
    }

    public void isConnected()
    {
        if (connectedController.isConnected()) {
            Utils.Log(" - Controller connected");
            SendMessage(" - Controller connected");
        }
        else {
            Utils.Log(" - Controller disconnected");
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

        RightThumbstick = new Utils.Vector2D();
        LeftThumbstick = new Utils.Vector2D();

    }
}
