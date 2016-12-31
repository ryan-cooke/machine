package Machine.desktop;

import Machine.Common.Network.BaseMsg;
import Machine.Common.Utils;
import Machine.Common.Network.ControllerMessage;
import ch.aplu.xboxcontroller.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class Controller extends XboxControllerAdapter{

    private final ScheduledExecutorService ScheduledManager;
    private String path = "xboxcontroller64.dll";
    private NetworkConnector connector;
    private XboxController connectedController;
    private ControllerMessage controllerState;

//    private Utils.Vector2D RightThumbstick;
//    private Utils.Vector2D LeftThumbstick;

    private float FlywheelSpeed;

    private void SendMessage(String msg){
        connector.SendMessage(msg);
    }

    private void SendMessage(BaseMsg msg){
        connector.SendMessage(msg);
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

    public void press(ControllerMessage.Button button){
        controllerState.setButtonsPressed(controllerState.getButtonsPressed()+1);
        controllerState.getButtons().replace(button, true);
    }
    
    public void depress(ControllerMessage.Button button){
        controllerState.setButtonsPressed(controllerState.getButtonsPressed()-1);
        controllerState.getButtons().replace(button, false);
    }
    
    public void buttonA(boolean pressed)
    {
        if(pressed){
            //SendMessage(new ControllerMessage(new ControllerMessage.MoveBack(100)));
            press(ControllerMessage.Button.A);
        }
        else{
            depress(ControllerMessage.Button.A);
        }
    }

    public void buttonB(boolean pressed)
    {
        if(pressed){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveRight(100)));
            press(ControllerMessage.Button.B);
        }
        else{
            depress(ControllerMessage.Button.B);
        }
    }

    public void buttonX(boolean pressed)
    {
        if(pressed){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveLeft(100)));
            press(ControllerMessage.Button.X);
        }
        else{
            depress(ControllerMessage.Button.X);
        }
    }

    public void buttonY(boolean pressed)
    {
        if (pressed){
//            SendMessage(new ControllerMessage(new ControllerMessage.MoveForward(100)));
            press(ControllerMessage.Button.Y);
        }
        else{
            depress(ControllerMessage.Button.Y);
        }
    }

    public void back(boolean pressed)
    {
        if (pressed){
            press(ControllerMessage.Button.BACK);
        }
        else{
            depress(ControllerMessage.Button.START);
        }
    }

    public void start(boolean pressed)
    {
        if (pressed){
            press(ControllerMessage.Button.RBUMPER);
        }
        else{
            depress(ControllerMessage.Button.RBUMPER);
        }
    }

    public void leftShoulder(boolean pressed)
    {
        if (pressed){
//            ControllerMessage.DEBUG_MOTOR.throttle = ControllerMessage.DEBUG_MOTOR.throttle <100?
//                    ControllerMessage.DEBUG_MOTOR.throttle - 5.0f : ControllerMessage.DEBUG_MOTOR.throttle;
            press(ControllerMessage.Button.LBUMPER);
        }
        else{
            depress(ControllerMessage.Button.LBUMPER);
        }
    }

    public void rightShoulder(boolean pressed)
    {
        if (pressed){
//            ControllerMessage.DEBUG_MOTOR.throttle = ControllerMessage.DEBUG_MOTOR.throttle <100?
//                    ControllerMessage.DEBUG_MOTOR.throttle + 5.0f : ControllerMessage.DEBUG_MOTOR.throttle;
            press(ControllerMessage.Button.RBUMPER);
        }
        else{
            depress(ControllerMessage.Button.RBUMPER);
        }
    }

    public void leftThumb(boolean pressed)
    {
        if (pressed){
            press(ControllerMessage.Button.LTHUMB);
        }
        else{
            depress(ControllerMessage.Button.LTHUMB);
        }
    }

    public void rightThumb(boolean pressed)
    {
        if (pressed){
            press(ControllerMessage.Button.RTHUMB);
        }
        else{
            depress(ControllerMessage.Button.RTHUMB);
        }
    }

    public void dpad(int direction, boolean pressed) {
        if (pressed) {
//            SendMessage("Pressed dpad direction "+direction);
            switch (direction) {
                case 0:
                    // N
                    press(ControllerMessage.Button.NDPAD);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 1:
                    // NE
                    break;
                case 2:
                    // E
                    press(ControllerMessage.Button.EDPAD);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_BR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 3:
                    // SE
                    break;
                case 4:
                    // S
                    press(ControllerMessage.Button.SDPAD);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.COUNTER_CLOCKWISE)));
                    break;
                case 5:
                    // SW
                    break;
                case 6:
                    // W
                    press(ControllerMessage.Button.WDPAD);
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
                    depress(ControllerMessage.Button.NDPAD);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 1:
                    // NE
                    break;
                case 2:
                    // E
                    depress(ControllerMessage.Button.EDPAD);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_BR(BadgerMotorController.CLOCKWISE)));
                    break;
                case 3:
                    // SE
                    break;
                case 4:
                    // S
                    depress(ControllerMessage.Button.SDPAD);
