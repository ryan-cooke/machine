package Machine.desktop;

import Machine.Common.Network.ControllerMessage;
import Machine.Common.Network.ReflectionMessage;
import Machine.Common.Network.TextCommandMessage;
import Machine.desktop.NetworkConnector;

import java.util.Scanner;

import static Machine.Common.Utils.Log;
import static Machine.Common.Utils.Prompt;

/**
 * Driver code for Desktop App
 */
public class MainDesktop {
    private static boolean isActive=true;
    private static boolean keepAlive=true;

    public static class MessageReader implements Runnable{
        private NetworkConnector Net;
        MessageReader(NetworkConnector nc){
            Net=nc;
        }

        @Override
        public void run() {
            //Four conditions can make this stop reading messages
            while(keepAlive && !Thread.currentThread().isInterrupted() && !Net.IsBroken() && Net.HasActiveConnection()){
                try {
                    Net.ReceiveMessage();
//                    Log(String.format("Received \'%s\'", Net.ReceiveMessage()));
                }
                catch (Exception e){
                    Log("Exception in Message Reader");
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
            NetworkConnector nc = new NetworkConnector(IP, 2017);
            MessageReader readerHandle = new MessageReader(nc);
            Thread readMessages = new Thread(readerHandle);
            Controller Xbox = new Controller(nc);
            input = "";
            keepAlive = true;
            isActive = true;
            readMessages.start();
            while (keepAlive) {
                input = Prompt('>', Kb);
                Log(String.format("Sending \"%s\"", input));

                //TODO: Cleanup the cases below
                nc.SendMessage(input);


                if(input.contains("CMD")){
                    Log(String.format("Sending TextCommandMessage \'%s\'",input.substring(4)));
                    nc.SendMessage(new TextCommandMessage(input.substring(4)));
                }

                if(input.contains("CALL")){
                    Log(String.format("Sending ReflectionMessage \'%s\'",input.substring(5)));
                    nc.SendMessage(new ReflectionMessage(input.substring(5)));
                }

                //Exit if we said "quit"
                if (input.contains("quit")) {
                    Log("Closing Connection");
                    isActive=false;
                    keepAlive = false;
                }
                //Retry if the connection broke
                else if (nc.IsBroken()) {
                    Log("Lost Connection... Reconnecting");
                    keepAlive = false;
                }
            }

            readerHandle.end();
            nc.End();
            nc = null;
        }while(isActive);

        System.exit(0);
    }
}
