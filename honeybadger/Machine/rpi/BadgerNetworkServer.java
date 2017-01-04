package Machine.rpi;

import Machine.Common.Constants;
import Machine.Common.Network.BaseMsg;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.*;

import static Machine.Common.Utils.Log;
import static java.util.concurrent.Executors.newScheduledThreadPool;

/**
 * Creates and handles the server connection
 */
public class BadgerNetworkServer {
    private final ScheduledExecutorService ScheduledManager;

    //Port from which to send and receive commands
    private final int port = 2017;

    //The Honeybadger that should process messages
    private HoneybadgerV6 Badger;

    private ServerSocket connection;
    private Socket clientConnection;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    private BaseMsg LastReceivedMessage;
    private BaseMsg LastSentMessage;

    private boolean KeepAlive;

    private boolean PrintPayload;

    BadgerNetworkServer(HoneybadgerV6 badger){
        Badger = badger;
        ScheduledManager = Executors.newScheduledThreadPool(1);

        if(Constants.getActivePlatform() == Constants.PLATFORM.MOCK_PI){
            PrintPayload = true;
        }

        SetupNetwork();
    }

    protected void SetupNetwork(){
        if(connection!=null){
            return;
        }

        try{
            Log("Using port "+port);
            connection = new ServerSocket(port);
            KeepAlive = true;
        }
        catch (Exception e){
            e.printStackTrace();
            System.err.println("BadgerNetworkServer failed to setup server.");
            System.exit(-1);
        }
    }

    protected void WaitForConnect(){
        Log("Waiting for a remote connection");
        try{
            clientConnection = connection.accept();
            if(clientConnection!=null){
                Log("Connected to "+clientConnection.getInetAddress().toString());
                outStream = new ObjectOutputStream(clientConnection.getOutputStream());
                inStream = new ObjectInputStream(clientConnection.getInputStream());
            }
        }
        catch (Exception e){
            e.printStackTrace();
            KeepAlive=false;
        }
    }

    protected boolean Handshake(){
        //TODO: last part. Implement some verification with the badger to make sure they speak our language.
        //Currently does nothing.
        return true;
    }

    protected void SendMessage(BaseMsg message){
        if(outStream==null){
            //There's no one connected. don't send a message
            return;
        }

        LastSentMessage = message;
        try {
            outStream.writeObject(LastSentMessage);
        }
        catch (Exception e){
            e.printStackTrace();
            System.err.println("Message Not Sent: "+ LastReceivedMessage.getPayload());
            KeepAlive=false;
        }
    }

    protected String ReceiveMessage(){
        if(inStream==null){
            //There's no one connected. Don't keep this connection alive.
            KeepAlive=false;
            return "";
        }

        try{
            LastReceivedMessage = (BaseMsg) inStream.readObject();

            //If this wasn't a base message, send an error out.
            if(LastReceivedMessage ==null){
                SendMessage(new BaseMsg("Bad Message/Command!"));
            }

            LastReceivedMessage.Execute(Badger);
            return LastReceivedMessage.getPayload();
        }
        catch (Exception e){
            e.printStackTrace();
            KeepAlive=false;
            return "";
        }
    }

    protected void CloseAll(){
        try{
            if (clientConnection!=null && !clientConnection.isClosed()){
                Log("Closing Client Connection");
                clientConnection.close();
                clientConnection = null;
            }
            if(connection!=null && !connection.isClosed()){
                Log("Shutting Down Server");
                connection.close();
                connection = null;
            }

            Log("Releasing Streams");
            outStream = null;
            inStream = null;
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

    }

    public void Run(){
        //Main loop
        boolean shouldClose = false;
        boolean shouldQuit = false;
        do{
            String message="";
            shouldClose=false;
            shouldQuit=false;

            SetupNetwork();
            WaitForConnect();
            if(Handshake()){
                //Setup the regular message sender
                final ScheduledFuture<?> PeriodicSenderHandle = ScheduledManager.scheduleAtFixedRate(
                        new Runnable() {
                            @Override
                            public void run() {
                                SendMessage(new BaseMsg("RPi OK!"));
                            }
                        },
                        3,10, TimeUnit.SECONDS
                );

                while(KeepAlive && !shouldClose && !shouldQuit){
                    message = ReceiveMessage();
                    //only for DEBUG
                    if(PrintPayload) {
                        Log(String.format("RX: %s",message));
                    }
                    shouldClose = message.contains("close");
                    shouldQuit = message.contains("quit");
                }
                Log("Cancelling PeriodicSender");
                PeriodicSenderHandle.cancel(true);
                if(Badger!=null) {
                    Badger.STOP();
                }
            }
            else{
                Log("Handshake FAILED!");
                Log(String.format("Failed device IP: %s",clientConnection.getInetAddress().toString()));
            }

            Log("Cleaning up connections");
            CloseAll();
        }while(!shouldQuit);

        Log("Stopping BadgerNetworkServer Run");
    }
}
