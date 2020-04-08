package ch.uzh.ifi.seal.soprafs20.Entities;

import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Internal PlayerEntity Representation
 * This class composes the internal representation of the user and defines how the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 *  nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
//Even though there is a red line beneath "PlayerEntity", it should still work
@Table(name = "PLAYER")
<<<<<<< Updated upstream:src/main/java/ch/uzh/ifi/seal/soprafs20/GameLogic/Player.java
public class Player implements Serializable {
=======
public class PlayerEntity implements Serializable, Comparable<PlayerEntity> {
>>>>>>> Stashed changes:src/main/java/ch/uzh/ifi/seal/soprafs20/Entities/PlayerEntity.java

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

<<<<<<< Updated upstream:src/main/java/ch/uzh/ifi/seal/soprafs20/GameLogic/Player.java
=======
    @Column(nullable = false)
    private int score;

    @Override
    public int compareTo(PlayerEntity other) {
        if (this.getScore()==other.getScore()) return 0;
        else if (this.getScore()>other.getScore()) return 1;
        else return -1;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

>>>>>>> Stashed changes:src/main/java/ch/uzh/ifi/seal/soprafs20/Entities/PlayerEntity.java
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
