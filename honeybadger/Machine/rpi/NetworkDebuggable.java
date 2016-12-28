package Machine.rpi;

import Machine.Common.Utils;

/**
 * Special Base class that provides a method to send network messages back to desktop
 */
public class NetworkDebuggable {
    protected void SendDebugMessage(String message){
        if(Utils.DEBUG_MODE_ON) {
            try {
                HoneybadgerV6.getInstance().sendToDesktop(message);
            } catch (Exception e) {
                System.err.println(String.format("Unexpected error occurred while sending:\n%s",message));
            }
        }
    }
}
