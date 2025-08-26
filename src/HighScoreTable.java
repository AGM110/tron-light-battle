import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreTable {
    private List<Score> scores;

    public HighScoreTable() {
        this.scores = new ArrayList<>();
    }

    public void addScore(Score score) {
        scores.add(score);
        Collections.sort(scores, (s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));
        if (scores.size() > 10) {
            scores.remove(scores.size() - 1);
        }
    }

    public List<Score> getTopScores() {
        return scores;
    }
}
