

package ch.uzh.ifi.seal.soprafs20.Entities;

import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;

import javax.persistence.*;
import java.io.Serializable;


@Entity

@Table(name="Player")
@Embeddable
public class PlayerEntity implements Serializable, Comparable<PlayerEntity> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
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
    private int score;

    @Override
    public int compareTo(PlayerEntity other) {
        if (this.getScore() == other.getScore()) return 0;
        else if (this.getScore() > other.getScore()) return 1;
        else return -1;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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
