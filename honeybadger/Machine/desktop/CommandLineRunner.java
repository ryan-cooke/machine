package Machine.desktop;

import Machine.Common.Shell;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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

        networkAdapterName = getNetworkCardName();

        if (networkAdapterName == null) {
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

    private static String getNetworkCardName() {
        try {
            File fXmlFile = new File("config.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            if(!fXmlFile.exists()){
                Document doc = dBuilder.newDocument();
                Element networkCard = doc.createElement("networkCard");
                networkCard.setNodeValue("Wi-Fi");
                doc.appendChild(networkCard);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("config.xml"));
                transformer.transform(source, result);
                return "Wi-Fi";
            } else {
                Document doc = dBuilder.parse(fXmlFile);

                doc.getDocumentElement().normalize();
                Element netCard = (Element) doc.getElementsByTagName("networkCard").item(0);

                return netCard.getTextContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String args[]) {
        System.out.println(getNetworkCardName());
    }
}
