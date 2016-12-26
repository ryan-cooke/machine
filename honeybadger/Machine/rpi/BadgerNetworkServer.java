package Machine.rpi;

import Machine.Common.Network.BaseMsg;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static Machine.Common.Utils.Log;

/**
 * Creates and handles the server connection
 */
public class BadgerNetworkServer {
    //Port from which to send and receive commands
    private final int port = 2017;

    //The Honeybadger that should process messages
    private HoneybadgerV6 Machine;

    private ServerSocket connection;
    private Socket clientConnection;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    private BaseMsg LastReceivedMessage;
    private BaseMsg LastSentMessage;

    private boolean KeepAlive;

    BadgerNetworkServer(HoneybadgerV6 badger){
        Machine = badger;
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

    public void WaitForConnect(){
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

    public boolean Handshake(){
        //TODO: last part
        //Currently does nothing.
        return true;
    }

    public void SendMessage(BaseMsg message){
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

    public String ReceiveMessage(){
        try{
            LastReceivedMessage = (BaseMsg) inStream.readObject();

            //If this wasn't a base message, send an error out.
            if(LastReceivedMessage ==null){
                SendMessage(new BaseMsg("Bad Message/Command!"));
            }

            LastReceivedMessage.Execute(Machine);
            return LastReceivedMessage.getPayload();
        }
        catch (Exception e){
            e.printStackTrace();
            KeepAlive=false;
        }

        return "";
    }

    public void CloseAll(){
        try{
            if (clientConnection!=null && !clientConnection.isClosed()){
                clientConnection.close();
                clientConnection = null;
            }
            if(connection!=null && !connection.isClosed()){
                connection.close();
                connection = null;
            }

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
        String Message="";
        do{
            SetupNetwork();
            WaitForConnect();

            if(Handshake()){
                while(KeepAlive && !Message.contains("quit")){
                    Message = ReceiveMessage();
                    //only for DEBUG
                    Log(Message);
                }
            }

            CloseAll();
        }while(!Message.contains("quit"));
    }
}
