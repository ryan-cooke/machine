package Machine.Common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static Machine.Common.Utils.Log;

/**
 * Class for interacting with the Operating System Shell
 */
public class Shell {
    private Runtime osShell;

    private Process latestProcess;

    private String latestStandardOut;

    private int latestTimeout;

    public Shell(){
        osShell = Runtime.getRuntime();
        latestProcess = null;
        latestStandardOut = null;
        latestTimeout = 500;
    }

     public void Run(String command, int timeout){
        latestTimeout = timeout;
        Run(command);
     }

    public void Run(String command){
        try {
            latestProcess = osShell.exec(command);
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(latestProcess.getInputStream()));
            boolean completedNormally = latestProcess.waitFor(latestTimeout, TimeUnit.MILLISECONDS);

            String line;
            while((line=reader.readLine())!=null){
                buffer.append(line);
                buffer.append("\n");
            }
            if(!completedNormally){
                buffer.append("PROCESS TERMINATED DUE TO TIMEOUT");
            }

            Log(String.format("Successfully executed \'%s\'",command));
        }catch (Exception e){e.printStackTrace();}
    }

    public String getLatestStandardOut() {
        return latestStandardOut;
    }
}
