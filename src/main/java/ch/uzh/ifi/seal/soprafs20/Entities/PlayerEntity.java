package ch.uzh.ifi.seal.soprafs20.Entities;

import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Internal Player Representation
 * This class composes the internal representation of the user and defines how the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 *  nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
@Table(name="Player")
@Embeddable
public class PlayerEntity implements Serializable, Comparable<PlayerEntity> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private PlayerStatus status;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int leaderBoardScore;

    @Column(nullable = false)
    private int gamesPlayed=0;

    @Column
    private Long timePassed;

    @Override
    public int compareTo(PlayerEntity other) {
        if (this.getLeaderBoardScore() == other.getLeaderBoardScore()) return 0;
        else if (this.getLeaderBoardScore() > other.getLeaderBoardScore()) return 1;
        else return -1;
    }

    public Long getTimePassed() {
        return timePassed;
    }

    public void setTimePassed(Long timePassed) {
        this.timePassed = timePassed;
    }

    public int getLeaderBoardScore() {
        return leaderBoardScore;
    }

    public void setLeaderBoardScore(int score) {
        this.leaderBoardScore = score;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public void setPassword (String password){this.password = password;}

    public String getPassword(){return password;}
}
