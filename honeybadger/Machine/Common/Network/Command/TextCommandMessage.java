package Machine.Common.Network.Command;

import Machine.Common.Constants;
import Machine.Common.Network.BaseMsg;
import Machine.Common.Utils;
import Machine.rpi.HoneybadgerV6;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import static Machine.Common.Utils.Log;

/**
 * Class to send debug, plain-text commands.
 *  A lot simpler/more reliable than ReflectionMessage.
 */
public class TextCommandMessage extends BaseMsg {
    protected static HashMap<String,IBadgerFunction> CommandMap;

    protected static void GenerateMap(){
        CommandMap = new HashMap<>();

        //Populate with all commands
        Class[] commandList = CMD.class.getClasses();
        for (Class registeredCommand:commandList) {
            if(Modifier.isAbstract(registeredCommand.getModifiers())){
                continue;
            }

            try {
                CommandMap.put(registeredCommand.getSimpleName(), (IBadgerFunction) registeredCommand.newInstance());
            }catch (Exception e){
                Log("Unable to correctly generate command mapping");
            }
        }

    }

    public TextCommandMessage(String msg){
        payload = msg;
    }

    @Override
    public void Execute(Object context) {
        HoneybadgerV6 badger = (HoneybadgerV6) context;
        if(badger==null){
            return;
        }

        if(CommandMap==null){
            GenerateMap();
        }

        String[] command = payload.split(" ");
        String[] callParameters = Arrays.copyOfRange(command,1,command.length);
        IBadgerFunction function = CommandMap.get(command[0]);

        //Call the function on the badger and catch possible exceptions
        try {
            boolean success = function.Invoke(badger, callParameters);

            String message = success?
                    String.format("Call to %s successful",command[0])
                    : String.format("Error calling %s",command[0]);

            badger.sendDebugMessageToDesktop(message);
        }catch (Exception e){
            e.printStackTrace();
            badger.sendCriticalMessageToDesktop(String.format("Error invoking %s",payload),e);
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
