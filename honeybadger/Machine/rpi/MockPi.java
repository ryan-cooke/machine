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

        BadgerNetworkServer ns = new BadgerNetworkServer(null);
        ns.Run();

        Log("Ending mock session");
        System.exit(0);
    }
}
