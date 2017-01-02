package Machine.desktop;

import Machine.Common.Network.ControllerMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * The autonomous controller class that will run at the beggining of the match
 */
public class BadgerAutonomousController {

    private Thread autoThread;

    private NetworkConnector connector;

    private ControllerMessage controllerState;

    public BadgerAutonomousController(NetworkConnector nc){
        connector = nc;
        controllerState = new ControllerMessage();
    }

    /**
     * Runs the autonomous script that begins sending messages to the RPi
     */
    public void TakeOver(){
        //TODO: Place State machine logic here

    }
}
