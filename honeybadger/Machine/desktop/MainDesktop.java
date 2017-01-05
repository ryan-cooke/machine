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
            BadgerAutonomousController auto = mainController.getAutonomousController();
            input = "";
            keepAlive = true;
            isActive = true;
            readMessages.start();
            while (keepAlive) {
                input = Prompt('>', Kb);

                //TODO: Cleanup the cases below
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
                        // SHOOT 0.5 0.5 2000 0.5 will move to other side at 0.5 throttle, get to center at 0.5 throttle for 2000 ms
                        // and rotate at a 0.5 throttle.
                        else if (keywords[1].contains("AUTO")){
                            mainController.setAutonomousRunning(true);
                            if (keywords[2] != null){
                                auto.placeBadger(StoD(keywords[2]),StoD(keywords[3]),Long.parseLong(keywords[4]),StoD(keywords[5]));
                            } else{
                                auto.placeBadger(0.5,0.5,2000,0.5);
                            }
                            mainController.setAutonomousRunning(false);
                        }
                        // SHOOT 0.5 0.5 3000 will start flywheel at 0.5, conveyors at 0.5 and last 3000 ms.
                        else if (keywords[1].contains("SHOOT")){
                            if(keywords[2] != null && keywords[3] != null){
                                mainController.setAutonomousRunning(true);
                                if(keywords[4] != null){
                                    auto.shootBalls(StoD(keywords[2]),StoD(keywords[3]),Long.parseLong(keywords[4]));
                                } else
                                auto.shootBalls(StoD(keywords[2]),StoD(keywords[3]));
                                mainController.setAutonomousRunning(false);
                            }
                        }
                        else if (keywords[1].contains("COLOR")){
                            if (keywords[2].contains("YELLOW")) {
                                JPanelOpenCV.setStartSide("yellow");
                            }else {
                                JPanelOpenCV.setStartSide("red");
                            }
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

    private static double StoD(String s){
        return Double.parseDouble(s);
    }
}
