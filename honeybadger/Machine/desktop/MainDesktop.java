package Machine.desktop;

import java.util.Calendar;
import java.util.Scanner;

/**
 * Driver code for Desktop App
 */
public class MainDesktop {
    public static void Newline(){
        System.out.println();
    }

    public static void Out(String log){
        //Very lazy, just needed a quick timestamp
        java.sql.Timestamp ts = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        System.out.print(ts+": "+log);
    }

    public static String Prompt(char symbol, Scanner kb){
        System.out.print(symbol+" ");
        return kb.nextLine();
    }

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
