package Machine.desktop;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.util.*;

import static Machine.Common.Utils.ErrorLog;

public class OpenCVConfig extends JDialog {
    private JPanel contentPane;
    private JButton buttonSave;
    private JButton buttonCancel;

    private JSlider hSlider1;
    private JSlider sSlider1;
    private JSlider vSlider1;
    private JSlider dilateSlider1;
    private JSlider blurSlider1;

    private JSlider hSlider2;
    private JSlider sSlider2;
    private JSlider vSlider2;
    private JSlider dilateSlider2;
    private JSlider blurSlider2;

    private JSlider hSlider3;
    private JSlider sSlider3;
    private JSlider vSlider3;
    private JSlider dilateSlider3;
    private JSlider blurSlider3;
    private JPanel sliderPanels;

    public OpenCVConfig() {

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonSave);

        ToolTipManager.sharedInstance().setInitialDelay(0);

        setTooltipListeners();

        buttonSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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

    private void setToolTipInitialValues() {
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

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        if (Toolkit.getDefaultToolkit().getScreenSize().getWidth() > 1920) {
            Font highDPI = new Font(null, Font.PLAIN, 24);
            UIManager.put("MenuBar.font", highDPI);
            UIManager.put("Menu.font", highDPI);
            UIManager.put("MenuItem.font", highDPI);
            UIManager.put("Slider.thumbHeight", 34);
            UIManager.put("Slider.thumbWidth", 34);
            UIManager.put("OptionPane.messageFont", highDPI);
            UIManager.put("OptionPane.buttonFont", highDPI);
            UIManager.put("TextField.font", highDPI);
            UIManager.put("TextArea.font", highDPI);
            UIManager.put("Label.font", highDPI);
            UIManager.put("Button.font", highDPI);
            UIManager.put("ToolTip.font", highDPI);
        }
        OpenCVConfig dialog = new OpenCVConfig();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
