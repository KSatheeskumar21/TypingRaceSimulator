import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

public class WinnerPanel extends JPanel{
    WinnerPanel(JPanel cards, CardLayout layout, Typist winner, List<Typist> typists, int turnCount, int turnDelay) {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel header = new JLabel("The winner is..." + winner.getName() + "!");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        add(header, BorderLayout.NORTH);

        double totalTimeInSeconds = (turnCount + turnDelay) / 1000.0;

        String[] tableCols = { "Typist", "Final progress", "Accuracy", "Time (s)", "WPM" };
        Object[][] tableData = new Object[typists.size()][5];

        for (int i = 0; i < typists.size(); i++) {
            Typist t = typists.get(i);
            double wpm = (t.getProgress() / 50) / (totalTimeInSeconds / 60.0);
            tableData[i][0] = t.getSymbol() + " " + t.getName();
            tableData[i][1] = t.getProgress();
            tableData[i][2] = String.format("%.2f", t.getAccuracy());
            tableData[i][3] = String.format("%.1f", totalTimeInSeconds);
            tableData[i][4] = String.format("%.1f", wpm);
        }

        JTable statsTable = new JTable(tableData, tableCols);
        statsTable.setEnabled(false);
        statsTable.setFont(new Font("Dialog", Font.BOLD, 13));
        statsTable.setRowHeight(28);
        statsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        add(new JScrollPane(statsTable), BorderLayout.CENTER);

        JButton raceAgain = new JButton("Race Again");
        raceAgain.setFont(new Font("Arial", Font.BOLD, 14));
        raceAgain.addActionListener(e -> layout.show(cards, "HOME"));

        JPanel southSection = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southSection.add(raceAgain);
        add(southSection, BorderLayout.SOUTH);
    }
}
