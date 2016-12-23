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

        //Ask for IP
        Log("Enter RPi IP: ");
        String IP = Kb.nextLine();
        Machine.desktop.NetworkConnector nc = new Machine.desktop.NetworkConnector(IP,2017);
        Controller Xbox = new Controller(nc);
        String input="";
        while(true){
            input = Prompt('>',Kb);
            Log("Sending "+input);
            nc.SendMessage(input);
            if(input.contains("quit")){
                nc = null;
                break;
            }
        }
    }
}
