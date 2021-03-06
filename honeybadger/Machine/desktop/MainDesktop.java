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
            String IP = "192.168.0.69";
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
                        // "MOVE 0.5 0.5 2000 0.5" will move to other side at 0.5 throttle,
                        // get to center at 0.5 throttle for 2000 ms
                        // and rotate at a 0.5 throttle.
                        else if (keywords[1].contains("MOVE")){
                            mainController.setAutonomousRunning(true);
                            if (keywords[2] != null){
                                auto.moveBadger(StoD(keywords[2]),StoD(keywords[3]),
                                        StoL(keywords[4]),StoD(keywords[5]));
                            } else{
                                auto.moveBadger(0.5,0.5,2000,0.5);
                            }
                            mainController.setAutonomousRunning(false);
                        }
                        //"MOVENOCV 0.5 2000 0.5 2000 0.5 2000 will move to other side at 0.5 throttle for 2 seconds,
                        // get to center at 0.5 throttle for 2000ms,
                        // and rotate at a 0.5 throttle.
                        else if (keywords[1].contains("MOVENOCV")){
                            mainController.setAutonomousRunning(true);
                            if(keywords[2] != null){
                                auto.moveBadger(StoD(keywords[2]),StoL(keywords[3]),
                                        StoD(keywords[4]),StoL(keywords[5]),
                                        StoD(keywords[6]),StoL(keywords[7]));
                            } else{
                                auto.moveBadger(0.5,2000,0.5,2000,0.5,2000);
                            }
                        }
                        // SHOOT 0.5 0.5 3000 will start flywheel at 0.5,
                        // conveyors at 0.5 and last 3000 ms.
                        else if (keywords[1].contains("SHOOT")){
                            if(keywords[2] != null && keywords[3] != null){
                                mainController.setAutonomousRunning(true);
//                                if(keywords[4] != null){
//                                    auto.shootBalls(StoD(keywords[2]),StoD(keywords[3]),
//                                            StoL(keywords[4]));
//                                } else {
//                                    auto.shootBalls(StoD(keywords[2]), StoD(keywords[3]));
//                                }
                                mainController.setAutonomousRunning(false);
                            }
                        }
                        // Need to set starting color, default is red.
                        else if (keywords[1].contains("COLOR")){
                            if (keywords[2].contains("YELLOW")) {
                                JPanelOpenCV.setStartSide("yellow");
                            }else {
                                JPanelOpenCV.setStartSide("red");
                            }
                        }
                        //"MVFW 0.5 2000" will move forward at throttle 0.5 for 2000 ms
                        //"MVFW 0.5" will move forward at 0.5 throttle till reach opencv boundary
                        else if (keywords[1].contains("MVFW")){
                            mainController.setAutonomousRunning(true);
                            if (keywords[2] != null && keywords[3] != null){
                                auto.goForward(StoD(keywords[2]), StoL(keywords[3]));
                            } else if (keywords[2] != null){
                                auto.goForward(StoD(keywords[2]));
                            }
                            mainController.setAutonomousRunning(false);
                        }
                        //"MVBK 0.5 2000" will move backwards at throttle 0.5 for 2000 ms
                        else if (keywords[1].contains("MVBK")){
                            mainController.setAutonomousRunning(true);
                            if (keywords[2] != null && keywords[3] != null){
                                auto.goBackwards(StoD(keywords[2]), StoL(keywords[3]));
                            }
                            mainController.setAutonomousRunning(false);
                        }
                        //"STRR 0.5 2000" will strafe right at throttle 0.5 for 2000 ms
                        else if (keywords[1].contains("STRR")){
                            mainController.setAutonomousRunning(true);
                            if (keywords[2] != null && keywords[3] != null){
                                auto.strafeRight(StoD(keywords[2]), StoL(keywords[3]));
                            }
                            mainController.setAutonomousRunning(false);
                        }
                        //"STRL 0.5 2000" will strafe left at throttle 0.5 for 2000 ms
                        else if (keywords[1].contains("STRL")){
                            mainController.setAutonomousRunning(true);
                            if (keywords[2] != null && keywords[3] != null){
                                auto.strafeLeft(StoD(keywords[2]), StoL(keywords[3]));
                            }
                            mainController.setAutonomousRunning(false);
                        }
                        //"ROTL 0.5 2000" will rotate at throttle 0.5 for 2000 ms
                        //"ROTL 0.5" will roateLeft at 0.5 throttle till reach opencv blue post
                        else if (keywords[1].contains("ROTL")){
                            mainController.setAutonomousRunning(true);
                            if (keywords[2] != null && keywords[3] != null){
                                auto.rotateLeft(StoD(keywords[2]), StoL(keywords[3]));
                            } else if (keywords[2] != null){
                                auto.rotateLeftToPole(StoD(keywords[2]));
                            }
                            mainController.setAutonomousRunning(false);
                        }
                        //"ROTR 0.5 2000" will rotate at throttle 0.5 for 2000 ms
                        //"ROTR 0.5" will roate right at 0.5 throttle till reach opencv blue post
                        else if (keywords[1].contains("ROTR")){
                            mainController.setAutonomousRunning(true);
                            if (keywords[2] != null && keywords[3] != null){
                                auto.rotateRight(StoD(keywords[2]), StoL(keywords[3]));
                            } else if (keywords[2] != null){
                                auto.rotateRightToPole(StoD(keywords[2]));
                            }
                            mainController.setAutonomousRunning(false);
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
    private static Long StoL(String s){ return Long.parseLong(s);}
}
