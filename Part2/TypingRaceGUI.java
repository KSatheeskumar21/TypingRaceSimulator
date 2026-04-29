import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

public class TypingRaceGUI extends JFrame {

    private static final String WINDOW_TITLE = "Typing Race Simulator";
    private static final Dimension DIMENSIONS = new Dimension(1000, 800);

    private CardLayout cardLayout;
    private JPanel cards;

    public TypingRaceGUI() {
        setTitle(WINDOW_TITLE);
        setSize(DIMENSIONS);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        cards.add(new HomePagePanel(cards, cardLayout), "HOME");
        // ...

        add(cards);
    }
    
    public void launch() {
        cardLayout.show(cards, "HOME");
        setVisible(true);
    }
}
