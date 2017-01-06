package Machine.desktop;

import Machine.Common.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Class to wrap certain common interactions with Windows
 */
public class CommandLineRunner {
    public static void SetStaticIP(){
        Shell windows = new Shell();
        String networkAdapterName;
        final String staticIP = "192.168.0.42";
        final String netGateway = "192.168.0.1";
        final String netSubmask = "255.255.255.0";

        try {
            networkAdapterName = getNetworkCardName();
        } catch (Exception e) {
            networkAdapterName = "Wi-Fi";
        }


        final String command = String.format("netsh interface ip set address \"%s\" static %s %s %s 1",
                networkAdapterName,
                staticIP,netSubmask,netGateway);

        windows.Run(command);
    }

    public static void SetDHCP(){
        Shell windows = new Shell();
        String networkAdapterName;
        try {
            networkAdapterName = getNetworkCardName();
        } catch (Exception e) {
            networkAdapterName = "Wi-Fi";
        }

        final String command = String.format("netsh int ip set address name = \"%s\" source = dhcp",
                networkAdapterName);

        windows.Run(command,60000);
    }

    private static String getNetworkCardName() throws ParserConfigurationException, IOException, SAXException {
        File fXmlFile = new File("config.xml");
        fXmlFile.createNewFile();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();
        Element netCard = (Element) doc.getElementsByTagName("networkCard").item(0);

        return netCard.getTextContent();
    }
}
