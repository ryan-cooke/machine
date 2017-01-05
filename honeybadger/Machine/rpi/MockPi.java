package Machine.rpi;

import Machine.Common.Constants;

import java.net.InetAddress;

import static Machine.Common.Utils.Log;

/**
 * Class for hosting a main function that can be used as a rapid prototype
 */
public class MockPi {
    public static void main(String[] args){
        Constants.setActivePlatform(Constants.PLATFORM.MOCK_PI);

        //Setup the server
        try {
            Log("Starting server at IP: " + InetAddress.getLocalHost().toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }

        try {
            HoneybadgerV6 badger = HoneybadgerV6.getInstance();
            badger.getNetworkServer().Run();
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

        Log("Ending mock session");
        System.exit(0);
    }
}
