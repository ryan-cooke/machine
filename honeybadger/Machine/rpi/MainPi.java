package Machine.rpi;

import Machine.Common.Constants;

import java.net.InetAddress;

import static Machine.Common.Utils.ErrorLog;
import static Machine.Common.Utils.Log;

/**
 * Entry Point for the Badger Code running on the Raspberry Pi
 */
public class MainPi {
    public static void main(String[] args){
        Constants.setActivePlatform(Constants.PLATFORM.BADGER_PI);

        try {
            //Setup the server
            Log("Starting server at IP: " + InetAddress.getLocalHost().toString());
            HoneybadgerV6 badger = HoneybadgerV6.getInstance();
            badger.STOP();
            BadgerNetworkServer ns = badger.getNetworkServer();
            ns.Run();

            ns = null;
        }
        catch(Exception e){
            ErrorLog("The RPI experienced an unrecoverable error",e);
            //DELAY! we need to not be running every second after we've had an unrecoverable error
            long now = System.currentTimeMillis();
            long target = now + 10000;
            while (target > System.currentTimeMillis());//Wait
            System.exit(-1);
        }

        Log("Ending");
        System.exit(0);
    }
}
