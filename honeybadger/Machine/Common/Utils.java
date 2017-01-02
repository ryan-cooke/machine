package Machine.Common;

import Machine.desktop.MainWindow;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public static float Clamp(float value, float min, float max){
        return Math.min(Math.max(value,min),max);
    }

    public static double Clamp(double value, double min, double max){
        return Math.min(Math.max(value,min),max);
    }

    public static int Clamp(int value, int min, int max){
        return Math.min(Math.max(value,min),max);
    }

    protected static SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    public static void Log(String log){
        //Make a timestamp to go with message
        Date date = new Date(Calendar.getInstance().getTime().getTime());
        String formatted = String.format("%s: %s\n", formatter.format(date), log);

        System.out.print(formatted);
        if(Constants.getActivePlatform()== Constants.PLATFORM.DESKTOP_GUI){
            MainWindow.writeToMessageFeed(formatted);
        }
    }

    public static void ErrorLog(String message){
        //Make a timestamp to go with message
        Date date = new Date(Calendar.getInstance().getTime().getTime());
        String formatted = String.format("%s: %s\n", formatter.format(date), message);

        System.out.flush();
        System.err.print(formatted);
        if(Constants.getActivePlatform() == Constants.PLATFORM.DESKTOP_GUI){
            MainWindow.writeToMessageFeed(formatted);
        }

        //Print immediately
        System.err.flush();
    }

    public static void ErrorLog(String message, Exception except){
        StringWriter errors = new StringWriter();
        if(except!=null) {
            except.printStackTrace(new PrintWriter(errors));
        }
        ErrorLog(String.format("%s\n%s",errors.toString(),message));
    }

    public static String Prompt(char symbol, Scanner kb){
        System.out.print(symbol+" ");
        return kb.nextLine();
    }
}
