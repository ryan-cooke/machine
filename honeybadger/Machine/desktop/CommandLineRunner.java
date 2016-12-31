package Machine.desktop;

import Machine.Common.Shell;

/**
 * Class to wrap certain common interactions with Windows
 */
public class CommandLineRunner {
    public static void SetStaticIP(){
        Shell windows = new Shell();
        final String networkAdapterName = "Wi-Fi";
        final String staticIP = "192.168.0.42";
        final String netGateway = "192.168.0.1";
        final String netSubmask = "255.255.255.0";

        final String command = String.format("netsh interface ip set address \"%s\" static %s %s %s 1",
                networkAdapterName,
                staticIP,netSubmask,netGateway);

        windows.Run(command);
    }

    public static void SetDHCP(){
        Shell windows = new Shell();
        final String networkAdapterName = "Wi-Fi";

        final String command = String.format("netsh int ip set address name = \"%s\" source = dhcp",
                networkAdapterName);

        windows.Run(command,60000);
    }
}
