package Machine.desktop;

import java.util.Scanner;

import static Machine.Common.Utils.Log;
import static Machine.Common.Utils.Prompt;

/**
 * Driver code for Desktop App
 */
public class MainDesktop {
    static boolean isActive=true;
    static boolean keepAlive=true;

    public static class ReadMessage implements Runnable{
        private Machine.desktop.NetworkConnector Net;
        ReadMessage(Machine.desktop.NetworkConnector nc){
            Net=nc;
        }

        @Override
        public void run() {
            while(keepAlive && !Thread.currentThread().isInterrupted()){
                try {
                    Log(String.format("Received \'%s\'", Net.ReceiveMessage()));
                }
                catch (Exception e){
                    Log("Closing Message Reader");
                }
            }
        }

        public void end(){
            Thread.currentThread().interrupt();
        }
    }

    //Quick test for network
    public static void main(String[] args){
        Scanner Kb = new Scanner(System.in);

        do {
            String IP = "192.168.0.1";
            Log(String.format("Enter RPi IP (Default %s): ", IP));
            String input = Kb.nextLine();
            if (!input.isEmpty()) {
                IP = input;
            }

            Log(String.format("Connecting to %s", IP));
            Machine.desktop.NetworkConnector nc = new Machine.desktop.NetworkConnector(IP, 2017);
            ReadMessage readerHandle = new ReadMessage(nc);
            Thread readMessages = new Thread(readerHandle);
            Controller Xbox = new Controller(nc);
            input = "";

            readMessages.start();
            while (keepAlive) {
                input = Prompt('>', Kb);
                Log(String.format("Sending \"%s\"", input));
                nc.SendMessage(input);

                //Exit if we said "quit"
                if (input.contains("quit")) {
                    nc.End();
                    nc = null;
                    Log("Closing Connection");
                    isActive=false;
                    keepAlive = false;
                }
                //Retry if the connection broke
                else if (nc.IsBroken()) {
                    nc.End();
                    nc = null;
                    Log("Lost Connection... Reconnecting");
                    keepAlive = false;
                }
            }

            readerHandle.end();
        }while(isActive);

        System.exit(0);
    }
}
