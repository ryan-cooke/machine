package Machine.Common;

import java.util.Calendar;
import java.util.Scanner;

/**
 * Some common Utilities that might be needed
 */
public class Utils {
    public static void Log(String log){
        //Very lazy, just needed a quick timestamp
        java.sql.Timestamp ts = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        System.out.format("%s: %s\n", ts, log);
    }

    public static String Prompt(char symbol, Scanner kb){
        System.out.print(symbol+" ");
        return kb.nextLine();
    }
}
