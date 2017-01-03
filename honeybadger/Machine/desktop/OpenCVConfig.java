package Machine.desktop;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import org.opencv.core.Scalar;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.xml.bind.ValidationEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import static Machine.Common.Utils.ErrorLog;

@SuppressWarnings("WeakerAccess")
public class OpenCVConfig extends JDialog {
    private JPanel contentPane;
    private JButton buttonSave;
    private JButton buttonCancel;
    private JPanel sliderPanels;

    private JSlider upperGreenHSlider;
    private JSlider upperGreenSSlider;
    private JSlider upperGreenVSlider;
    private JSlider upperGreenDilateSlider;
    private JSlider upperGreenBlurSlider;

    private JSlider upperBlueHSlider;
    private JSlider upperBlueSSlider;
    private JSlider upperBlueVSlider;
    private JSlider upperBlueDilateSlider;
    private JSlider upperBlueBlurSlider;

    private JSlider upperBlackHSlider;
    private JSlider upperBlackSSlider;
    private JSlider upperBlackVSlider;
    private JSlider upperBlackDilateSlider;
    private JSlider upperBlackBlurSlider;

    private JSlider upperRedHSlider;
    private JSlider upperRedSSlider;
    private JSlider upperRedVSlider;
    private JSlider upperRedDilateSlider;
    private JSlider upperRedBlurSlider;

    private JSlider upperYellowHSlider;
    private JSlider upperYellowSSlider;
    private JSlider upperYellowVSlider;
    private JSlider upperYellowDilateSlider;
    private JSlider upperYellowBlurSlider;

    private JSlider upperWhiteHSlider;
    private JSlider upperWhiteSSlider;
    private JSlider upperWhiteVSlider;
    private JSlider upperWhiteDilateSlider;
    private JSlider upperWhiteBlurSlider;

    private JSlider canny1;
    private JSlider canny2;
    private JSlider houghline1;
    private JSlider houghline2;
    private JSlider houghline3;

    private JSlider lowerGreenHSlider;
    private JSlider lowerGreenSSlider;
    private JSlider lowerGreenVSlider;
    private JSlider lowerGreenDilateSlider;
    private JSlider lowerGreenBlurSlider;

    private JSlider lowerBlueHSlider;
    private JSlider lowerBlueSSlider;
    private JSlider lowerBlueVSlider;
    private JSlider lowerBlueDilateSlider;
    private JSlider lowerBlueBlurSlider;

    private JSlider lowerBlackHSlider;
    private JSlider lowerBlackSSlider;
    private JSlider lowerBlackVSlider;
    private JSlider lowerBlackDilateSlider;
    private JSlider lowerBlackBlurSlider;

    private JSlider lowerRedHSlider;
    private JSlider lowerRedSSlider;
    private JSlider lowerRedVSlider;
    private JSlider lowerRedDilateSlider;
    private JSlider lowerRedBlurSlider;

    private JSlider lowerYellowHSlider;
    private JSlider lowerYellowSSlider;
    private JSlider lowerYellowVSlider;
    private JSlider lowerYellowDilateSlider;
    private JSlider lowerYellowBlurSlider;

    private JSlider lowerWhiteHSlider;
    private JSlider lowerWhiteSSlider;
    private JSlider lowerWhiteVSlider;
    private JSlider lowerWhiteDilateSlider;
    private JSlider lowerWhiteBlurSlider;

    public Color green;
    public Color blue;
    public Color black;
    public Color yellow;
    public Color red;
    public Color white;

    public Document doc;

    public class Color {
        public Values upper;
        public Values lower;
        public String name;

        public Color(String name, Values upper, Values lower) {
            this.name = name;
            this.upper = upper;
            this.lower = lower;
        }

        public Values getUpper() {
            return upper;
        }

        public void setUpper(Values upper) {
            this.upper = upper;
        }

        public Values getLower() {
            return lower;
        }

        public void setLower(Values lower) {
            this.lower = lower;
        }

        public Element toXMLNode() {
            Element color = doc.createElement("Color");
            color.setAttribute("Name", this.name);

            Element upper = this.upper.toXMLNode();
            upper.setAttribute("Bound", "Upper");
            color.appendChild(upper);

            Element lower = this.lower.toXMLNode();
            lower.setAttribute("Bound", "Lower");
            color.appendChild(lower);

            return color;
        }

    }

