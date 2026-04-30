import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WinnerPanel extends JPanel{
    public WinnerPanel(JPanel cards, CardLayout layout, Typist winner, List<Typist> typists,
                 int turnCount, int turnDelay, Leaderboard leaderboard, Map<String, Integer> burnouts) {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel header = new JLabel("The winner is..." + winner.getName() + "!");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        add(header, BorderLayout.NORTH);

        double totalTimeInSeconds = (turnCount * turnDelay) / 1000.0;

        String[] tableCols = { "Typist", "Final progress", "Accuracy", "Time (s)", "WPM", "Burnouts", "Points" };
        Object[][] tableData = new Object[typists.size()][7];

        List<Typist> ranks = new ArrayList<>(typists);
        ranks.sort((a, b) -> b.getProgress() - a.getProgress());

        for (int i=0; i < ranks.size(); i++) {
            Typist t = ranks.get(i);
            int pos = i + 1;
            double wpm = (t.getProgress() / 5.0) / (totalTimeInSeconds / 60.0);
            int numberOfBurnouts = burnouts.getOrDefault(t.getName(), 0);

            Leaderboard.ResultOfRace res = new Leaderboard.ResultOfRace(
                t.getName(), pos, wpm, t.getAccuracy(), numberOfBurnouts
            );
            leaderboard.recordRaceResult(res);

            tableData[i][0] = t.getSymbol() + " " + t.getName();
            tableData[i][1] = pos;
            tableData[i][2] = t.getProgress();
            tableData[i][3] = String.format("%.2f", t.getAccuracy());
            tableData[i][4] = String.format("%.1f", wpm);
            tableData[i][5] = numberOfBurnouts;
            tableData[i][6] = res.pointsEarned;
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

        JButton viewStatsButton = new JButton("View Stats & Leaderboard");
        viewStatsButton.setFont(new Font("Arial", Font.BOLD, 14));
        viewStatsButton.addActionListener(e -> {
            StatsLeaderboardPanel statsPanel = new StatsLeaderboardPanel(cards, layout, typists, leaderboard);
            cards.add(statsPanel, "STATS");
            layout.show(cards, "STATS");
        });

        JPanel southSection = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southSection.add(raceAgain);
        southSection.add(viewStatsButton);
        add(southSection, BorderLayout.SOUTH);
    }
}
