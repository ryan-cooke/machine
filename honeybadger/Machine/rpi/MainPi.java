package Machine.rpi;

import java.net.InetAddress;

import static Machine.Common.Utils.OutLine;

/**
 * Driver code for Pi
 */
public class MainPi {
    //Quick test for network
    public static void main(String[] args){
        //Setup the server
        try {
            OutLine("Starting server at IP: " + InetAddress.getLocalHost().toString());
        }
        catch(Exception e){
            System.out.println(e.getStackTrace());
        }

        BadgerNetworkServer ns=null;
        try {
            Badger badger = Badger.getInstance();
            ns = badger.getNetworkServer();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        ns.WaitForConnect();

        String input="";
        while(!input.contains("quit")) {
            input = ns.ReceiveMessage();
            OutLine(input);
        }
        ns.CloseAll();
    }
}
