package Machine.desktop;

import Machine.Common.Network.ControllerMessage;
import ch.aplu.xboxcontroller.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static Machine.Common.Utils.Log;


public class Controller extends XboxControllerAdapter{

    private final ScheduledExecutorService ScheduledManager;

    private String path = "xboxcontroller64.dll"; //Change to choose between 32 and 64 bit.
    private NetworkConnector connector;
    private XboxController connectedController;

    private ControllerMessage controllerState;

    private ScheduledFuture<?> ControllerMessageSender;

    private void press(ControllerMessage.Button button){
        controllerState.setButtonsPressed(controllerState.getButtonsPressed()+1);
        controllerState.buttons.replace(button, true);
    }
    
    private void depress(ControllerMessage.Button button){
        controllerState.setButtonsPressed(controllerState.getButtonsPressed()-1);
        controllerState.buttons.replace(button, false);
    }
    
    public void buttonA(boolean pressed)
    {
        if(pressed){
            press(ControllerMessage.Button.A);
        }
        else{
            depress(ControllerMessage.Button.A);
        }
    }

    public void buttonB(boolean pressed)
    {
        if(pressed){
            press(ControllerMessage.Button.B);
        }
        else{
            depress(ControllerMessage.Button.B);
        }
    }

    public void buttonX(boolean pressed)
    {
        if(pressed){
            press(ControllerMessage.Button.X);
        }
        else{
            depress(ControllerMessage.Button.X);
        }
    }

    public void buttonY(boolean pressed)
    {
        if (pressed){
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
            press(ControllerMessage.Button.LBUMPER);
        }
        else{
            depress(ControllerMessage.Button.LBUMPER);
        }
    }

    public void rightShoulder(boolean pressed)
    {
        if (pressed){
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
                    break;
                case 1:
                    // NE
                    break;
                case 2:
                    // E
                    press(ControllerMessage.Button.EDPAD);
                    break;
                case 3:
                    // SE
                    break;
                case 4:
                    // S
                    press(ControllerMessage.Button.SDPAD);
                    break;
                case 5:
                    // SW
                    break;
                case 6:
                    // W
                    press(ControllerMessage.Button.WDPAD);
                    break;
                case 7:
                    // NW
                    break;
            }
        } else{
            //Log("Unpressed dpad direction "+direction);
            switch (direction) {
                case 0:
                    // N
                    depress(ControllerMessage.Button.NDPAD);
                    break;
                case 1:
                    // NE
                    break;
                case 2:
                    // E
                    depress(ControllerMessage.Button.EDPAD);
                    break;
                case 3:
                    // SE
                    break;
                case 4:
                    // S
                    depress(ControllerMessage.Button.SDPAD);
                    break;
                case 5:
                    // SW
                    break;
                case 6:
                    // W
                    depress(ControllerMessage.Button.WDPAD);
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
        controllerState.leftTriggerMag = value;
    }

    public void rightTrigger(double value)
    {
        //value is how hard you press. Between 0-1.0
        controllerState.rightTriggerMag = value;
    }

    public void leftThumbMagnitude(double magnitude)
    {
        //magnitude is how hard you press. Between 0-1.0
        controllerState.leftThumbstickMagnitude = magnitude;
    }

    public void leftThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
        //@foxtrot94: Would rather this be kept in a vector to avoid code duplication
        controllerState.leftThumbstickMagnitude = (direction);
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
        controllerState.rightThumbstickMagnitude = magnitude;
    }

    public void rightThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
//        RightThumbstick.UpdateAngleDegrees(direction);
        controllerState.rightThumbstickMagnitude = (direction);
        //@foxtrot94: Would rather this be kept in a vector to avoid code duplication
        if (direction < 45 || direction > 315){
            controllerState.setRightThumbstickDirection('N');
        } else if (direction < 135){
            controllerState.setRightThumbstickDirection('E');
        } else if (direction < 225){
            controllerState.setRightThumbstickDirection('S');
        } else if (direction < 315){
            controllerState.setRightThumbstickDirection('W');
        } else {
            controllerState.setRightThumbstickDirection('Z');
        }
    }

    public void isConnected()
    {
        if (connectedController.isConnected()) {
            Log(" - Controller connected");
        }
        else {
            Log(" - Controller disconnected");
        }
    }

    public Controller(NetworkConnector messageConnector)
    {
        connector = messageConnector;
        //TODO: CHOOSE BETWEEN 32 AND 64 BIT!
        connectedController = new XboxController(
                System.getProperty("user.dir") +"\\"+path ,
                1,
                50,
                50);

        isConnected();
        ScheduledManager = Executors.newScheduledThreadPool(1);

        controllerState = new ControllerMessage();

        connectedController.addXboxControllerListener(this);
        connectedController.setLeftThumbDeadZone(0.2);
        connectedController.setRightThumbDeadZone(0.2);

        makePeriodicSender();
    }

    public void makePeriodicSender(){
        if (connector==null || ControllerMessageSender!=null || connector.IsBroken()){
            Log("Unable to make periodic controller message sender.");
            return;
        }

        ControllerMessageSender = ScheduledManager.scheduleAtFixedRate(
                () -> {
                    if(connector.HasActiveConnection() && !connector.IsBroken()) {
                        connector.SendMessage(controllerState);
                    }else{
                        ControllerMessageSender.cancel(false); //Don't interrupt yourself.
                        ControllerMessageSender = null;
                    }
                },
                1,1, TimeUnit.SECONDS
        );
    }
}
