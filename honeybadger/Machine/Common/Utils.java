package Machine.Common;

import Machine.desktop.MainWindow;

import java.util.Calendar;
import java.util.Scanner;

/**
 * Some common Utilities that might be needed
 */
public class Utils {

    public static class Vector2D{
        public double x,y;
        private double Magnitude;
        private double Angle;

        public Vector2D(){}

        public Vector2D(double x, double y){
            this.x=x;
            this.y=y;
        }

        private void FromAngles(double mag, double angle){
            x = mag*Math.sin(angle);
            y = mag*Math.cos(angle);
        }

        public void UpdateAngleDegrees(double angle){
            Angle=angle;
            FromAngles(Magnitude,Math.toRadians(Angle));
        }

        public void UpdateMagnitude(double mag){
            Magnitude=mag;
            FromAngles(Magnitude,Math.toRadians(Angle));
        }

        public String toString(){
            return String.format("X: %f | Y: %f", x,y);
        }
    }

    public static void Log(String log){
        //Very lazy, just needed a quick timestamp
        java.sql.Timestamp ts = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        String formatted = String.format("%s: %s\n", ts, log);
        System.out.format(formatted);
        if(Constants.getActivePlatform()== Constants.PLATFORM.DESKTOP_GUI){
            MainWindow.writeToMessageFeed(formatted);
        }
    }

    public static void ErrorLog(String message){
        //Very lazy, just needed a quick timestamp
        java.sql.Timestamp ts = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        String formatted = String.format("%s: %s\n", ts, message);
        System.out.format(formatted);
        System.err.format(formatted);
        if(Constants.getActivePlatform() == Constants.PLATFORM.DESKTOP_GUI){
            MainWindow.writeToMessageFeed(formatted);
        }

        //Print immediately
        System.err.flush();
        System.out.flush();
    }

    public static String Prompt(char symbol, Scanner kb){
        System.out.print(symbol+" ");
        return kb.nextLine();
    }
}
