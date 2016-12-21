package Machine.Common;

import java.util.Calendar;
import java.util.Scanner;

/**
 * Created by Javier Fajardo on 2016-12-20.
 */
public class Utils {
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
}
