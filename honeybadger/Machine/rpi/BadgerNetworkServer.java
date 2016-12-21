package Machine.rpi;

import Machine.Common.Network.BaseMsg;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static Machine.Common.Utils.OutLine;

/**
 * Creates and handles the server connection
 */
public class BadgerNetworkServer {
    //Port from which to send and receive commands
    private final int port = 2017;

    //The Honeybadger that should process messages
    private Badger Machine;

    private ServerSocket connection;
    private Socket clientConnection;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private BaseMsg LastMessage;

    BadgerNetworkServer(Badger badger){
        Machine = badger;
        SetupNetwork();
    }

    BadgerNetworkServer(){
        SetupNetwork();
    }

    protected void SetupNetwork(){
        if(connection!=null){
            return;
        }

        try{
            OutLine("Using port "+port);
            connection = new ServerSocket(port);
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void WaitForConnect(){
        OutLine("Waiting for a remote connection");
        try{
            clientConnection = connection.accept();
            if(clientConnection!=null){
                OutLine("Connected to "+clientConnection.getInetAddress().toString());
                outStream = new ObjectOutputStream(clientConnection.getOutputStream());
                inStream = new ObjectInputStream(clientConnection.getInputStream());
            }
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public String ReceiveMessage(){
        try{
            LastMessage = (BaseMsg) inStream.readObject();
            LastMessage.Execute(Machine);
            return LastMessage.getPayload();
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
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
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

    }
}
