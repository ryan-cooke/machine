package Machine.desktop;

import Machine.Common.Network.ControllerMessage;
import ch.aplu.xboxcontroller.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static Machine.Common.Utils.Log;
import Machine.Common.Utils.Button;


public class Controller extends XboxControllerAdapter{

    private final ScheduledExecutorService ScheduledManager;

    private String path = "xboxcontroller64.dll"; //Change to choose between 32 and 64 bit.
    private NetworkConnector connector;
    private XboxController connectedController;

    private ControllerMessage controllerState;

    private ScheduledFuture<?> ControllerMessageSender;

    private void press(Button button){
        controllerState.setButtonsPressed(controllerState.getButtonsPressed()+1);
        controllerState.buttons.replace(button, true);
    }
    
    private void depress(Button button){
        controllerState.setButtonsPressed(controllerState.getButtonsPressed()-1);
        controllerState.buttons.replace(button, false);
    }
    
    public void buttonA(boolean pressed)
    {
        if(pressed){
            press(Button.A);
        }
        else{
            depress(Button.A);
        }
    }

    public void buttonB(boolean pressed)
    {
        if(pressed){
            press(Button.B);
        }
        else{
            depress(Button.B);
        }
    }

    public void buttonX(boolean pressed)
    {
        if(pressed){
            press(Button.X);
        }
        else{
            depress(Button.X);
        }
    }

    public void buttonY(boolean pressed)
    {
        if (pressed){
            press(Button.Y);
        }
        else{
            depress(Button.Y);
        }
    }

    public void back(boolean pressed)
    {
        if (pressed){
            press(Button.BACK);
        }
        else{
            depress(Button.START);
        }
    }

    public void start(boolean pressed)
    {
        if (pressed){
            press(Button.RBUMPER);
        }
        else{
            depress(Button.RBUMPER);
        }
    }

    public void leftShoulder(boolean pressed)
    {
        if (pressed){
            press(Button.LBUMPER);
        }
        else{
            depress(Button.LBUMPER);
        }
    }

    public void rightShoulder(boolean pressed)
    {
        if (pressed){
            press(Button.RBUMPER);
        }
        else{
            depress(Button.RBUMPER);
        }
    }

    public void leftThumb(boolean pressed)
    {
        if (pressed){
            press(Button.LTHUMB);
        }
        else{
            depress(Button.LTHUMB);
        }
    }

    public void rightThumb(boolean pressed)
    {
        if (pressed){
            press(Button.RTHUMB);
        }
        else{
            depress(Button.RTHUMB);
        }
    }

    public void dpad(int direction, boolean pressed) {
        if (pressed) {
//            SendMessage("Pressed dpad direction "+direction);
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
        } else{
            //Log("Unpressed dpad direction "+direction);
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

    public void leftTrigger(double value)
    {
        //value is how hard you press. Between 0-1.0
        controllerState.leftTriggerMagnitude = value;
    }

    public void rightTrigger(double value)
    {
        //value is how hard you press. Between 0-1.0
        controllerState.rightTriggerMagnitude = value;
    }

    public void leftThumbMagnitude(double magnitude)
    {
        //magnitude is how hard you press. Between 0-1.0
        controllerState.leftThumbstickMagnitude = magnitude;
        if( magnitude < 0.2){
            controllerState.leftThumbstickDirection = 'Z';
            controllerState.leftThumbstickRotation = 0.0;
        }
    }

    public void leftThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
        //@foxtrot94: Would rather this be kept in a vector to avoid code duplication
        controllerState.leftThumbstickRotation = (direction);
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
        if( magnitude < 0.2){
            controllerState.rightThumbstickDirection = 'Z';
            controllerState.rightThumbstickRotation = 0.0;
        }
    }

    public void rightThumbDirection(double direction)
    {
        //direction is angle. Between 0-360.0, at top
//        RightThumbstick.UpdateAngleDegrees(direction);
        controllerState.rightThumbstickRotation = (direction);
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

        String arch = System.getProperty("os.arch");
        System.out.println(arch);
        if(arch.contains("x86")){
            path="xboxcontroller.dll";
        }
        else{
            path="xboxcontroller64.dll";
        }
        connectedController = new XboxController(
                System.getProperty("user.dir") +"\\"+path ,
                1,
                50,
                50);

        isConnected();
        ScheduledManager = Executors.newScheduledThreadPool(1);

        controllerState = new ControllerMessage();
        controllerState.Initialize();

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
                        Log(controllerState.toString());
                        connector.SendMessage(new ControllerMessage(controllerState));
                    }else{
                        ControllerMessageSender.cancel(false); //Don't interrupt yourself.
                        ControllerMessageSender = null;
                    }
                },
                1,1, TimeUnit.SECONDS
        );
    }
}
