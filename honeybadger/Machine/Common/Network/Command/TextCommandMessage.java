package Machine.Common.Network.Command;

import Machine.Common.Network.BaseMsg;
import Machine.Common.Utils;
import Machine.rpi.HoneybadgerV6;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * Class to send debug, plain-text commands.
 *  A lot simpler/more reliable than ReflectionMessage.
 */
public class TextCommandMessage extends BaseMsg {
    protected static HashMap<String,IBadgerFunction> CommandMap;

    protected static void GenerateMap(){
        CommandMap = new HashMap<>();

        CommandMap.put("setPWM",new CMD.setPWM());
        CommandMap.put("setAbsPWM",new CMD.setAbsPWM());
        CommandMap.put("sweepPWM", new CMD.sweepPWM());
        CommandMap.put("setMotor",new CMD.setMotor());
        CommandMap.put("servo", new CMD.servo());
        CommandMap.put("flywheel", new CMD.flywheel());
        CommandMap.put("stop", new CMD.stop());
    }

    public TextCommandMessage(String msg){
        payload = msg;
    }

    @Override
    public void Execute(Object context) {
        HoneybadgerV6 badger = (HoneybadgerV6) context;
        if(badger==null || !Utils.DEBUG_MODE_ON){
            return;
        }

        if(CommandMap==null){
            GenerateMap();
        }

        String[] command = payload.split(" ");
        String[] callParameters = Arrays.copyOfRange(command,1,command.length);
        IBadgerFunction function = CommandMap.get(command[0]);

        //Call the function on the badger
        boolean success = function.Invoke(badger,callParameters);
        if(success){
            //Send an ACK message
            badger.sendDebugMessageToDesktop(String.format("Call to %s successful",command[0]));
        }
        else{
            //Send a critical message
            badger.sendMessageToDesktop(String.format("Error calling %s",command[0]));
        }
    }

    public static Set<String> getCommandListName(){
        if(CommandMap==null){
            GenerateMap();
        }
        return CommandMap.keySet();
    }

    public static Collection<IBadgerFunction> getCommandHandlers(){
        if(CommandMap==null){
            GenerateMap();
        }
        return CommandMap.values();
    }
}
