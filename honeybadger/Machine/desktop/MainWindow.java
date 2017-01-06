package Machine.desktop;

import Machine.Common.Constants;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;

import static Machine.Common.Utils.ErrorLog;
import static Machine.Common.Utils.Log;

@SuppressWarnings("ALL")
public class MainWindow {
    private static MainWindow singleton;
    private static JFrame mainFrame;

    private NetworkConnector networkBus;
    private NetworkConnector.MessageReader messageReader;

    private Thread networkThread;
    private Thread videoThread;
    private Thread updaterThread;
    private Thread autonomousThread;

    private JPanel contentPane;
    private JButton buttonAutonomous;
    private JButton buttonStop;
    private JFormattedTextField Prompt;
    private JMenuBar menuBar;
    private JMenu file;
    private JMenu view;
    private JMenu opencv;
    private JMenu opencvBuffers;
    private JMenuItem update;
    private JMenuItem exit;
    private JMenuItem fontSizeIncrease;
    private JMenuItem fontSizeDecrease;
    private JMenuItem openCVConfigMenuItem;
    private JMenuItem regularBuffer;
    private JMenuItem cannyBuffer;
    private JMenuItem houghBuffer;
    private JMenuItem changeStream;

    private JTextArea messageFeed;
    private JPanelOpenCV videoPanel;

    private final String promptChar = "> ";
    private ArrayList<String> inputHistory;
    private int inputOffset;
    private double fontSize;

    private static String ConnectionIP;

    private static MainController Controller;

    private int selectedCamera;
    private BadgerAutonomousController BAC;

