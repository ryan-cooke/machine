package Machine.rpi;

import java.net.InetAddress;

import static Machine.Common.Utils.Log;

/**
 * Driver code for Pi
 */
public class MainPi {
    //Quick test for network
    public static void main(String[] args){
        //Setup the server
        try {
            Log("Starting server at IP: " + InetAddress.getLocalHost().toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }

        BadgerNetworkServer ns=null;
        try {
            HoneybadgerV6 badger = HoneybadgerV6.getInstance();
            badger.STOP();
            ns = badger.getNetworkServer();
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

        ns.Run();

        ns = null;
        Log("Ending");
        System.exit(0);
    }
}
