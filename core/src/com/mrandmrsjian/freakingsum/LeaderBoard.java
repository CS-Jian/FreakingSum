package com.mrandmrsjian.freakingsum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jian on 15/5/5.
 */
public class LeaderBoard {
    public Array<Score> scores;
    private int size;

    public LeaderBoard(int size) {
        this.size = size;
        scores = new Array(true, size, Score.class);
    }

    public class Score {
        public String dateTime;
        public int score;

        public Score() {
            this(0);
        }

        public Score(int score) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            this.dateTime = sdf.format(new Date());

            this.score = score;
        }

        public Score(String item) {
            Gdx.app.debug("LeaderBoard", "init score data=" + item);
            String[] data = item.split(",");
            dateTime = data[0];
            score = Integer.parseInt(data[1]);
        }

        public String toString() {
            return dateTime + "," + score;
        }

        public int compareTo(Score otherScore) {
            return (this.score > otherScore.score) ? 1 : (this.score < otherScore.score ? -1 : 0);
        }

    }

    public String toString() {
        StringBuilder sb = new StringBuilder(scores.get(0).toString());
        for (int i=1; i<size; i++) {
            sb.append("|").append(scores.get(i).toString());
        }
        return sb.toString();
    }

    public void loadFromString(String data) {
        Gdx.app.debug("LeaderBoard", "load data=" + data );
        if (!data.isEmpty()) {
            String[] items = data.split("\\|");
            for (int i = 0; i < items.length; i++) {
                scores.add(new Score(items[i]));
            }
        } else {
            for (int i=0; i<size; i++) {
                scores.add(new Score(0));
            }
        }
    }

    public int recordScore(int point) {
        Score score = new Score(point);
        int position = -1;
        if (score.compareTo(scores.get(size-1))>0) {
            scores.set(size-1, score);
            position = size-1;

            for (int i = size-2; i >= 0; i--) {
                if (score.compareTo(scores.get(i)) > 0) {
                    scores.swap(i + 1, i);
                    position = i;
                } else {
                    break;
                }
            }
        }

        return position;
    }
}
