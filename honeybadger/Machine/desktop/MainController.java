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
    private boolean isAutonomousRunning;
    private Controller connectedController;
    private BadgerAutonomousController autonomousController;
    private ControllerMessage controllerState;

    private final ScheduledExecutorService ScheduledManager;
    private ScheduledFuture<?> ControllerMessageSender;



    public MainController(NetworkConnector messageConnector){
        connector = messageConnector;
        controllerState = new ControllerMessage();
        controllerState.Initialize();
        Controller connectedController = new Controller(controllerState);
        BadgerAutonomousController autonomousController =
                new BadgerAutonomousController(controllerState);

        ScheduledManager = Executors.newScheduledThreadPool(1);

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
                    if (isAutonomousRunning){
                        connector.SendMessage(new ControllerMessage(autonomousController.getControllerState()));
                    }
                    else if(connectedController.getXboxController().isConnected())
                        connector.SendMessage(new ControllerMessage(connectedController.getControllerState()));
                }
            },
            1,1, TimeUnit.SECONDS
        );
    }
}
