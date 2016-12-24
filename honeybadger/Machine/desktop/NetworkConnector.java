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
    BaseMsg LastMessage;

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
        LastMessage = null;
    }

    void SendMessage(String msg){
        if(connection!=null){
            LastMessage = new BaseMsg(msg);
            SendMessage(LastMessage);
        }
    }

    void SendMessage(BaseMsg msg){
        if(connection!=null){
            LastMessage = msg;
            try {
                outStream.writeObject(LastMessage);
            }
            catch (Exception e){
                exceptionOccurred = true;
                e.printStackTrace();
                System.out.println("Message Not Sent: "+LastMessage.getPayload());
            }
        }
    }

    void End(){
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
}
