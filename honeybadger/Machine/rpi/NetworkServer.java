package Machine.rpi;

import Machine.Common.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Creates and handles the server connection
 */
public class NetworkServer {
    final int port = 2017;

    ServerSocket connection;
    Socket clientConnection;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    Message LastMessage;

    NetworkServer(){
        try{
            MainPi.Out("Using port "+port);
            MainPi.Newline();
            connection = new ServerSocket(port);
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void WaitForConnect(){
        MainPi.Out("Waiting for a remote connection");
        MainPi.Newline();
        try{
            clientConnection = connection.accept();
            if(clientConnection!=null){
                MainPi.Out("Connected to "+clientConnection.getInetAddress().toString());
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
            LastMessage = (Message) inStream.readObject();
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