//                    SendMessage(new ControllerMessage(new ControllerMessage.DEBUG_MOTOR_FR(BadgerMotorController.COUNTER_CLOCKWISE)));
                    break;
                case 5:
                    // SW
                    break;
                case 6:
                    // W
                    depress(ControllerMessage.Button.WDPAD);
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
            press(ControllerMessage.Button.LTRIGGER);
        }
        else {
            depress(ControllerMessage.Button.LTRIGGER);
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
            press(ControllerMessage.Button.RTRIGGER);
        }
        else {
            depress(ControllerMessage.Button.RTRIGGER);
        }
    }

    public void leftThumbMagnitude(double magnitude)
    {
        //magnitude is how hard you press. Between 0-1.0
//        LeftThumbstick.UpdateMagnitude(magnitude);
        controllerState.setLeftMag(magnitude);
    }

    public void leftThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
//        LeftThumbstick.UpdateAngleDegrees(direction);
        controllerState.setLeftThumb(direction);
        if (direction < 45 || direction > 315){
            controllerState.setLeftThumbDir('N');
        } else if (direction < 135){
            controllerState.setLeftThumbDir('E');
        } else if (direction < 225){
            controllerState.setLeftThumbDir('S');
        } else if (direction < 315){
            controllerState.setLeftThumbDir('W');
        } else {
            controllerState.setLeftThumbDir('Z');
        }
    }

    public void rightThumbMagnitude(double magnitude)
    {
        //magnitude is how hard you press. Between 0-1.0
//        RightThumbstick.UpdateMagnitude(magnitude);
        controllerState.setRightMag(magnitude);
    }

    public void rightThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
//        RightThumbstick.UpdateAngleDegrees(direction);
        controllerState.setRightThumb(direction);
        if (direction < 45 || direction > 315){
            controllerState.setRightThumbDir('N');
        } else if (direction < 135){
            controllerState.setRightThumbDir('E');
        } else if (direction < 225){
            controllerState.setRightThumbDir('S');
        } else if (direction < 315){
            controllerState.setRightThumbDir('W');
        } else {
            controllerState.setRightThumbDir('Z');
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
        ScheduledManager = Executors.newScheduledThreadPool(1);
        ControllerMessage state = new ControllerMessage();
        connectedController.addXboxControllerListener(this);
        connectedController.setLeftThumbDeadZone(0.2);
        connectedController.setRightThumbDeadZone(0.2);

//        RightThumbstick = new Utils.Vector2D();
//        LeftThumbstick = new Utils.Vector2D();

        final ScheduledFuture<?> PeriodicSenderHandle = ScheduledManager.scheduleAtFixedRate(
                (Runnable) () -> SendMessage(state),
                3,10, TimeUnit.SECONDS
        );
    }
}
