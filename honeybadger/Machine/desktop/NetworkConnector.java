package Machine.desktop;

import Machine.Common.Network.BaseMsg;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Creates a connection and sends messages down the wire
 */
public class NetworkConnector {
    String host;
    int port;

    Socket connection;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    BaseMsg LastMessage;

    NetworkConnector(String Host, int connectPort){
        host = Host;
        port = connectPort;
        try{
            connection = new Socket(host,port);
            outStream = new ObjectOutputStream(connection.getOutputStream());
            inStream = new ObjectInputStream(connection.getInputStream());
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
        LastMessage = null;
    }

    void SendMessage(String msg){
        if(connection!=null){
            LastMessage = new BaseMsg(msg);
            try {
                outStream.writeObject(LastMessage);
                //System.out.println("Machine.Common.Network.BaseMsg sent");
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println("Machine.Common.Network.BaseMsg Not Sent: "+LastMessage.getPayload());
            }
        }
    }

    void SendMessage(BaseMsg msg){
        if(connection!=null){
            LastMessage = msg;
            try {
                outStream.writeObject(LastMessage);
                //System.out.println("Machine.Common.Network.BaseMsg sent");
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println("Machine.Common.Network.BaseMsg Not Sent: "+LastMessage.getPayload());
            }
        }
    }

}
