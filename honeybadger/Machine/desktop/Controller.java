package Machine.desktop;

import Machine.Common.Network.BaseMsg;
import Machine.Common.Utils;
import Machine.Common.Network.ControllerMessage;
import ch.aplu.xboxcontroller.*;

import java.util.HashMap;


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

    public static class controllerState extends BaseMsg{
        static int buttonsPressed = 0; // Number of buttons currently pressed
        public enum Button {A, B, X, Y, BACK, START, RBUMPER, LBUMPER, RTHUMB,
        LTHUMB, NDPAD, EDPAD, SDPAD, WDPAD, LTRIGGER, RTRIGGER}

        private static HashMap<Button, Boolean> buttons; // The 16 buttons pressed if true
        static {
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
        }
        private static double leftThumb = 0.0;
        private static char leftThumbDir = 'Z'; // N, E, S, W for directions.
        private static double rightThumb = 0.0;
        private static char rightThumbDir = 'Z';
        private static double leftMag = 0.0;
        private static double rightMag = 0.0;

        public static int getButtonsPressed() {
            return buttonsPressed;
        }

        public static HashMap<Button, Boolean> getButtons() {
            return buttons;
        }

        public static double getLeftThumb() {
            return leftThumb;
        }

        public static char getLeftThumbDir() {
            return leftThumbDir;
        }

        public static double getRightThumb() {
            return rightThumb;
        }

        public static char getRightThumbDir() {
            return rightThumbDir;
        }

        public static double getLeftMag() {
            return leftMag;
        }

        public static double getRightMag() {
            return rightMag;
        }
    }

//    public void sendActions(){
//        if(controllerState.buttons[0]){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveBack(100)));
//        }
//        if(controllerState.buttons[1]){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveRight(100)));
//        }
//        if(controllerState.buttons[2]){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveLeft(100)));
//        }
//        if(controllerState.buttons[3]){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveRight(100)));
//        }
//        if(controllerState.buttons[4]){
//            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//        }
//        if(controllerState.buttons[5]){
//            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//        }
//        if(controllerState.buttons[6]){
//            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//        }
//        if(controllerState.buttons[7]){
//            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//        }
//        if(controllerState.buttons[8]){
//            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//        }
//        if(controllerState.buttons[9]){
//            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//        }
//        if(controllerState.buttons[10]){
//            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//        }
//        if(controllerState.buttons[11]){
//            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//        }
//        if(controllerState.buttons[12]){
//            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//        }
//        if(controllerState.buttons[13]){
//            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//        }
//        if(controllerState.buttons[14]){
//            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//        }
//        if(controllerState.buttons[15]){
//            SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//        }
//        if(controllerState.leftMag > 0.2){
//            switch (controllerState.leftThumbDir) {
//                case 'N':
//                    SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//                    break;
//                case 'E':
//                    SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//                    break;
//                case 'S':
//                    SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//                    break;
//                case 'W':
//                    SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//                    break;
//            }
//        }
//        if(controllerState.rightMag > 0.2){
//            switch (controllerState.rightThumbDir) {
//                case 'N':
//                    SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//                    break;
//                case 'E':
//                    SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//                    break;
//                case 'S':
//                    SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//                    break;
//                case 'W':
//                    SendMessage(new ControllerMessage(new ControllerMessage.Stop()));
//                    break;
//            }
//        }
//    }

    public static void press(controllerState.Button button){
        controllerState.buttonsPressed++;
        controllerState.buttons.replace(button, true);
    }
    
    public static void depress(controllerState.Button button){
        controllerState.buttonsPressed--;
        controllerState.buttons.replace(button, false);
    }
    
    public void buttonA(boolean pressed)
    {
        if(pressed){
            SendMessage(new ControllerMessage(new ControllerMessage.MoveBack(100)));
            press(controllerState.Button.A);
        }
        else{
            depress(controllerState.Button.A);
        }
    }

    public void buttonB(boolean pressed)
    {
        if(pressed){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveRight(100)));
            press(controllerState.Button.B);
        }
        else{
            depress(controllerState.Button.B);
        }
    }

    public void buttonX(boolean pressed)
    {
        if(pressed){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveLeft(100)));
            press(controllerState.Button.X);
        }
        else{
            depress(controllerState.Button.X);
        }
    }

    public void buttonY(boolean pressed)
    {
        if (pressed){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveForward(100)));
            press(controllerState.Button.Y);
        }
        else{
            depress(controllerState.Button.Y);
        }
    }

    public void back(boolean pressed)
    {
        if (pressed){
            press(controllerState.Button.BACK);
        }
        else{
            depress(controllerState.Button.START);
        }
    }

    public void start(boolean pressed)
    {
        if (pressed){
            press(controllerState.Button.RBUMPER);
        }
        else{
            depress(controllerState.Button.RBUMPER);
        }
    }

    public void leftShoulder(boolean pressed)
    {
        if (pressed){
//            ControllerMessage.DEBUG_MOTOR.throttle = ControllerMessage.DEBUG_MOTOR.throttle <100?
//                    ControllerMessage.DEBUG_MOTOR.throttle - 5.0f : ControllerMessage.DEBUG_MOTOR.throttle;
            press(controllerState.Button.LBUMPER);
        }
        else{
            depress(controllerState.Button.LBUMPER);
        }
    }

    public void rightShoulder(boolean pressed)
    {
        if (pressed){
//            ControllerMessage.DEBUG_MOTOR.throttle = ControllerMessage.DEBUG_MOTOR.throttle <100?
//                    ControllerMessage.DEBUG_MOTOR.throttle + 5.0f : ControllerMessage.DEBUG_MOTOR.throttle;
            press(controllerState.Button.RBUMPER);
        }
        else{
            depress(controllerState.Button.RBUMPER);
        }
    }

    public void leftThumb(boolean pressed)
    {
        if (pressed){
            press(controllerState.Button.LTHUMB);
        }
        else{
            depress(controllerState.Button.LTHUMB);
        }
    }

    public void rightThumb(boolean pressed)
    {
        if (pressed){
            press(controllerState.Button.RTHUMB);
        }
        else{
            depress(controllerState.Button.RTHUMB);
        }
    }

    public void dpad(int direction, boolean pressed) {
        if (pressed) {
//            SendMessage("Pressed dpad direction "+direction);
            switch (direction) {
                case 0:
                    // N
                    press(controllerState.Button.NDPAD);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 1:
                    // NE
                    break;
                case 2:
                    // E
                    press(controllerState.Button.EDPAD);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_BR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 3:
                    // SE
                    break;
                case 4:
                    // S
                    press(controllerState.Button.SDPAD);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.COUNTER_CLOCKWISE)));
                    break;
                case 5:
                    // SW
                    break;
                case 6:
                    // W
                    press(controllerState.Button.WDPAD);
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
                    depress(controllerState.Button.NDPAD);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 1:
                    // NE
                    break;
                case 2:
                    // E
                    depress(controllerState.Button.EDPAD);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_BR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 3:
                    // SE
                    break;
                case 4:
                    // S
                    depress(controllerState.Button.SDPAD);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.COUNTER_CLOCKWISE)));
                    break;
                case 5:
                    // SW
                    break;
                case 6:
                    // W
                    depress(controllerState.Button.WDPAD);
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
        if (value > 0.2){
            press(controllerState.Button.LTRIGGER);
        }
        else {
            depress(controllerState.Button.LTRIGGER);
        }
