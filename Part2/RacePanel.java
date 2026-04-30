import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RacePanel extends JPanel {
    private static final int TURN_DELAY = 200;

    private final String passage;
    private final List<Typist> typists;
    private final boolean autocorrect;
    private final boolean caffeineMode;
    private final TypingRace race;

    private final JPanel cards;
    private final CardLayout cardLayout;
    private final Leaderboard leaderboard;
    private final Map<String, Integer> burnoutCounts = new HashMap<>();

    private JPanel passagePane;
    private Timer timer;
    private int turns = 0;

    public RacePanel(JPanel cards, CardLayout layout, String passage,
            List<Typist> typists, boolean autocorrectEnabled, boolean caffeineModeEnabled, Leaderboard leaderboard) {
        this.cards = cards;
        this.cardLayout = layout;
        this.passage = passage;
        this.typists = typists;
        this.autocorrect = autocorrectEnabled;
        this.caffeineMode = caffeineModeEnabled;
        this.leaderboard = leaderboard;

        // Create TypingRace object to handle simulation logic
        race = new TypingRace(passage.length(), autocorrectEnabled);
        for (int i = 0; i < typists.size(); i++) {
            typists.get(i).resetToStart();
            race.addTypist(typists.get(i));
        }

        for (Typist t : typists) {
            burnoutCounts.put(t.getName(), 0);
        }

        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel header = new JLabel("Simulating race...", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        passagePane = new JPanel();
        passagePane.setLayout(new BoxLayout(passagePane, BoxLayout.Y_AXIS));
        add(passagePane, BorderLayout.CENTER);

        buildPassageRows();
        startRace();
    }

    private void buildPassageRows() {
        passagePane.removeAll();
        for (Typist t : typists) {
            JPanel row = new JPanel(new BorderLayout(8, 0));

            JLabel name = new JLabel(String.valueOf(t.getSymbol() + " " + t.getName()));
            name.setFont(new Font("Dialog", Font.BOLD, 13));
            name.setPreferredSize(new Dimension(180, 24));

            JTextPane textPane = new JTextPane();
            textPane.setText(passage);
            textPane.setEditable(false);
            textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
            textPane.putClientProperty("typist", t);

            row.add(name, BorderLayout.WEST);
            row.add(textPane, BorderLayout.CENTER);

            passagePane.add(row);
            passagePane.add(Box.createVerticalStrut(8));
        }

        passagePane.revalidate();
        passagePane.repaint();
    }

    private void updatePassageDisplay() {
        for (Component rowComp : passagePane.getComponents()) {
            if (!(rowComp instanceof JPanel)) {
                continue;
            }

            JPanel row = (JPanel) rowComp;

            JTextPane tPane = null;
            Typist typist = null;

            for (Component comp : row.getComponents()) {
                if (comp instanceof JTextPane) {
                    tPane = (JTextPane) comp;
                    typist = (Typist) tPane.getClientProperty("typist");
                }
            }

            if (tPane == null || typist == null) {
                continue;
            }

            StyledDocument textDoc = tPane.getStyledDocument();

            Style defStyle = StyleContext.getDefaultStyleContext()
                    .getStyle(StyleContext.DEFAULT_STYLE);
            textDoc.setCharacterAttributes(0, passage.length(), defStyle, true);

            Style progressStyle = tPane.addStyle("progress", null);
            if (typist.isBurntOut()) {
                StyleConstants.setBackground(progressStyle, new Color(255, 200, 100));
            } else {
                StyleConstants.setBackground(progressStyle, new Color(180, 230, 180));
            }

            int prog = Math.min(typist.getProgress(), passage.length());
            textDoc.setCharacterAttributes(0, prog, progressStyle, false);

        }
    }
    
    private void startRace() {
        timer = new Timer(TURN_DELAY, e -> {
            turns++;

            for (Typist t : typists) {
                if (caffeineMode) {
                    if (turns == 1) {
                        t.setAccuracy(t.getAccuracy() + 0.1);
                    } else if (turns == 11) {
                        t.setAccuracy(t.getAccuracy() - 0.10);
                    }
                }
            }

            int halfway = passage.length() / 2;
            for (Typist t : typists) {
                if (t.hasAccessory(1)) {
                    if (t.hasAccessory(1)) {
                        if (turns == 1) {
                            t.setAccuracy(t.getAccuracy() + 0.10);
                        } else if (turns == halfway) {
                            t.setAccuracy(t.getAccuracy() - 0.20);
                        }
                    }
                }
            }

            for (Typist t : typists) {
                boolean wasBurntOut = t.isBurntOut();
                race.advanceTypist(t);
                
                if (!wasBurntOut && t.isBurntOut()) {
                    burnoutCounts.merge(t.getName(), 1, Integer::sum);
                }
            }

            updatePassageDisplay();

            for (Typist t : typists) {
                if (race.raceFinishedBy(t)) {
                    timer.stop();
                    t.setAccuracy(t.getAccuracy() + 0.02);
                    ShowWinnerScreen(t);
                    return;
                }
            }
        });

        timer.start();

    }
    
    private void ShowWinnerScreen(Typist winner) {
        WinnerPanel winnerPanel = new WinnerPanel(cards, cardLayout, winner, typists, turns, TURN_DELAY, leaderboard, burnoutCounts);
        cards.add(winnerPanel, "WINNER");
        cardLayout.show(cards, "WINNER");
    }
}
