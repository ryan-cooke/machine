package Machine.rpi;

import Machine.Common.Constants;

import java.net.InetAddress;

import static Machine.Common.Utils.Log;

/**
 * Entry Point for the Badger Code running on the Raspberry Pi
 */
public class MainPi {
    public static void main(String[] args){
        Constants.setActivePlatform(Constants.PLATFORM.BADGER_PI);

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
