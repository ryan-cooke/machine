package Machine.rpi;

import Machine.Common.Constants;
import Machine.Common.Utils;

/**
 * Special Base class that provides a method to send network messages back to desktop
 */
public class NetworkDebuggable {
    protected void SendACK(String message){
        if(Constants.DEBUG_MODE_ON) {
            try {
                HoneybadgerV6.getInstance().sendMessageToDesktop(message);
            } catch (Exception e) {
                System.err.println(String.format("Unexpected error occurred while sending:\n%s",message));
            }
        }
    }

    protected void SendDebugMessage(String message){
        if(Constants.DEBUG_MODE_ON) {
            try {
                HoneybadgerV6.getInstance().sendMessageToDesktop(message);
            } catch (Exception e) {
                System.err.println(String.format("Unexpected error occurred while sending:\n%s",message));
            }
        }
    }

    protected void SendCriticalMessage(String message, Exception exception){
        if(Constants.DEBUG_MODE_ON) {
            try {
                HoneybadgerV6.getInstance().sendCriticalMessageToDesktop(message,exception);
            } catch (Exception e) {
                System.err.println(String.format("Unexpected error occurred while sending:\n%s",message));
            }
        }
    }
}
