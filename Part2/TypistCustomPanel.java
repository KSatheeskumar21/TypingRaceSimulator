import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

public class TypistCustomPanel extends JPanel {
    // Names for each typing style
    private static final String[] TYPE_STYLES = { "Touch Typist", "Hunt & Peck", "Phone Thumbs", "Voice-To-Text" };

    // Arrays containing modifiers for typist accuracy and burnout time for each style
    private static final double[] STYLE_ACC_MODS = { 0.10, -0.10, -0.05, -0.15 };
    private static final int[] STYLE_BURN_MODS = { 0, -1, 0, 1 };
    
    // Names for each type of keyboard and their respect accuracy modifiers
    private static final String[] KEYBOARD_TYPES = { "Mechanical", "Membrane", "Touchscreen", "Stenography" };
    private static final double[] KEYBOARD_ACC_MODS = { 0.05, 0.0, -0.10, 0.15 };

    // Names for the available accessories and descriptions for what they do
    private static final String[] ACCESSORIES = { "Wrist Support", "Energy Drink", "Noise-cancelling Headphones" };
    private static final String[] ACC_DESCRIPTIONS = {
        "Reduces burnout duration by 1",
        "Accuracy increases by 0.1 for the first half, then decreases by 0.1 in the second",
        "Mistype chance reduced by 0.05"
    };

    private final Typist typist;
    private final JPanel cards;
    private final CardLayout layout;

    private int selectedStyle = 0;
    private int selectedKeyboard = 0;
    private boolean[] selectedAccessories = new boolean[ACCESSORIES.length];

    public TypistCustomPanel(JPanel cards, CardLayout cardLayout, Typist typist) {
        this.cards = cards;
        this.layout = cardLayout;
        this.typist = typist;

        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // North section: Title for panel
        JLabel titleLabel = new JLabel("Customise " + typist.getName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        add(titleLabel, BorderLayout.NORTH);

        // Center: Panel to set customisation options
        JPanel centerSection = new JPanel(new GridLayout(1, 3, 16, 0));
        centerSection.add(buildTypingStylePanel());
        centerSection.add(buildKeyboardPanel());
        centerSection.add(buildAccessoriesPanel());
        add(centerSection, BorderLayout.CENTER);

        // South: Preview for modified accuracy and button to return to home panel
        JPanel southSection = new JPanel(new BorderLayout(0, 8));
        southSection.add(buildAccuracyPreview(), BorderLayout.CENTER);
        southSection.add(buildBackButton(), BorderLayout.SOUTH);
        add(southSection, BorderLayout.SOUTH);

    }

    private JPanel buildTypingStylePanel() {
        JPanel stylePanel = new JPanel(new BorderLayout(0, 8));
        stylePanel.setBorder(BorderFactory.createTitledBorder("Typing Style:"));

        // Radio button to select typing style
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        ButtonGroup btnGroup = new ButtonGroup();

        // Create set of radio buttons for each typing style
        for (int i = 0; i < TYPE_STYLES.length; i++) {
            final int index = i;
            JRadioButton styleBtn = new JRadioButton(
                    TYPE_STYLES[i] + "Accuracy: "
                            + (STYLE_ACC_MODS[i] >= 0 ? "+" : "") + STYLE_ACC_MODS[i]
                            + " Burnout: " + (STYLE_BURN_MODS[i] >= 0 ? "+" : "")
                            + STYLE_BURN_MODS[i] + " turns");

            if (i == 0) {
                styleBtn.setSelected(true);
            }
            styleBtn.addActionListener(e -> selectedStyle = index);
            btnGroup.add(styleBtn);
            radioPanel.add(styleBtn);
            radioPanel.add(Box.createVerticalStrut(6));
        }

        stylePanel.add(radioPanel, BorderLayout.NORTH);
        return stylePanel;

    }
    
    private JPanel buildKeyboardPanel() {
        JPanel keyboardPanel = new JPanel(new BorderLayout(0, 8));
        keyboardPanel.setBorder(BorderFactory.createTitledBorder("Keyboard Type:"));

        // Radio button to select typing style
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        ButtonGroup btnGroup = new ButtonGroup();

        // Create set of radio buttons for each typing style
        for (int i = 0; i < KEYBOARD_TYPES.length; i++) {
            final int index = i;
            JRadioButton keyBtn = new JRadioButton(KEYBOARD_TYPES[i] + " - Accuracy: " +
            (KEYBOARD_ACC_MODS[i] >= 0 ? "+" : "") + KEYBOARD_ACC_MODS[i]);

            if (i == 0) {
                keyBtn.setSelected(true);
            }
            keyBtn.addActionListener(e -> selectedKeyboard = index);
            btnGroup.add(keyBtn);
            radioPanel.add(keyBtn);
            radioPanel.add(Box.createVerticalStrut(6));
        }

        keyboardPanel.add(radioPanel, BorderLayout.NORTH);
        return keyboardPanel;
    }
    
    private JPanel buildAccessoriesPanel() {
        JPanel accPanel = new JPanel(new BorderLayout(0, 8));
        accPanel.setBorder(BorderFactory.createTitledBorder("Accessories"));

        JPanel checkboxes = new JPanel();
        checkboxes.setLayout(new BoxLayout(checkboxes, BoxLayout.Y_AXIS));

        for (int i = 0; i < ACCESSORIES.length; i++) {
            final int index = i;
            JCheckBox cbox = new JCheckBox(ACCESSORIES[i] + ": " + ACC_DESCRIPTIONS[i]);
            cbox.addActionListener(e -> selectedAccessories[index] = cbox.isSelected());
            checkboxes.add(cbox);
            checkboxes.add(Box.createVerticalStrut(6));
        }
        
        accPanel.add(checkboxes, BorderLayout.NORTH);

        return accPanel;
    }
    
    private JLabel buildAccuracyPreview() {
        JLabel accLabel = new JLabel("", SwingConstants.CENTER);
        accLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        
        accLabel.setText(genAccuracyPreview());

        return accLabel;
    }

    private String genAccuracyPreview() {
        double mod = STYLE_ACC_MODS[selectedStyle] + KEYBOARD_ACC_MODS[selectedKeyboard];
        double preview = Math.min(1.0, Math.max(0.0, typist.getAccuracy() + mod));

        return String.format("%.2f → %.2f after customisation", typist.getAccuracy(), preview);
    }

    private JButton buildBackButton() {
        JButton returnButton = new JButton("Save and return");
        returnButton.setFont(new Font("Arial", Font.BOLD, 14));

        returnButton.addActionListener(e -> ApplySettings());

        return returnButton;
    }

    private void ApplySettings() {
        double accMod = STYLE_ACC_MODS[selectedStyle] + KEYBOARD_ACC_MODS[selectedKeyboard];
        typist.setAccuracy(typist.getAccuracy() + accMod);

        typist.setTypingStyle(selectedStyle);
        typist.setKeyboardType(selectedKeyboard);
        typist.setAccessories(selectedAccessories.clone());

        layout.show(cards, "HOME");
    }
}
