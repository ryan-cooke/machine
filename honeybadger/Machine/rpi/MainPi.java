package Machine.rpi;

import java.net.InetAddress;

import static Machine.Common.Utils.Newline;
import static Machine.Common.Utils.Out;
import static Machine.Common.Utils.Prompt;

/**
 * Driver code for Pi
 */
public class MainPi {
    //Quick test for network
    public static void main(String[] args){
        //Setup the server
        try {
            Out("Starting server at IP: " + InetAddress.getLocalHost().toString());
            Newline();
        }
        catch(Exception e){
            System.out.println(e.getStackTrace());
        }

        NetworkServer ns = new NetworkServer();
        ns.WaitForConnect();
        Newline();

        String input="";
        while(!input.contains("quit")) {
            input = ns.ReceiveMessage();
            Out(input);
            Newline();
        }
        ns.CloseAll();
    }
}
