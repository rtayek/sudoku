package p;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;

class MainControls extends JPanel {
    MainControls(Main main, Struct struct, Properties properties, File propertiesFile) {
        this.main = main;
        this.struct = struct;
        this.properties = properties;
        this.propertiesFile = propertiesFile;
        this.buttons = new JButton[Main.Buttons.values().length];
        this.colorChooserButtons = new JButton[struct.colors.length];
        this.colorPreviews = new JButton[struct.colors.length];
        colorsDialog = new JDialog(main, Main.Buttons.Colors.toString(), true);
        setLayout(new BorderLayout(4, 4));
        setPreferredSize(new Dimension(160, 0));
        buttonPanel = new JPanel(new GridLayout(Main.Buttons.values().length, 1, 0, 4));
        for (Main.Buttons b : Main.Buttons.values()) {
            JButton button = new JButton(b.toString());
            button.setName(b.toString());
            button.addActionListener(main);
            buttons[b.ordinal()] = button;
            buttonPanel.add(button);
        }
        add(buttonPanel, BorderLayout.NORTH);
        colorPreviewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        for (int i = 0; i < colorPreviews.length; i++) {
            JButton preview = new JButton("" + (i + 1));
            preview.setOpaque(true);
            preview.setBorderPainted(false);
            preview.setEnabled(false);
            colorPreviews[i] = preview;
            colorPreviewPanel.add(preview);
        }
        add(colorPreviewPanel, BorderLayout.SOUTH);
        setupColorsDialog();
        updateColorButtons();
    }

    void updateIndexDisplay(int index) {
        if (struct.sudokus != null && !struct.sudokus.isEmpty()) {
            String text = "" + index;
            buttons[Main.Buttons.Number.ordinal()].setText(text);
        }
    }

    void showColorDialog() {
        colorsDialog.pack();
        colorsDialog.setLocationRelativeTo(main);
        colorsDialog.setVisible(true);
    }

    void repaintCanvas() {
        main.canvas.repaint();
    }

    private void setupColorsDialog() {
        colorsDialogPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        ActionListener actionListener = e -> {
            Object source = e.getSource();
            if (source instanceof JButton) {
                JButton button = (JButton) source;
                int i = Integer.parseInt(button.getName()) - 1;
                Color newColor = JColorChooser.showDialog(null, "Choose Color " + (i + 1), struct.colors[i]);
                if (newColor != null) {
                    struct.colors[i] = newColor;
                    updateColorButtons();
                    repaintCanvas();
                    colorsDialog.setVisible(false);
                    Main.writeProperties(properties, propertiesFile);
                }
            }
        };
        for (int i = 0; i < colorChooserButtons.length; i++) {
            String name = "" + (i + 1);
            JButton chooser = new JButton(name);
            chooser.setName(name);
            chooser.addActionListener(actionListener);
            colorChooserButtons[i] = chooser;
            colorsDialogPanel.add(chooser);
        }
        colorsDialog.getContentPane().add(colorsDialogPanel);
    }

    private void updateColorButtons() {
        for (int i = 0; i < colorPreviews.length; i++) {
            Color color = struct.colors[i];
            colorPreviews[i].setBackground(color);
            colorChooserButtons[i].setBackground(color);
        }
    }

    private final Main main;
    private final Struct struct;
    private final Properties properties;
    private final File propertiesFile;
    private final JButton[] buttons;
    private final JButton[] colorChooserButtons;
    private final JButton[] colorPreviews;
    private final JPanel buttonPanel;
    private final JPanel colorPreviewPanel;
    private JPanel colorsDialogPanel;
    private final JDialog colorsDialog;
}