    public class Values {
        public double H;
        public double S;
        public double V;
        public double dilate;
        public double blur;

        public Values(double h, double s, double v, double dilate, double blur) {
            H = h;
            S = s;
            V = v;
            this.dilate = dilate;
            this.blur = blur;
        }

        public Values(double h, double s, double v) {
            H = h;
            S = s;
            V = v;
        }

        public Values(Scalar s){
            H = s.val[0];
            S = s.val[1];
            V = s.val[2];
        }

        public double getH() {
            return H;
        }

        public double getS() {
            return S;
        }

        public double getV() {
            return V;
        }

        public double getDilate() {
            return dilate;
        }

        public double getBlur() {
            return blur;
        }

        public void setH(double h) {
            H = h;
        }

        public void setS(double s) {
            S = s;
        }

        public void setV(double v) {
            V = v;
        }

        public void setDilate(double dilate) {
            this.dilate = dilate;
        }

        public void setBlur(double blur) {
            this.blur = blur;
        }

        public Element toXMLNode() {
            Element values = doc.createElement("values");

            Element h = doc.createElement("H");
            h.setAttribute("value", Double.toString(this.H));
            values.appendChild(h);

            Element s = doc.createElement("S");
            s.setAttribute("value", Double.toString(this.S));
            values.appendChild(s);

            Element v = doc.createElement("V");
            v.setAttribute("value", Double.toString(this.V));
            values.appendChild(v);

            Element dilate = doc.createElement("Dilate");
            dilate.setAttribute("value", Double.toString(this.dilate));
            values.appendChild(dilate);

            Element blur = doc.createElement("Blur");
            blur.setAttribute("value", Double.toString(this.blur));
            values.appendChild(blur);

            return values;
        }

    }

    public OpenCVConfig() {
        parseScalarsFromJPanelOpenCV();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonSave);
        ToolTipManager.sharedInstance().setInitialDelay(0);
        setInitialSliderValues();
        setTooltipListeners();
        buttonSave.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void setTooltipListeners() {
        for (Component c : sliderPanels.getComponents()) {
            if (c instanceof JPanel)
                for (Component d : ((JPanel) c).getComponents()) {
                    if (d instanceof JSlider) {
                        ((JSlider) d).addChangeListener((ChangeEvent event) -> {
                            int value = ((JSlider) d).getValue();
                            ((JSlider) d).setToolTipText(Integer.toString(value));
                        });
                    }
                }
        }
    }

    private void parseScalarsFromJPanelOpenCV() {
        Scalar[] colors = JPanelOpenCV.getColorScalars();
        green  = new Color("Green",  new Values(colors[0]), new Values(colors[1]));
        blue   = new Color("Blue",   new Values(colors[2]), new Values(colors[3]));
        black  = new Color("Black",  new Values(colors[4]), new Values(colors[5]));
        red    = new Color("Red",    new Values(colors[6]), new Values(colors[7]));
        yellow = new Color("Yellow", new Values(colors[8]), new Values(colors[9]));
    }

    private void setInitialSliderValues() {
        lowerGreenHSlider.setValue((int) green.lower.H);
        upperGreenHSlider.setValue((int) green.upper.H);
        lowerGreenSSlider.setValue((int) green.lower.S);
        upperGreenSSlider.setValue((int) green.upper.S);
        lowerGreenVSlider.setValue((int) green.lower.V);
        upperGreenVSlider.setValue((int) green.upper.V);

        lowerBlueHSlider.setValue((int) blue.lower.H);
        upperBlueHSlider.setValue((int) blue.upper.H);
        lowerBlueSSlider.setValue((int) blue.lower.S);
        upperBlueSSlider.setValue((int) blue.upper.S);
        lowerBlueVSlider.setValue((int) blue.lower.V);
        upperBlueVSlider.setValue((int) blue.upper.V);

        lowerBlackHSlider.setValue((int) black.lower.H);
        upperBlackHSlider.setValue((int) black.upper.H);
        lowerBlackSSlider.setValue((int) black.lower.S);
        upperBlackSSlider.setValue((int) black.upper.S);
        lowerBlackVSlider.setValue((int) black.lower.V);
        upperBlackVSlider.setValue((int) black.upper.V);

        lowerRedHSlider.setValue((int) red.lower.H);
        upperRedHSlider.setValue((int) red.upper.H);
        lowerRedSSlider.setValue((int) red.lower.S);
        upperRedSSlider.setValue((int) red.upper.S);
        lowerRedVSlider.setValue((int) red.lower.V);
        upperRedVSlider.setValue((int) red.upper.V);

        lowerYellowHSlider.setValue((int) yellow.lower.H);
        upperYellowHSlider.setValue((int) yellow.upper.H);
        lowerYellowSSlider.setValue((int) yellow.lower.S);
        upperYellowSSlider.setValue((int) yellow.upper.S);
        lowerYellowVSlider.setValue((int) yellow.lower.V);
        upperYellowVSlider.setValue((int) yellow.upper.V);
    }

