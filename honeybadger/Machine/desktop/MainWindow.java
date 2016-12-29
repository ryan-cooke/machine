package Machine.desktop;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MainWindow extends JDialog {

    private JPanel contentPane;
    private JButton buttonReboot;
    private JButton buttonExit;
    private JFormattedTextField Prompt;

    private JTextArea messageFeed;
    private JPanelOpenCV videoPanel;

    private final String promptChar = "> ";
    private ArrayList<String> inputHistory;
    private int inputOffset;

    public MainWindow() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonReboot);

        inputHistory = new ArrayList<>(40);
        inputOffset = 0;
        Prompt.setText(promptChar);

        registerCallbacks();
        //Below are listeners being tested
        Prompt.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String input = Prompt.getText().substring(2);
                if(input.length()>0) {
                    Prompt.setText(promptChar);
                    messageFeed.append(input);
                    messageFeed.append("\n");

                    inputHistory.add(input);
                }
                inputOffset=0;
            }
        },KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),JComponent.WHEN_FOCUSED);

        Prompt.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Document rawIn = Prompt.getDocument();
                Cursor cursor = Prompt.getCursor();
                int pos = Prompt.getCaretPosition();
                if(rawIn.getLength()>promptChar.length() && pos>promptChar.length()) {
                    try {
                        rawIn.remove(Prompt.getCaretPosition()-1, 1);
                    }
                    catch (Exception e){e.printStackTrace();}
                }
            }
        },KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE,0),JComponent.WHEN_FOCUSED);

        Prompt.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Try getting previous commands
                inputOffset +=1;
                previousInputLookup();
            }
        },KeyStroke.getKeyStroke(KeyEvent.VK_UP,0),JComponent.WHEN_FOCUSED);

        Prompt.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                inputOffset -=1;
                previousInputLookup();
            }
        },KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0),JComponent.WHEN_FOCUSED);
    }

    private void registerCallbacks(){
        buttonReboot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onPressReboot();
            }
        });

        buttonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onPressExit();
            }
        });

        // call onPressExit() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onPressExit();
            }
        });

        // call onPressExit() on ESCAPE
        //TODO: Remove?
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onPressExit();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void previousInputLookup(){
        int historyIndex = inputHistory.size()-inputOffset;
        if(historyIndex>=0 && historyIndex<inputHistory.size()){
            Prompt.setText(String.format("%s%s",promptChar,inputHistory.get(historyIndex)));
        }
    }

    private void onPressReboot() {
        //TODO: Send a network command to quit.
    }

    private void onPressExit() {
        // add your code here if necessary
        int dialogResult = JOptionPane.showConfirmDialog (null,
                "Are you sure you want to quit?","Exit Warning",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if(dialogResult == JOptionPane.YES_OPTION){
            //TODO: close all network connections
            dispose();
        }
    }

    public static void main(String[] args) {
        //Try changing the theme
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        }catch (Exception e){}

        MainWindow dialog = new MainWindow();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
