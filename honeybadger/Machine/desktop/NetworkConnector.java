package Machine.desktop;

import Machine.Common.Network.BaseMsg;
import Machine.Common.Network.Command.IBadgerFunction;
import Machine.Common.Network.Command.ShellCommandMessage;
import Machine.Common.Network.Command.TextCommandMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static Machine.Common.Utils.ErrorLog;
import static Machine.Common.Utils.Log;

/**
 * Creates a connection and sends messages down the wire
 */
public class NetworkConnector {

    public static class MessageReader implements Runnable {
        private NetworkConnector Net;

        MessageReader(NetworkConnector nc) {
            Net = nc;
        }

        @Override
        public void run() {
            //Four conditions can make this stop reading messages
            while (!Thread.currentThread().isInterrupted() && !Net.IsBroken() && Net.HasActiveConnection()) {
                try {
                    Net.ReceiveMessage();
                } catch (Exception e) {
                    ErrorLog("Exception in Message Reader",e);
                }
            }
        }

        public void end() {
            Thread.currentThread().interrupt();
        }
    }


    String host;
    int port;

    boolean exceptionOccurred;

    Socket connection;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;

    private BaseMsg LastReceivedMessage;
    private BaseMsg LastSentMessage;

    public final int CONNECTION_FAILED = 1;
    public final int NC_END = 2;

    NetworkConnector(String Host, int connectPort) {
        host = Host;
        port = connectPort;
        exceptionOccurred = false;

        Log("Connecting to "+host+":"+port);
        OpenConnection();

        if (connection == null) {
            MainWindow.dieWithError("Connection to "+host+":"+port+" failed.");
        }

        Log(String.format("Connection to %s:%s established successfully", Host, connectPort));
        LastSentMessage = null;
    }

    private void OpenConnection(){
        int retries = 3;
        while (connection == null && retries > 0) {
            try {
                connection = new Socket(host, port);
                outStream = new ObjectOutputStream(connection.getOutputStream());
                inStream = new ObjectInputStream(connection.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.flush();
                System.err.flush();
                Log("waiting 10 seconds to retry");
            }

            if (connection == null) {
                try {
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                    MainWindow.dieWithError("Interrupted during retry wait period. Exiting");
                }
                retries -= 1;
            }
        }
    }

    void SendMessage(String msg) {
        if (connection != null) {
            LastSentMessage = new BaseMsg(msg);
            SendMessage(LastSentMessage);
        }
    }

    void SendMessage(BaseMsg msg) {
        if (connection != null) {
            LastSentMessage = msg;
            try {
                outStream.writeObject(LastSentMessage);
            } catch (Exception e) {
                exceptionOccurred = true;
                e.printStackTrace();
                Log("Message Not Sent: " + LastSentMessage.getPayload());
            }
        }
    }

    public String ReceiveMessage() {
        try {
            LastReceivedMessage = (BaseMsg) inStream.readObject();

            //If this wasn't a base message, send an error out.
            if(LastReceivedMessage ==null){
                Log("Bad Message!");
            }
            //Don't execute for now
            LastReceivedMessage.Execute(null);

            exceptionOccurred = connection.isClosed();
            return LastReceivedMessage.getPayload();
        } catch (Exception e) {
            //Don't do anything.
            //e.printStackTrace();
        }

        return "";
    }

    public void End() {
        if (!connection.isConnected() || connection==null) {
            return;
        }

        try {
            //Clean up everything
            inStream.close();
            outStream.close();
            connection.close();

            connection = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean IsBroken() {
        return exceptionOccurred;
    }

    public boolean HasActiveConnection() {
        return connection != null && connection.isConnected() &&
                !connection.isClosed() && !connection.isInputShutdown() && !connection.isOutputShutdown();
    }

    public boolean HandleMessage(String input) {
        if(input.contains("CMD")) {
            //Check that it is not followed by something else
            String[] keywords = input.split(" ");
            if (keywords.length > 1) {
                if (keywords[1].toLowerCase().contains("list")) {
                    Set<String> commands = TextCommandMessage.getCommandListName();
                    MainWindow.writeToMessageFeed("Known command that can be called with CMD");
                    for(String command : commands){
                        MainWindow.writeToMessageFeed(String.format("   %s",command));
                    }
                } else if (keywords[1].toLowerCase().contains("help")) {
                    MainWindow.writeToMessageFeed("Explaining Commands");
                    Collection<IBadgerFunction> functors = TextCommandMessage.getCommandHandlers();
                    for (IBadgerFunction functor : functors) {
                        MainWindow.writeToMessageFeed(String.format("   %s | call: %s", functor.getClass().getSimpleName(), functor.Explain()));
                    }
                } else {
                    MainWindow.writeToMessageFeed(String.format("Sending TextCommandMessage \'%s\'", input.substring(4)));
                    this.SendMessage(new TextCommandMessage(input.substring(4).trim()));
                }
            }
        }
        else if(input.startsWith("COLOR")){
            String[] keywords = input.split(" ");
            if(keywords.length > 1){
                String color = keywords[1];
                JPanelOpenCV.setStartSide(color);
                Log(String.format("Set start side %s",color));
            }
        }
        else if(input.startsWith("SH")){
            MainWindow.writeToMessageFeed(String.format("Sending Shell Message \'%s\'", input.substring(3)));
            this.SendMessage(new ShellCommandMessage(input.substring(3)));
        }
        else{
            MainWindow.writeToMessageFeed(String.format("Sending \"%s\"", input));
            this.SendMessage(input);
        }
        if (this.IsBroken()) {
            Log("Lost Connection... Reconnecting");
            return false;
        } else {
            return true;
        }
    }

    public void Refresh(){
        //Close and reopen basically...
        this.End();
        this.OpenConnection();
    }

    public String getHost() {
        return host;
    }
}
