import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Leaderboard {
    
    public static class ResultOfRace {
        public final String typistName;
        public final int racePosition;
        public final double wpm;
        public final double accuracy;
        public final int burnoutCount;
        public final int pointsEarned;

        public ResultOfRace(String name, int pos, double wpm, double accuracy, int burnCount) {
            this.typistName = name;
            this.racePosition = pos;
            this.wpm = wpm;
            this.accuracy = accuracy;
            this.burnoutCount = burnCount;
            this.pointsEarned = calcPoints(pos, wpm, burnCount);
        }

        public int calcPoints(int pos, double wpm, int burnCount) {
            int pointsFromPosition = Math.max(0, 4 - pos);
            int wpmBonus = (int) (wpm / 10);
            int burnPenalty = burnCount;
            return Math.max(0, pointsFromPosition + wpmBonus - burnPenalty);
        }
    }
    
    private final Map<String, List<ResultOfRace>> raceHistory;
    private final Map<String, Integer> cumulativePoints;
    private final Map<String, Double> personalBests;

    public Leaderboard() {
        raceHistory = new HashMap<>();
        cumulativePoints = new HashMap<>();
        personalBests = new HashMap<>();
    }


    public void recordRaceResult(ResultOfRace res) {
        raceHistory.computeIfAbsent(res.typistName, k -> new ArrayList<>()).add(res);

        cumulativePoints.merge(res.typistName, res.pointsEarned, Integer::sum);

        personalBests.merge(res.typistName, res.wpm, Math::max);
    }

    public List<ResultOfRace> getHistory(String typistName) {
        return raceHistory.getOrDefault(typistName, new ArrayList<>());
    }

    public int getCumulativePoints(String typistName) {
        return cumulativePoints.getOrDefault(typistName, 0);
    }

    public double getPersonalBests(String typistName) {
        return personalBests.getOrDefault(typistName, 0.0);
    }

    public List<String> getRankedTypist() {
        List<String> typistNames = new ArrayList<>(cumulativePoints.keySet());
        typistNames.sort((a, b) -> cumulativePoints.get(b) - cumulativePoints.get(a));
        return typistNames;
    }

    public String getTitle(String typistName) {
        List<ResultOfRace> typistResults = getHistory(typistName);
        if (typistResults.isEmpty()) {
            return "Newcomer";
        }

        if (typistResults.size() >= 3) {
            boolean threeConsecutiveWins = true;
            for (int i = typistResults.size() - 3; i < typistResults.size(); i++) {
                if (typistResults.get(i).racePosition != 1) {
                    threeConsecutiveWins = false;
                    break;
                }
            }

            if (threeConsecutiveWins) {
                return "Speed Demon";
            }
        }

        if (typistResults.size() >= 5) {
            boolean noBurns = true;
            for (int i = typistResults.size() - 5; i < typistResults.size(); i++) {
                if (typistResults.get(i).burnoutCount > 0) {
                    noBurns = false;
                    break;
                }
            }

            if (noBurns) {
                return "Iron Fingers";
            }
        }

        int wins = 0;
        for (ResultOfRace r : typistResults) {
            if (r.racePosition == 1) {
                wins++;
            }
        }
        if (wins >= 1) {
            return "Champion";
        }

        return "Competitor";
    }

}
