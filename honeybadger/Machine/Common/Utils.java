package Machine.Common;

import java.util.Calendar;
import java.util.Scanner;

/**
 * Some common Utilities that might be needed
 */
public class Utils {
    /**
     * Check to see if debug mode is on.
     */
    public static boolean DEBUG_MODE_ON = true;

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
//            return String.format("Angle: %f | Mag: %f",Angle,Magnitude);
            return String.format("X: %f | Y: %f", x,y);
        }
    }

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
