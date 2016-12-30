package Machine.desktop;

import Machine.Common.Network.BaseMsg;
import Machine.Common.Network.ControllerMessage;
import Machine.Common.Utils;

import Machine.rpi.hw.BadgerMotorController;
import ch.aplu.xboxcontroller.*;

import java.nio.file.SecureDirectoryStream;
import java.util.ArrayList;


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

    public static class controllerState{
        static int buttonPressed = 0; // Number of buttons currently pressed
        static boolean button[] = new boolean[15]; // The 16 buttons pressed if true
        static double leftThumb = 0.0;
        static char leftThumbDir = 'Z'; // N, E, S, W for directions.
        static double rightThumb = 0.0;
        static char rightThumbDir = 'Z';
        static double leftMag = 0.0;
        static double rightMag = 0.0;

    }

    public static void press(int button){
        controllerState.buttonPressed++;
        controllerState.button[button] = true;
    }
    
    public static void depress(int button){
        controllerState.buttonPressed--;
        controllerState.button[button] = false;
    }
    
    public void buttonA(boolean pressed)
    {
        // Button 0
        if(pressed){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveBack(100)));
            press(0);
        }
        else{
            depress(0);
        }
    }

    public void buttonB(boolean pressed)
    {
        // Button 1
        if(pressed){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveRight(100)));
            press(1);
        }
        else{
            depress(1);
        }
    }

    public void buttonX(boolean pressed)
    {
        // Button 2
        if(pressed){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveLeft(100)));
            press(2);
        }
        else{
            depress(2);
        }
    }

    public void buttonY(boolean pressed)
    {
        // Button 3
        if (pressed){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveForward(100)));
            press(3);
        }
        else{
            depress(3);
        }
    }

    public void back(boolean pressed)
    {
        // Button 4
        if (pressed){
//            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
            press(4);
        }
        else{
            depress(4);
        }
    }

    public void start(boolean pressed)
    {
        // Button 5
        if (pressed){
//            SendMessage("Pressed start button");
            press(5);
        }
        else{
            depress(5);
        }
    }

    public void leftShoulder(boolean pressed)
    {
        // Button 6
        if (pressed){
//            ControllerMessage.DEBUG_MOTOR.throttle = ControllerMessage.DEBUG_MOTOR.throttle <100?
//                    ControllerMessage.DEBUG_MOTOR.throttle - 5.0f : ControllerMessage.DEBUG_MOTOR.throttle;
//            SendMessage("Pressed left bumper");
            press(6);
        }
        else{
            depress(6);
        }
    }

    public void rightShoulder(boolean pressed)
    {
        // Button 7
        if (pressed){
//            ControllerMessage.DEBUG_MOTOR.throttle = ControllerMessage.DEBUG_MOTOR.throttle <100?
//                    ControllerMessage.DEBUG_MOTOR.throttle + 5.0f : ControllerMessage.DEBUG_MOTOR.throttle;
//            SendMessage("Pressed right bumper");
            press(7);
        }
        else{
            depress(7);
        }
    }

    public void leftThumb(boolean pressed)
    {
        // Button 8
        if (pressed){
//            SendMessage("Pressed left thumbstick");
            press(8);
        }
        else{
            depress(8);
        }
    }

    public void rightThumb(boolean pressed)
    {
        // Button 9
        if (pressed){
//            SendMessage("Pressed right thumbstick");
            press(9);
        }
        else{
            depress(9);
        }
    }

    public void dpad(int direction, boolean pressed) {
        if (pressed) {
//            SendMessage("Pressed dpad direction "+direction);
            switch (direction) {
                case 0:
                    // N
                    // Button 10
                    press(10);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 1:
                    // NE
                    break;
                case 2:
                    // E
                    // Button 11
                    press(11);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_BR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 3:
                    // SE
                    break;
                case 4:
                    // S
                    // Button 12
                    press(12);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.COUNTER_CLOCKWISE)));
                    break;
                case 5:
                    // SW
                    break;
                case 6:
                    // W
                    // Button 13
                    press(13);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_BR(BadgerMotorController.COUNTER_CLOCKWISE)));
                    break;
                case 7:
                    // NW
                    break;
            }
        } else{
            SendMessage("Unpressed dpad direction "+direction);
            switch (direction) {
                case 0:
                    // N
                    // Button 10
                    depress(10);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 1:
                    // NE
                    break;
                case 2:
                    // E
                    // Button 11
                    depress(11);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_BR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 3:
                    // SE
                    break;
                case 4:
                    // S
                    // Button 12
                    depress(12);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.COUNTER_CLOCKWISE)));
                    break;
                case 5:
                    // SW
                    break;
                case 6:
                    // W
                    // Button 13
                    depress(13);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_BR(BadgerMotorController.COUNTER_CLOCKWISE)));
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
        // Button 14
        if (value > 0.2){
            press(14);
        }
        else {
            depress(14);
        }
//        FlywheelSpeed += value;
//        if(FlywheelSpeed>100){
//            FlywheelSpeed=100;
//        }
//        SendMessage(new ControllerMessage(new ControllerMessage.Shoot((float)value)));
    }

    public void rightTrigger(double value)
    {
        // Button 15
        //value is how hard you press. Between 0-1.0
        if (value > 0.2){
            press(15);
        }
        else {
            depress(15);
        }
//        SendMessage(new ControllerMessage(new ControllerMessage.Shoot((float)value)));
    }

    public void leftThumbMagnitude(double magnitude)
    {
        //magnitude is how hard you press. Between 0-1.0
        LeftThumbstick.UpdateMagnitude(magnitude);
//        SendMessage(LeftThumbstick.toString());
        controllerState.leftMag = magnitude;
    }

    public void leftThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
        LeftThumbstick.UpdateAngleDegrees(direction);
//        SendMessage(LeftThumbstick.toString());
        controllerState.leftThumb = direction;
        if (direction < 90 && direction > 0){
            controllerState.leftThumbDir = 'N';
        } else if (direction < 180){
            controllerState.leftThumbDir = 'E';
        } else if (direction < 270){
            controllerState.leftThumbDir = 'S';
        } else if (direction < 360){
            controllerState.leftThumbDir = 'W';
        } else {
            controllerState.leftThumbDir = 'Z';
        }
    }

    public void rightThumbMagnitude(double magnitude)
    {
        //magnitude is how hard you press. Between 0-1.0
        RightThumbstick.UpdateMagnitude(magnitude);
//        SendMessage(RightThumbstick.toString());
        controllerState.rightMag = magnitude;
    }

    public void rightThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
        RightThumbstick.UpdateAngleDegrees(direction);
//        SendMessage(RightThumbstick.toString());
        controllerState.rightThumb = direction;
        if (direction < 90 && direction > 0){
            controllerState.rightThumbDir = 'N';
        } else if (direction < 180){
            controllerState.rightThumbDir = 'E';
        } else if (direction < 270){
            controllerState.rightThumbDir = 'S';
        } else if (direction < 360){
            controllerState.rightThumbDir = 'W';
        } else {
            controllerState.rightThumbDir = 'Z';
        }
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