//        FlywheelSpeed += value;
//        if(FlywheelSpeed>100){
//            FlywheelSpeed=100;
//        }
//        SendMessage(new ControllerMessage(new ControllerMessage.Shoot((float)value)));
    }

    public void rightTrigger(double value)
    {
        //value is how hard you press. Between 0-1.0
        if (value > 0.2){
            press(controllerState.Button.RTRIGGER);
        }
        else {
            depress(controllerState.Button.RTRIGGER);
        }
    }

    public void leftThumbMagnitude(double magnitude)
    {
        //magnitude is how hard you press. Between 0-1.0
        LeftThumbstick.UpdateMagnitude(magnitude);
        controllerState.leftMag = magnitude;
    }

    public void leftThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
        LeftThumbstick.UpdateAngleDegrees(direction);
        controllerState.leftThumb = direction;
        if (direction < 45 || direction > 315){
            controllerState.leftThumbDir = 'N';
        } else if (direction < 135){
            controllerState.leftThumbDir = 'E';
        } else if (direction < 225){
            controllerState.leftThumbDir = 'S';
        } else if (direction < 315){
            controllerState.leftThumbDir = 'W';
        } else {
            controllerState.leftThumbDir = 'Z';
        }
    }

    public void rightThumbMagnitude(double magnitude)
    {
        //magnitude is how hard you press. Between 0-1.0
        RightThumbstick.UpdateMagnitude(magnitude);
        controllerState.rightMag = magnitude;
    }

    public void rightThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
        RightThumbstick.UpdateAngleDegrees(direction);
        controllerState.rightThumb = direction;
        if (direction < 45 || direction > 315){
            controllerState.rightThumbDir = 'N';
        } else if (direction < 135){
            controllerState.rightThumbDir = 'E';
        } else if (direction < 225){
            controllerState.rightThumbDir = 'S';
        } else if (direction < 315){
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
