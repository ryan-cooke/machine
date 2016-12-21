package Machine.desktop;

import java.util.Scanner;

import static Machine.Common.Utils.Newline;
import static Machine.Common.Utils.Out;
import static Machine.Common.Utils.Prompt;

/**
 * Driver code for Desktop App
 */
public class MainDesktop {
    //Quick test for network
    public static void main(String[] args){

        Scanner Kb = new Scanner(System.in);

        //Ask for IP
        Out("Enter RPi IP: ");
        String IP = Kb.nextLine();
        Machine.desktop.NetworkConnector nc = new Machine.desktop.NetworkConnector(IP,2017);
        Controller Xbox = new Controller(nc);
        String input="";
        while(!input.contains("quit")){
            input = Prompt('>',Kb);
            Out("Sending "+input);
            Newline();
            nc.SendMessage(input);
        }
    }
}
