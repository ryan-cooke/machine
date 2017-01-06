package Machine.desktop;

import Machine.Common.Constants;
import Machine.Common.Network.Command.IBadgerFunction;
import Machine.Common.Network.Command.TextCommandMessage;

import java.net.NetworkInterface;
import java.util.*;

import static Machine.Common.Network.Command.TextCommandMessage.getCommandHandlers;
import static Machine.Common.Utils.Log;
import static Machine.Common.Utils.Prompt;
import static java.net.NetworkInterface.getNetworkInterfaces;

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
        Constants.setActivePlatform(Constants.PLATFORM.DESKTOP);

        Scanner Kb = new Scanner(System.in);
        Log("Setting static IP");
        //CommandLineRunner.SetStaticIP();

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
            MainController mainController = new MainController(nc);
            input = "";
            keepAlive = true;
            isActive = true;
            readMessages.start();
            while (keepAlive) {
                input = Prompt('>', Kb);

                if(input.contains("CMD")){
                    //Check that it is not followed by something else
                    String[] keywords = input.split(" ");
                    if(keywords.length>1){
                        if(keywords[1].contains("LIST")){
                            Set<String> commands = TextCommandMessage.getCommandListName();
                            Log("Known command that can be called with CMD");
                            Log(String.format("   %s", Arrays.toString(commands.toArray())));
                        }
                        else if(keywords[1].contains("HELP")){
                            Log("Explaining Commands");
                            Collection<IBadgerFunction> functors = TextCommandMessage.getCommandHandlers();
                            for(IBadgerFunction functor : functors){
                                Log(String.format("\t%s | call: %s",functor.getClass().getSimpleName(),functor.Explain()));
                            }
                        }
                        else if (keywords[1].contains("AUTO")){
                            mainController.setAutonomousRunning(!mainController.isAutonomousRunning());
                        }
                        else {
                            Log(String.format("Sending TextCommandMessage \'%s\'", input.substring(4)));
                            nc.SendMessage(new TextCommandMessage(input.substring(4)));
                        }
                    }
                }
                else{
                    Log(String.format("Sending \"%s\"", input));
                    nc.SendMessage(input);
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

        //CommandLineRunner.SetDHCP();

        System.exit(0);
    }
}
