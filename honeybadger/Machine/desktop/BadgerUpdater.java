package Machine.desktop;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static Machine.Common.Utils.ErrorLog;
import static Machine.Common.Utils.Log;

/**
 * Class that handles updating the badger with new code
 */
public class BadgerUpdater {
    /**
     * Primary method to update and send the jar file to the badger.
     * Modified from Dhinakar's original StackOverflow answer
     * (See "how to transfer a file through SFTP in Java":
     *    http://stackoverflow.com/questions/14830146/how-to-transfer-a-file-through-sftp-in-java)
     * @param host
     * @param password
     */
    public static boolean sendUpdate (String host, String password) {
        final int SFTP_PORT = 22;
        final String targetDirectory = "/home/pi/intellij/out/production/Forge";
        final String user = "pi";

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        Log("Preparing the host information for transfer");

        boolean operationSuccessful=false;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, SFTP_PORT);
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            Log("Establishing secure connection.");
            session.connect();
            Log("Connection successful. Switching to Secure File Transfer Protocol.");
            channel = session.openChannel("sftp");
            channel.connect();

            Log("SFTP Channel Open. Transferring update...");
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(targetDirectory);

            //Open up the zip file
            //TODO: test in release
            String currentDir = System.getProperty("user.dir");
            if(currentDir.endsWith("honeybadger")){
                currentDir = currentDir+"\\executable";
            }
            ZipFile zip = new ZipFile(new File(String.format("%s\\bin\\BadgerV6-Desktop.jar",currentDir)));
            Enumeration<?> entries = zip.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry zippedFile = (ZipEntry)entries.nextElement();
                String zipFilename = zippedFile.getName();
                if (!zippedFile.isDirectory() && zipFilename.endsWith(".class") && zipFilename.startsWith("Machine")) {
                    channelSftp.put(zip.getInputStream(zippedFile), zippedFile.getName());
                }
            }

            Log("Update sent successfully to Honeybadger");
            operationSuccessful = true;
        } catch (Exception e) {
            ErrorLog("Exception occurred while updating the Honeybadger",e);
            e.printStackTrace();
        }
        finally{
            if(channelSftp!=null) {
                channelSftp.exit();
                Log("SFTP channel closed.");
            }

            if(channel!=null)
                channel.disconnect();
            if(session!=null)
                session.disconnect();
            Log("Update Session disconnected.");
        }

        return operationSuccessful;
    }
}
