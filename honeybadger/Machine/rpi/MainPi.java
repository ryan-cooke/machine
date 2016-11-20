package Machine.rpi;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Scanner;

/**
 * Driver code for Pi
 */
public class MainPi {
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
