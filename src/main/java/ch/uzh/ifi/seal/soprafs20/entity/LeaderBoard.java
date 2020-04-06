package ch.uzh.ifi.seal.soprafs20.entity;
import javax.persistence.*;

@Entity
@Table(name = "LeaderBoard")
public class LeaderBoard implements Comparable<LeaderBoard>{

    @Id
    @Column(nullable = false, unique = true)
    private Long playerId;

    @Column(nullable = false)
    private int score;

    @Override
    public int compareTo(LeaderBoard other) {
        if (getScore()==other.getScore()) return 0;
        else if (getScore()>other.getScore()) return 1;
        else return -1;
    }



    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
}