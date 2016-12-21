package Machine.Common.Network;

import java.io.Serializable;

/**
 * Simple message over the net
 */
public class BaseMsg implements Serializable, INetCommand {
    protected String payload;

    public BaseMsg(){

    }

    public BaseMsg(String msg){
        payload = msg;
    }

    public String getPayload() {
        return payload;
    }

    public void Execute(Object context){
        System.out.format("BaseMessage: %s\n",payload);
    }

}
