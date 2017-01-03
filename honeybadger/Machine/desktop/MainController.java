package Machine.desktop;

import Machine.Common.Network.ControllerMessage;
import ch.aplu.xboxcontroller.XboxController;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static Machine.Common.Utils.Log;

/**
 * Sends messages between the main and autonomous controller.
 */
public class MainController {

    private NetworkConnector connector;
    private Controller connectedController;
    private BadgerAutonomousController autonomousController;
    private ControllerMessage controllerState;

    private final ScheduledExecutorService ScheduledManager;
    private ScheduledFuture<?> ControllerMessageSender;



    public MainController(NetworkConnector messageConnector){
        connector = messageConnector;
        controllerState = new ControllerMessage();
        controllerState.Initialize();
        Controller xbox = new Controller(controllerState);
        BadgerAutonomousController autonomousController =
                new BadgerAutonomousController(controllerState);

        ScheduledManager = Executors.newScheduledThreadPool(1);

        makePeriodicSender();

    }
//TODO: Need to make autonomous take control of the network connector for scripts.
    public void makePeriodicSender(){
        if (connector==null || ControllerMessageSender!=null || connector.IsBroken()){
            Log("Unable to make periodic controller message sender.");
            return;
        }
        ControllerMessageSender = ScheduledManager.scheduleAtFixedRate(
                () -> {
                    if(connector.HasActiveConnection() && !connector.IsBroken()) {
                        if(connectedController.getXboxController().isConnected())
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