    private void save() {
        green.lower.H = lowerGreenHSlider.getValue();
        green.upper.H = upperGreenHSlider.getValue();
        green.lower.S = lowerGreenSSlider.getValue();
        green.upper.S = upperGreenSSlider.getValue();
        green.lower.V = lowerGreenVSlider.getValue();
        green.upper.V = upperGreenVSlider.getValue();

        blue.lower.H = lowerBlueHSlider.getValue();
        blue.upper.H = upperBlueHSlider.getValue();
        blue.lower.S = lowerBlueSSlider.getValue();
        blue.upper.S = upperBlueSSlider.getValue();
        blue.lower.V = lowerBlueVSlider.getValue();
        blue.upper.V = upperBlueVSlider.getValue();

        black.lower.H = lowerBlackHSlider.getValue();
        black.upper.H = upperBlackHSlider.getValue();
        black.lower.S = lowerBlackSSlider.getValue();
        black.upper.S = upperBlackSSlider.getValue();
        black.lower.V = lowerBlackVSlider.getValue();
        black.upper.V = upperBlackVSlider.getValue();

        red.lower.H = lowerRedHSlider.getValue();
        red.upper.H = upperRedHSlider.getValue();
        red.lower.S = lowerRedSSlider.getValue();
        red.upper.S = upperRedSSlider.getValue();
        red.lower.V = lowerRedVSlider.getValue();
        red.upper.V = upperRedVSlider.getValue();

        yellow.lower.H = lowerYellowHSlider.getValue();
        yellow.upper.H = upperYellowHSlider.getValue();
        yellow.lower.S = lowerYellowSSlider.getValue();
        yellow.upper.S = upperYellowSSlider.getValue();
        yellow.lower.V = lowerYellowVSlider.getValue();
        yellow.upper.V = upperYellowVSlider.getValue();

        JPanelOpenCV.setGreen(new Scalar(green.upper.H, green.upper.S, green.upper.V), new Scalar(green.lower.H, green.lower.S, green.lower.V));
        JPanelOpenCV.setBlue(new Scalar(blue.upper.H, blue.upper.S, blue.upper.V), new Scalar(blue.lower.H, blue.lower.S, blue.lower.V));
        JPanelOpenCV.setBlack(new Scalar(black.upper.H, black.upper.S, black.upper.V), new Scalar(black.lower.H, black.lower.S, black.lower.V));
        //JPanelOpenCV.setRed(new Scalar(red.upper.H, red.upper.S, red.upper.V), new Scalar(red.lower.H, red.lower.S, red.lower.V));
        //JPanelOpenCV.setYellow(new Scalar(yellow.upper.H, yellow.upper.S, yellow.upper.V), new Scalar(yellow.lower.H, yellow.lower.S, yellow.lower.V));
    }

    private void saveToXML() {

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("OpenCVConfig");
            doc.appendChild(rootElement);
            rootElement.appendChild(green.toXMLNode());
            rootElement.appendChild(blue.toXMLNode());
            rootElement.appendChild(black.toXMLNode());
            rootElement.appendChild(red.toXMLNode());
            rootElement.appendChild(yellow.toXMLNode());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("C:\\file.xml"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onOK() {
        this.dispose();
    }

    private void onCancel() {
        this.dispose();
    }

    public static void main(String[] args) {
        OpenCVConfig dialog = new OpenCVConfig();
        dialog.pack();
        dialog.setVisible(true);
    }
}
