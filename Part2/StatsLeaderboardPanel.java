import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class StatsLeaderboardPanel extends JPanel {
    public StatsLeaderboardPanel(JPanel cards, CardLayout layout, List<Typist> typists, Leaderboard leaderboard) {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel statsHeader = new JLabel("Stats and Leaderboard", SwingConstants.CENTER);
        statsHeader.setFont(new Font("Arial", Font.BOLD, 22));

        add(statsHeader, BorderLayout.NORTH);

        JTabbedPane typistTabs = new JTabbedPane();
        typistTabs.addTab("Leaderboard", buildLeaderboard(leaderboard));

        for (Typist t : typists) {
            typistTabs.addTab(t.getName(), buildTypistHistory(t, leaderboard));
        }

        add(typistTabs, BorderLayout.CENTER);

        JButton returnBtn = new JButton("Return to results");
        returnBtn.setFont(new Font("Arial", Font.BOLD, 14));
        returnBtn.addActionListener(e -> layout.show(cards, "WINNER"));

        JPanel southContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southContainer.add(returnBtn);
        add(southContainer, BorderLayout.SOUTH);

    }

    private JPanel buildLeaderboard(Leaderboard leaderboard) {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] tableCols = { "Rank", "Typist", "Total Points", "Best WPM", "Title" };
        List<String> ranks = leaderboard.getRankedTypist();
        Object[][] tableData = new Object[ranks.size()][5];

        for (int i = 0; i < ranks.size(); i++) {
            String tName = ranks.get(i);
            tableData[i][0] = i + 1;
            tableData[i][1] = tName;
            tableData[i][2] = leaderboard.getCumulativePoints(tName);
            tableData[i][3] = String.format("%.1f", leaderboard.getPersonalBests(tName));
            tableData[i][4] = leaderboard.getTitle(tName);
        }

        JTable statsTable = new JTable(tableData, tableCols);
        statsTable.setEnabled(false);
        statsTable.setFont(new Font("Dialog", Font.PLAIN, 13));
        statsTable.setRowHeight(28);
        statsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        tablePanel.add(new JScrollPane(statsTable), BorderLayout.CENTER);
        return tablePanel;
    }

    private JPanel buildTypistHistory(Typist t, Leaderboard leaderboard) {
        JPanel typistHistory = new JPanel(new BorderLayout(0, 8));
        typistHistory.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel personalBest = new JLabel(
                "Best WPM: " + String.format("%.1f", leaderboard.getPersonalBests(t.getName()))
                        + "   |   Title: " + leaderboard.getTitle(t.getName()), SwingConstants.CENTER);
        personalBest.setFont(new Font("Arial", Font.PLAIN, 13));
        typistHistory.add(personalBest, BorderLayout.NORTH);

        String[] historyCols = { "Race", "Position", "WPM", "Accuracy", "Burnouts", "Points" };
        List<Leaderboard.ResultOfRace> history = leaderboard.getHistory(t.getName());
        Object[][] historyData = new Object[history.size()][6];

        for (int i = 0; i < history.size(); i++) {
            Leaderboard.ResultOfRace r = history.get(i);
            historyData[i][0] = i + 1;
            historyData[i][1] = r.racePosition;
            historyData[i][2] = String.format("%.1f", r.wpm);
            historyData[i][3] = String.format("%.2f", r.accuracy);
            historyData[i][4] = r.burnoutCount;
            historyData[i][5] = r.pointsEarned;
        }

        JTable historyTable = new JTable(historyData, historyCols);
        historyTable.setEnabled(false);
        historyTable.setFont(new Font("Dialog", Font.PLAIN, 13));
        historyTable.setRowHeight(28);
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        typistHistory.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        return typistHistory;
    }
}