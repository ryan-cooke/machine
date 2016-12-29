package Machine.desktop;

import javax.swing.*;
import java.awt.event.*;

public class MainWindow extends JDialog {

    private JPanel contentPane;
    private JButton buttonReboot;
    private JButton buttonExit;
    private JFormattedTextField Prompt;
    private JTextArea MessageFeed;
    private JPanelOpenCV VideoPanel;

    public MainWindow() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonReboot);

        registerCallbacks();
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

    private void onPressReboot() {
        //TODO: Send a network command to quit.
    }

    private void onPressExit() {
        // add your code here if necessary
        int dialogResult = JOptionPane.showConfirmDialog (null,
                "Are you sure you want to quit?","Warning",
                JOptionPane.YES_NO_OPTION);
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
