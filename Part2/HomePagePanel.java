import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import java.util.List;
import java.util.ArrayList;

public class HomePagePanel extends JPanel {

    // Constants for various parts of the Panel
    private final String HEADER = "Typing Race Simulator";
    private final String SHORT_PASSAGE = "The quick brown fox jumps over the lazy dog.";
    private final String MEDIUM_PASSAGE = "How vexingly quick daft zebras jump! The five boxing wizards jump quickly.";
    private final String LONG_PASSAGE = "Pack my box with five dozen liquor jugs. Sphinx of black quartz, judge my vow. The jay, pig, fox, zebra and my wolves quack!";
    private final int MAX_NUMBER_OF_TYPISTS = 6;
    private final int DEFAULT_NUMBER_OF_TYPISTS = 3;

    // Attribute fields to be passed to other parts of the GUI
    private String passage;
    private List<Typist> typists;
    private List<JPanel> typistRows;
    private boolean autocorrectMode = false;
    private boolean caffeineMode = false;
    private boolean nightShift = false;
    
    public HomePagePanel(JPanel cards, CardLayout layout) {
        
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));


        // North Section: Header
        JLabel header = new JLabel(HEADER, SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        add(header, BorderLayout.NORTH);

        // Center: Configure Race
        JPanel centerSettings = new JPanel(new GridLayout(1, 2, 16, 0));

        // Adding panels for race settings and number of typists
        centerSettings.add(buildSettingsPanel());
        centerSettings.add(buildTypistsPanel());

        // South: Difficulty modifiers + start button
        JPanel southSection = new JPanel();
        // Create box layout that stacks children vertically
        southSection.setLayout(new BoxLayout(southSection, BoxLayout.Y_AXIS)); 
        southSection.add(buildModifiersPanel());
        southSection.add(Box.createVerticalStrut(12));
        southSection.add(buildStartButton(cards, layout));

        // Container for Center and South sections
        JPanel container = new JPanel(new BorderLayout(0, 16));
        container.add(centerSettings, BorderLayout.CENTER);
        container.add(southSection, BorderLayout.SOUTH);

        add(container, BorderLayout.CENTER);
    }

    /**
     * Creates JPanel that contains information about settings for the
     * length of the race, or gives the user the option to enter their
     * own custom passage
     * 
     * @return JPanel object with a menu to configure race settings
     */
    private JPanel buildSettingsPanel() {
        JPanel settingsPanel = new JPanel();

        // Create radio buttons to select passage length
        JRadioButton shortBtn = new JRadioButton("Short (~40 chars)");
        JRadioButton medBtn = new JRadioButton("Medium (~80 chars)");
        JRadioButton longBtn = new JRadioButton("Long (~120 chars)");
        shortBtn.setSelected(true);
        passage = SHORT_PASSAGE;

        // Link buttons together to prevent more than one being selected
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(shortBtn);
        btnGroup.add(medBtn);
        btnGroup.add(longBtn);

        // Panel to display all three buttons
        JPanel radioPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        radioPanel.add(shortBtn);
        radioPanel.add(medBtn);
        radioPanel.add(longBtn);

        // Custom text area
        JLabel customPassageLabel = new JLabel("Or enter a custom passage:");
        customPassageLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JTextArea cPassageArea = new JTextArea(3, 20);
        cPassageArea.setLineWrap(true);
        cPassageArea.setWrapStyleWord(true);
        cPassageArea.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(cPassageArea);

        // Add button to enable creating a custom passage
        JCheckBox customCheckBox = new JCheckBox("Use custom passage");

        // Configuring buttons
        shortBtn.addActionListener(e -> {
            customCheckBox.setSelected(false);
            cPassageArea.setEnabled(false);
            cPassageArea.setText("");
            passage = SHORT_PASSAGE;
        });

        medBtn.addActionListener(e -> {
            customCheckBox.setSelected(false);
            cPassageArea.setEnabled(false);
            cPassageArea.setText("");
            passage = MEDIUM_PASSAGE;
        });

        longBtn.addActionListener(e -> {
            customCheckBox.setSelected(false);
            cPassageArea.setEnabled(false);
            cPassageArea.setText("");
            passage = LONG_PASSAGE;
        });

        customCheckBox.addActionListener(e -> {
            boolean useCustom = customCheckBox.isSelected();
            cPassageArea.setEnabled(useCustom);
            btnGroup.clearSelection();

            if (useCustom) {
                passage = cPassageArea.getText();
            } else {
                cPassageArea.setText("");
                shortBtn.setSelected(true);
                passage = SHORT_PASSAGE;
            }
        });

        cPassageArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateTextArea();
            }

            public void removeUpdate(DocumentEvent e) {
                updateTextArea();
            }

            public void changedUpdate(DocumentEvent e) {
                updateTextArea();
            }

            private void updateTextArea() {
                if (customCheckBox.isSelected()) {
                    passage = cPassageArea.getText();
                }
            }
        });

        // Create panel for custom passage area
        JPanel customPanel = new JPanel(new BorderLayout(0, 4));
        customPanel.add(customCheckBox, BorderLayout.NORTH);
        customPanel.add(customPassageLabel, BorderLayout.CENTER);
        customPanel.add(scrollPane, BorderLayout.SOUTH);

        // Assemble panels into final panel
        settingsPanel.add(radioPanel);
        settingsPanel.add(customPanel);

        return settingsPanel;
    }
    
    /**
     * Creates JPanel that allows user to configure number of typists
     * 
     * @return
     */
    private JPanel buildTypistsPanel() {

        JPanel typistsPanel = new JPanel(new BorderLayout(0, 10));
        typistsPanel.setBorder(BorderFactory.createTitledBorder("Typists"));

        // Setting number of typists
        JLabel seatLabel = new JLabel("Number of typists:");
        JSpinner seatSpinner = new JSpinner(new SpinnerNumberModel(3, 2, 6, 1));

        JPanel seatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        seatPanel.add(seatLabel);
        seatPanel.add(seatSpinner);

        // Displaying list of typists
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        // Default values for each typist
        String[] defNames = { "TURBOFINGERS", "QWERTY_QUEEN", "HUNT_N_PECK", "KING_CLAVIER", "LIL_CLICKY",
                "MACROMORE" };
        char[] defSymbols = { '①', '②', '③', '④', '⑤', '⑥' };
        double[] defAccuracies = { 0.85, 0.60, 0.30, 0.70, 0.50, 0.40 };

        typists = new ArrayList<>();
        typistRows = new ArrayList<>();

        // Create ArrayList of Typist objects for all possible typists
        for (int i = 0; i < MAX_NUMBER_OF_TYPISTS; i++) {
            Typist t = new Typist(defSymbols[i], defNames[i], defAccuracies[i]);
            typists.add(t);

            JPanel row = buildTypistRow(t);
            typistRows.add(row);
            listPanel.add(row);
            listPanel.add(Box.createVerticalStrut(4));
        }

        // Default number of typists is 3, display available typists accordingly
        updateVisibleRows(DEFAULT_NUMBER_OF_TYPISTS);

        // Update visible rows when number of typists is changed
        seatSpinner.addChangeListener(e -> {
            int count = (int) seatSpinner.getValue();
            updateVisibleRows(count);
        });

        typistsPanel.add(seatPanel, BorderLayout.NORTH);
        typistsPanel.add(listPanel, BorderLayout.CENTER);

        return typistsPanel;
    }

    /**
     * Creates a JPanel object containing information about a given typist
     * 
     * @param t Typist object used to construct row
     * @return JPanel object containing necessary information
     */
    private JPanel buildTypistRow(Typist t) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel symLabel = new JLabel(String.valueOf(t.getSymbol()));
        symLabel.setFont(new Font("Dialog", Font.PLAIN, 16));

        // Text field for user to change typist name if they do not like default names
        JTextField nameField = new JTextField(t.getName(), 12);
        nameField.addActionListener(e -> t.setName(nameField.getText()));
        nameField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                t.setName(nameField.getText());
            }
        });

        // Display accuracy information
        JLabel accLabel = new JLabel("Acc:");
        JSpinner accSpinner = new JSpinner(new SpinnerNumberModel(t.getAccuracy(), 0.0, 1.0, 0.05));
        accSpinner.setPreferredSize(new Dimension(60, 24));
        accSpinner.addChangeListener(e -> t.setAccuracy((double) accSpinner.getValue()));

        row.add(symLabel);
        row.add(nameField);
        row.add(accLabel);
        row.add(accSpinner);

        return row;
    }
    
    /**
     * Displays a list of JPanels that display information 
     * about each typist being used in the simulation
     * 
     * @param count number of typists that should be shown
     */
    private void updateVisibleRows(int count) {
        for (int i = 0; i < typistRows.size(); i++) {
            typistRows.get(i).setVisible(i < count);
        }
    }

    /**
     * Gets a sublist of the typists ArrayList
     * 
     * @param count Number of typists being selected
     * @return sub list of typists containing selected 
     * typists
     */
    public List<Typist> getTypists(int count) {
        return new ArrayList<>(typists.subList(0, count));
    }

    /**
     * Creates JPanel object containing data about modifiers
     * 
     * @return JPaenl object with set of modifier checkboxes
     */
    private JPanel buildModifiersPanel() {
        JPanel modifierPanel = new JPanel(new BorderLayout(0, 10));
        modifierPanel.setBorder(BorderFactory.createTitledBorder("Difficulty modifiers"));

        // Container to store all modifier options
        JPanel boxContainer = new JPanel(new GridLayout(1, 3, 16, 0));

        JCheckBox autocorrectBox = new JCheckBox("Autocorrect (slide back halved)");
        autocorrectBox.addActionListener(e -> autocorrectMode = autocorrectBox.isSelected());

        JCheckBox caffBox = new JCheckBox("Caffeine Mode (speed boost for the first 10 turns)");
        caffBox.addActionListener(e -> caffeineMode = caffBox.isSelected());

        JCheckBox nightShiftBox = new JCheckBox("Night Shift (Accuracy reduced slightly)");
        nightShiftBox.addActionListener(e -> nightShift = nightShiftBox.isSelected());

        boxContainer.add(autocorrectBox);
        boxContainer.add(caffBox);
        boxContainer.add(nightShiftBox);

        modifierPanel.add(boxContainer, BorderLayout.CENTER);

        return modifierPanel;
    }
    
    private JButton buildStartButton(JPanel cards, CardLayout layout) {
        JButton startButton = new JButton("Start Race");

        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setPreferredSize(new Dimension(200, 40));

        startButton.addActionListener(e -> {
            String pass = getSelectedPassage();

            if (pass == null || pass.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a passage before starting",
                    "No Passage Selected",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }


            if (nightShift) {
                for (Typist t : typists) {
                    t.setAccuracy(t.getAccuracy() - 0.05);
                }
            }

            cards.add(new RacePanel(cards, layout, passage, typists, autocorrectMode, caffeineMode), "RACE");
            layout.show(cards, "RACE");

        });

        return startButton;
    }
    
    // Getters and Setters

    public String getSelectedPassage() {
        return passage;
    }
    
    public boolean isAutocorrectEnabled() {
        return autocorrectMode;
    }

    public boolean isCaffeineModeEnabled() {
        return caffeineMode;
    }

    public boolean isNightShiftEnabled() {
        return nightShift;
    }
}
