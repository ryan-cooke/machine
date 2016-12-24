package Machine.desktop;

import java.util.Scanner;

import static Machine.Common.Utils.Log;
import static Machine.Common.Utils.Prompt;

/**
 * Driver code for Desktop App
 */
public class MainDesktop {
    //Quick test for network
    public static void main(String[] args){
        Scanner Kb = new Scanner(System.in);
        boolean isActive=true;
        do {
            String IP = "192.168.0.1";
            Log(String.format("Enter RPi IP (Default %s): ", IP));
            String input = Kb.nextLine();
            if (!input.isEmpty()) {
                IP = input;
            }

            Log(String.format("Connecting to %s", IP));
            Machine.desktop.NetworkConnector nc = new Machine.desktop.NetworkConnector(IP, 2017);
            Controller Xbox = new Controller(nc);
            input = "";

            while (true) {
                input = Prompt('>', Kb);
                Log(String.format("Sending \"%s\"", input));
                nc.SendMessage(input);

                //Exit if we said "quit"
                if (input.contains("quit")) {
                    nc.End();
                    nc = null;
                    Log("Closing Connection");
                    isActive=false;
                    break;
                }

                //Retry if the connection broke
                if (nc.IsBroken()) {
                    nc.End();
                    nc = null;
                    Log("Lost Connection... Reconnecting");
                    break;
                }
            }
        }while(isActive);

        System.exit(0);
    }
}
