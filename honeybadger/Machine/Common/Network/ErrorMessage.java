package Machine.Common.Network;

import java.io.PrintWriter;
import java.io.StringWriter;

import static Machine.Common.Utils.ErrorLog;

/**
 * Class used to send error messages
 */
public class ErrorMessage extends BaseMsg {
    public ErrorMessage(String message, Exception except){
        StringWriter errors = new StringWriter();
        if(except!=null) {
            except.printStackTrace(new PrintWriter(errors));
        }
        payload = String.format("%s\n%s",errors.toString(),message);
    }

    public void Execute(Object context){
        ErrorLog(payload);
    }
}
