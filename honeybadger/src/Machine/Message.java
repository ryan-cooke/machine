package Machine;

import java.io.Serializable;

/**
 * Simple message over the net
 */
public class Message implements Serializable {
    protected String payload;

    public Message(String msg){
        payload = msg;
    }

    public String getPayload() {
        return payload;
    }

}
