import java.awt.*;
import javax.swing.*;

public class HomePagePanel extends JPanel {
    
    public HomePagePanel(JPanel cards, CardLayout layout) {
        setLayout(new BorderLayout());
        JLabel header = new JLabel("Typing Race Simulator", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));

        add(header, BorderLayout.NORTH);
    }
}
