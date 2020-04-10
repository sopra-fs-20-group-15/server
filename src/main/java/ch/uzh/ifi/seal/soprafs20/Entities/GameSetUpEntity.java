package ch.uzh.ifi.seal.soprafs20.Entities;

import ch.uzh.ifi.seal.soprafs20.constant.GameType;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Queue;


/**
 * Internal PlayerEntity Representation
 * This class composes the internal representation of the user and defines how the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 *  nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
//Even though there is a red line beneath "PlayerEntity", it should still work
@Table(name = "GAMESETUP")
public class GameSetUpEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long numberOfPlayers;

    @Column(nullable = false)
    private Long numberOfBots;

    @Column(nullable = false)
    private GameType gameType;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private Long HostId;

    /**Getters and Setters*/
    public Long getHostId() {
        return HostId;
    }

    public void setHostId(Long hostId) {
        HostId = hostId;
    }

    public Long getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(Long numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public Long getNumberOfBots() {
        return numberOfBots;
    }

    public void setNumberOfBots(Long numberOfBots) {
        this.numberOfBots = numberOfBots;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}