    private MainWindow() {

        //CommandLineRunner.SetStaticIP();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = 0.75 * screenSize.getWidth();
        double height = 0.75 * screenSize.getHeight();

        mainFrame = new JFrame("Badger Controller");
        mainFrame.setSize((int) width, (int) height);
        mainFrame.setLayout(new GridLayout(1, 1));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                onPressExit();
            }
        });

        mainFrame.add(contentPane);
        initilializeMenu();
        mainFrame.setVisible(true);

        inputHistory = new ArrayList<>(40);
        inputOffset = 0;
        Prompt.setText(promptChar);

        try {
            //Choose and load the dlls for the correct arch
            String arch = System.getProperty("os.arch");
            System.out.println(arch);
            if (arch.contains("x86")) {
                System.loadLibrary("opencv_java310");
                System.loadLibrary("opencv_ffmpeg310");
            } else {
                System.loadLibrary("opencv_java310_64");
                System.loadLibrary("opencv_ffmpeg310_64");
            }

            JPanelOpenCV.processedImage = ImageIO.read(new File("maxresdefault.jpg"));
            Mat original = new Mat(JPanelOpenCV.processedImage.getHeight(), JPanelOpenCV.processedImage.getWidth(), CvType.CV_8UC3);
            original.put(0, 0, ((DataBufferByte) JPanelOpenCV.processedImage.getRaster().getDataBuffer()).getData());
            Mat reduced = new Mat();
            Size newSize = new Size(640, 480);
            Imgproc.resize(original, reduced, newSize);

            JPanelOpenCV.processedImage = JPanelOpenCV.MatToBufferedImage(reduced);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame.invalidate();
        mainFrame.repaint();

        registerCallbacks();
        //Below are listeners being tested
        Prompt.registerKeyboardAction(actionEvent -> {
            String input = Prompt.getText().substring(2);
            if (input.length() > 0) {
                Prompt.setText(promptChar);

                inputHistory.add(input);
                //TODO: Add autonomous phase command here

                if (singleton.networkBus != null) {
                    boolean messageSuccess = singleton.networkBus.HandleMessage(input);
                    if (!messageSuccess) {
                        resetConnection();
                    }
                }
            }
            inputOffset = 0;
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);

        Prompt.registerKeyboardAction(actionEvent -> {
            Document rawIn = Prompt.getDocument();
            Cursor cursor = Prompt.getCursor();
            int pos = Prompt.getCaretPosition();
            if (rawIn.getLength() > promptChar.length() && pos > promptChar.length()) {
                try {
                    rawIn.remove(Prompt.getCaretPosition() - 1, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), JComponent.WHEN_FOCUSED);

        Prompt.registerKeyboardAction(actionEvent -> {
            //Try getting previous commands
            inputOffset += 1;
            previousInputLookup();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), JComponent.WHEN_FOCUSED);

        Prompt.registerKeyboardAction(actionEvent -> {
            inputOffset -= 1;
            previousInputLookup();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED);

        videoPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                startVideoStream();
            }
        });


    }

    private void initilializeMenu() {
        menuBar = new JMenuBar();

        file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);

        view = new JMenu("View");
        view.setMnemonic(KeyEvent.VK_V);

        opencv = new JMenu("OpenCV");
        opencv.setMnemonic(KeyEvent.VK_O);

        exit = new JMenuItem("Exit");
        exit.setMnemonic(KeyEvent.VK_E);
        exit.setToolTipText("If you really need a tool tip for this button, you shouldn't be in engineering");

        update = new JMenuItem("Update badger");
        update.setToolTipText("Update the Honeybadger V6 remotely");
        update.addActionListener(e -> runRemoteUpdate());

        openCVConfigMenuItem = new JMenuItem("OpenCV Config");
        openCVConfigMenuItem.setMnemonic(KeyEvent.VK_C);
        openCVConfigMenuItem.setToolTipText("Open the OpenCV Configuration panel");
        openCVConfigMenuItem.addActionListener((ActionEvent event) -> {
            OpenCVConfig.main(new String[0]);
        });

        //reg canny houghe
        opencvBuffers = new JMenu("Change OpenCV Buffer");
        opencvBuffers.setMnemonic(KeyEvent.VK_B);

        regularBuffer = new JMenuItem("Regular");
        regularBuffer.setMnemonic(KeyEvent.VK_R);
        regularBuffer.addActionListener(e -> {
            JPanelOpenCV.setDrawingBuffer(JPanelOpenCV.BUFFER_TYPE.REGULAR);
        });

        cannyBuffer = new JMenuItem("Canny");
        cannyBuffer.setMnemonic(KeyEvent.VK_C);
        cannyBuffer.addActionListener(e -> {
            JPanelOpenCV.setDrawingBuffer(JPanelOpenCV.BUFFER_TYPE.CANNY);
        });

        houghBuffer = new JMenuItem("Hough");
        houghBuffer.setMnemonic(KeyEvent.VK_H);
        houghBuffer.addActionListener(e -> {
            JPanelOpenCV.setDrawingBuffer(JPanelOpenCV.BUFFER_TYPE.HOUGH);
        });

        changeStream = new JMenuItem("Change Stream");
        changeStream.setMnemonic(KeyEvent.VK_C);
        changeStream.addActionListener(e -> {
            if (selectedCamera == 0){
                resetVideoFeed(8080);
            } else if (selectedCamera == 1){
                resetVideoFeed(8090);
            }
        });

        fontSizeIncrease = new JMenuItem("Increase Font Size");
        fontSizeIncrease.setMnemonic(KeyEvent.VK_PLUS);
        fontSizeIncrease.setToolTipText("Increases the font size");
        fontSizeIncrease.addActionListener(e -> increaseFontSize());

        fontSizeDecrease = new JMenuItem("Decrease Font Size");
        fontSizeDecrease.setMnemonic(KeyEvent.VK_MINUS);
        fontSizeDecrease.setToolTipText("Decrease the font size");
        fontSizeDecrease.addActionListener(e -> decreaseFontSize());

        file.add(update);
        file.add(exit);

        view.add(fontSizeIncrease);
        view.add(fontSizeDecrease);

        opencvBuffers.add(regularBuffer);
        opencvBuffers.add(cannyBuffer);
        opencvBuffers.add(houghBuffer);

        opencv.add(openCVConfigMenuItem);
        opencv.add(changeStream);
        opencv.add(opencvBuffers);

        menuBar.add(file);
        menuBar.add(view);
        menuBar.add(opencv);
        mainFrame.setJMenuBar(menuBar);
    }

    private void setFontSize(double size, Component c) {
        Font font = c.getFont().deriveFont((float) size);
        c.setFont(font);
    }

    private void increaseFontSize() {
        double size = messageFeed.getFont().getSize();
        size *= 1.4;
        fontSize = size;
        setFontSize(size, messageFeed);
        setFontSize(size, buttonStop);
        setFontSize(size, buttonAutonomous);
        setFontSize(size, Prompt);
        setFontSize(size, exit);
        setFontSize(size, fontSizeIncrease);
        setFontSize(size, fontSizeDecrease);
        setFontSize(size, openCVConfigMenuItem);
        setFontSize(size, file);
        setFontSize(size, view);
        setFontSize(size, update);
        contentPane.updateUI();
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private void decreaseFontSize() {
        double size = messageFeed.getFont().getSize();
        size *= 0.3;
        fontSize = size;
        setFontSize(size, messageFeed);
        setFontSize(size, buttonStop);
        setFontSize(size, buttonAutonomous);
        setFontSize(size, Prompt);
        setFontSize(size, exit);
        setFontSize(size, fontSizeIncrease);
        setFontSize(size, fontSizeDecrease);
        setFontSize(size, openCVConfigMenuItem);
        setFontSize(size, file);
        setFontSize(size, view);
        setFontSize(size, update);
        contentPane.updateUI();
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private void registerCallbacks() {
        buttonAutonomous.addActionListener(e -> onPressStartAutonomous());

        buttonStop.addActionListener(e -> onPressStop());

        // call onPressExit() on ESCAPE
        contentPane.registerKeyboardAction(e -> onPressExit(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onPressStop() {
        if (singleton.networkBus != null) {
            if(Controller.isAutonomousRunning()){
                Controller.setAutonomousRunning(false);
            }
            if(autonomousThread!=null){
                autonomousThread.stop();
            }

            boolean messageSuccess = singleton.networkBus.HandleMessage("CMD stop");
            if (!messageSuccess) {
                resetConnection();
            }
            CommandLineRunner.SetDHCP();
        }
    }

    private void previousInputLookup() {
        int historyIndex = inputHistory.size() - inputOffset;
        if (historyIndex >= 0 && historyIndex < inputHistory.size()) {
            Prompt.setText(String.format("%s%s", promptChar, inputHistory.get(historyIndex)));
        }
        //Clamp it otherwise
        else if (historyIndex < 0) {
            inputOffset = inputHistory.size();
        } else if (historyIndex >= inputHistory.size()) {
            inputOffset = 0;
        }
    }

    private void runRemoteUpdate() {
        if (updaterThread != null) {
            JOptionPane.showMessageDialog(null,
                    "An update is in progress. Please wait",
                    "Update Transmission Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        final int[] option = {1};
        JDialog dialog = new JDialog();
        JPanel panel = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JLabel label = new JLabel("Remote host password:");
        JPasswordField pass = new JPasswordField(20);
        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Cancel");
        ok.addActionListener(e -> {
            char[] passwordC = pass.getPassword();
            if (passwordC.length > 0) {
                String password = new String(passwordC);
                String finalPassword = password;
                updaterThread = new Thread(() -> {
                    boolean success = BadgerUpdater.sendUpdate(ConnectionIP, finalPassword);
                    //resetConnection();
                    updaterThread = null;
                });
                updaterThread.start();
            } else {
                Log("No password was entered");
            }
            dialog.dispose();
        });
        cancel.addActionListener(e -> {
            dialog.dispose();
            Log("Update canceled");
        });

        panel3.add(label);
        panel3.add(pass);
        panel2.add(ok);
        panel2.add(cancel);
        panel.add(panel3);
        panel.add(panel2);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        pass.requestFocusInWindow();
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private void startVideoStream() {
        if (videoThread == null) {
            MainWindow.writeToMessageFeed("Opening video stream...");
            JPanelOpenCV.instance = videoPanel;
            videoThread = new Thread(() -> {
                try {
                    JPanelOpenCV.SetConnectionHost(ConnectionIP, 8090);
                    videoPanel.startLoop();
                } catch (Exception e) {
                    MainWindow.writeToMessageFeed("Failed to open video stream");
                    e.printStackTrace();
                } finally {
                    videoThread = null;
                }
            });
            videoThread.start();
        }
    }

    private void resetVideoFeed(int port) {
        if (videoThread != null) {
            videoPanel.renderActive(false);
            videoThread = null;
        }
        if (videoThread == null) {
            MainWindow.writeToMessageFeed("Opening video stream...");
            JPanelOpenCV.instance = videoPanel;
            videoThread = new Thread(() -> {
                try {
                    JPanelOpenCV.SetConnectionHost(ConnectionIP, port);
                    videoPanel.startLoop();
                } catch (Exception e) {
                    MainWindow.writeToMessageFeed("Failed to open video stream");
                    e.printStackTrace();
                } finally {
                    videoThread = null;
                }
            });
            videoThread.start();
        }
    }

    private void onPressStartAutonomous() {
        if(autonomousThread==null){
            Log("Autonomous controller is already running!");
            return;
        }
        autonomousThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Controller.setAutonomousRunning(true);
                Controller.getAutonomousController().TakeOver();
                Controller.setAutonomousRunning(false);
            }
        });
        autonomousThread.start();
    }

    private void onPressExit() {
        // add your code here if necessary
        int dialogResult = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to quit?", "Exit Warning",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (dialogResult == JOptionPane.YES_OPTION) {
            try {
                networkBus.SendMessage("close");
                networkBus.End();
                CommandLineRunner.SetDHCP();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.exit(0);
            }
        }
    }

    private void resetConnection() {
        Log("Connection died. Attempting to reset...");
        String ConnectionIP = singleton.networkBus.host;

        singleton.networkBus.End();
        singleton.messageReader.end();

        singleton.networkBus = new NetworkConnector(ConnectionIP, 2017);
        singleton.messageReader = new NetworkConnector.MessageReader(singleton.networkBus);
        Thread readMessages = new Thread(singleton.messageReader);
        Controller.Reinitialize(singleton.networkBus);
        readMessages.start();
    }

    synchronized public static void writeToMessageFeed(String input) {
        if (singleton == null) {
            return;
        }
        singleton.messageFeed.append(input);
        if (!input.endsWith("\n")) {
            singleton.messageFeed.append("\n");
        }
        singleton.messageFeed.setCaretPosition(singleton.messageFeed.getDocument().getLength());
    }

    synchronized public static void dieWithError(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "A death has occured",
                JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    public static String promptForIP() {
        String ConnectionIP = JOptionPane.showInputDialog(
                "Honeybadger IP: ",
                "192.168.0.1");

        if (ConnectionIP == null) {
            JOptionPane.showMessageDialog(null,
                    "Honeybadger Command requires a network IP to run", "IP Needed",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        return ConnectionIP;
    }

    public static void main(String[] args) {
        Constants.setActivePlatform(Constants.PLATFORM.DESKTOP_GUI);

        if (Toolkit.getDefaultToolkit().getScreenSize().getWidth() > 1900) {
            Font highDPI = new Font(null, Font.PLAIN, 24);
            UIManager.put("MenuBar.font", highDPI);
            UIManager.put("Menu.font", highDPI);
            UIManager.put("MenuItem.font", highDPI);
            UIManager.put("Slider.thumbHeight", 34);
            UIManager.put("Slider.thumbWidth", 34);
            UIManager.put("Slider.trackWidth", 5);
            UIManager.put("OptionPane.messageFont", highDPI);
            UIManager.put("OptionPane.buttonFont", highDPI);
            UIManager.put("TextField.font", highDPI);
            UIManager.put("TextArea.font", highDPI);
            UIManager.put("Label.font", highDPI);
            UIManager.put("Button.font", highDPI);
            UIManager.put("ToolTip.font", highDPI);
            UIManager.put("FormattedTextField.font", highDPI);
            UIManager.put("PasswordField.font", highDPI);
        }

        CommandLineRunner.SetStaticIP();

        //Get the IP first.
        ConnectionIP = promptForIP();

        singleton = new MainWindow();
        singleton.networkBus = new NetworkConnector(ConnectionIP, 2017);
        singleton.messageReader = new NetworkConnector.MessageReader(singleton.networkBus);
        singleton.startVideoStream();

        Thread controllerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Controller = new MainController(singleton.networkBus);
                while(true){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        controllerThread.start();

        Thread readMessages = new Thread(singleton.messageReader);

        readMessages.start();
    }
}
