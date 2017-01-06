package Machine.Common.Network.Command;

import Machine.Common.Network.BaseMsg;
import Machine.Common.Shell;

import Machine.rpi.HoneybadgerV6;

/**
 * Special message for executing payloads on the native commandline.
 */
public class ShellCommandMessage extends BaseMsg{
    public ShellCommandMessage(String command){
        payload = command;
    }

    @Override
    public void Execute(Object context) {
        HoneybadgerV6 badger = (HoneybadgerV6)context;

        Shell bash = new Shell();
        //bash.Run(payload,1000); FUTURE doesn't work
        badger.sendDebugMessageToDesktop("\n"+bash.getLatestStandardOut());
    }
}
