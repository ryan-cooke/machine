package Machine.Common.Network;

import Machine.Common.Utils;
import Machine.rpi.HoneybadgerV6;
import Machine.rpi.hw.BadgerMotorController;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.HashMap;

import static Machine.Common.Utils.Log;

/**
 * Class intended to call methods on remote targets
 */
public class ReflectionMessage extends BaseMsg {
    private static boolean reflectionIsMapped=false;

    private static HashMap<String,Object> ObjectMap;
    private static HashMap<String,Method> MethodMap;

    public ReflectionMessage(String message){
        payload=message;
    }

    public static void GenerateMap(HoneybadgerV6 badger){
        try {
            //Build the Object Table
            ObjectMap = new HashMap<>();
            ObjectMap.put("badger", badger);
            ObjectMap.put("honeybadger", badger);
            ObjectMap.put("pi", badger);

            Field BMCField = badger.getClass().getDeclaredField("motorController");
            BMCField.setAccessible(true);
            Object BadgerMotorController = (Object) BMCField.get(badger);
            ObjectMap.put("BMC",BadgerMotorController);
            ObjectMap.put("motors",BadgerMotorController);
            ObjectMap.put("hw",BadgerMotorController);

            Object NetworkController = badger.getNetworkServer();
            ObjectMap.put("net",NetworkController);
            ObjectMap.put("BNS",NetworkController);
        }
        catch (Exception e){
            e.printStackTrace();
            Log("Didn't finish ObjectMap");
            return;
        }

        try{
            //Build the Method Table
            MethodMap = new HashMap<>();
            Class badgerClass = badger.getClass();
            MethodMap.put("STOP", badgerClass.getDeclaredMethod("STOP"));
            MethodMap.put("left", badgerClass.getDeclaredMethod("strafeLeft"));
            MethodMap.put("right", badgerClass.getDeclaredMethod("strafeRight"));
            MethodMap.put("forward", badgerClass.getDeclaredMethod("moveForward"));
            MethodMap.put("backward", badgerClass.getDeclaredMethod("moveBackward"));

            Field BMCField = badger.getClass().getDeclaredField("motorController");
            BMCField.setAccessible(true);
            Object BadgerMotorController = (Object) BMCField.get(badger);
            Class motorControlClass = BadgerMotorController.getClass();
            MethodMap.put("PWM",motorControlClass.getDeclaredMethod("setPWM"));
        }
        catch (Exception e){
            e.printStackTrace();
            Log("Didn't finish MethodMap");
            return;
        }

        reflectionIsMapped=true;
    }

    public void Execute(Object context){
        HoneybadgerV6 badger = (HoneybadgerV6) context;
        if(badger==null || !Utils.DEBUG_MODE_ON){
            return;
        }

        //Code can be assumed to be executing on an RPi past this point
        if(!reflectionIsMapped){
            GenerateMap(badger);
        }

        try {
            //split as method call
            String[] methodCall = payload.split(" ");
            Object object = ObjectMap.get(methodCall[0]);

            Method function;
            if(MethodMap.containsKey(methodCall[1])) {
                function = MethodMap.get(methodCall[1]);
            }
            else{
                function = object.getClass().getDeclaredMethod(methodCall[1]);
            }
            function.setAccessible(true);

            //Finally, deal with all the parameters
            Object[] VarArgs = new Object[methodCall.length-2];
            for (int i = 3; i < methodCall.length; i++) {
                String arg = methodCall[i];
                Object param;
                //Is it a class reference?
                if(arg.contains(".")){
                    String[] pair = arg.split(".");
                    Class classPrototype = Class.forName(pair[0]);
                    Object classInstance = ObjectMap.get(pair[0]);
                    Field classField = classPrototype.getDeclaredField(pair[1]);
                    classField.setAccessible(true);
                    param = classField.get(classInstance);
                }
                else { //probably a number
                    param = NumberFormat.getInstance().parse(arg);
                }
                VarArgs[i-2]=param;
            }

            //Finally, call it
            function.invoke(object,VarArgs);
        }
        catch (Exception e){
            Log(String.format("Could not execute Reflection given by %s",payload));
            e.printStackTrace();
        }
    }
}
