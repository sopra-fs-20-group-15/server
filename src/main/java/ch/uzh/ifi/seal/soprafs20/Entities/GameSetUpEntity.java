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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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
    private Long hostId;

    @Column(nullable = true)
    @ElementCollection
    List<String> playerTokens;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getPlayerTokens() {
        return playerTokens;
    }

    public void setPlayerTokens(List<String> playerTokens) {
        this.playerTokens = playerTokens;
    }

    /**Getters and Setters*/




    public Long getHostId() {
        return hostId;
    }

    public void setHostId(Long hostId) {
        this.hostId = hostId;
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

    public List<String> getPlayerToken() {
        return PlayerToken;
    }

    public void setPlayerToken(List<String> playerToken) {
        this.PlayerToken = playerToken;
    }
}
