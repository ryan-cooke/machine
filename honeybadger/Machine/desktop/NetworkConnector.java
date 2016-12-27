package Machine.desktop;

import Machine.Common.Network.BaseMsg;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static Machine.Common.Utils.Log;

/**
 * Creates a connection and sends messages down the wire
 */
public class NetworkConnector {
    String host;
    int port;

    boolean exceptionOccurred;

    Socket connection;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;

    private BaseMsg LastReceivedMessage;
    private BaseMsg LastSentMessage;

    NetworkConnector(String Host, int connectPort){
        host = Host;
        port = connectPort;
        exceptionOccurred = false;

        int retries=3;
        while(connection==null && retries>0) {
            try {
                connection = new Socket(host, port);
                outStream = new ObjectOutputStream(connection.getOutputStream());
                inStream = new ObjectInputStream(connection.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.flush();
                System.err.flush();
                Log("waiting 10 seconds to retry");
            }

            if(connection==null){
                try {
                    Thread.sleep(10000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log("Interrupted during retry wait period. Exiting");
                    System.exit(1);
                }
                retries-=1;
            }
        }

        if(connection==null){
            System.exit(1);
        }

        Log(String.format("Connection to %s:%s established successfully",Host,connectPort));
        LastSentMessage = null;
    }

    void SendMessage(String msg){
        if(connection!=null){
            LastSentMessage = new BaseMsg(msg);
            SendMessage(LastSentMessage);
        }
    }

    void SendMessage(BaseMsg msg){
        if(connection!=null){
            LastSentMessage = msg;
            try {
                outStream.writeObject(LastSentMessage);
            }
            catch (Exception e){
                exceptionOccurred = true;
                e.printStackTrace();
                System.out.println("Message Not Sent: "+ LastSentMessage.getPayload());
            }
        }
    }

    public String ReceiveMessage(){
        try{
            LastReceivedMessage = (BaseMsg) inStream.readObject();

            //If this wasn't a base message, send an error out.
            if(LastReceivedMessage ==null){
                Log("Bad Message!");
            }

            LastReceivedMessage.Execute(null);

            exceptionOccurred = connection.isClosed();
            return LastReceivedMessage.getPayload();
        }
        catch (Exception e){
            //Don't do anything.
            //e.printStackTrace();
        }

        return "";
    }

    public void End(){
        if(!connection.isConnected()){
            return;
        }

        try {
            connection.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean IsBroken() {
        return exceptionOccurred;
    }

    public boolean HasActiveConnection(){
        return connection!=null && connection.isConnected() &&
                !connection.isClosed() && !connection.isInputShutdown() && !connection.isOutputShutdown();
    }
}
