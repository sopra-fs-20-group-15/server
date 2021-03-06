package ch.uzh.ifi.seal.soprafs20.Entities;

import ch.uzh.ifi.seal.soprafs20.constant.GameType;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;

import javax.persistence.*;
import java.util.List;

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
    private String gameName;

    @Column(nullable = false)
    private Long numberOfPlayers;

    @Column(nullable = false)
    private Long numberOfAngles;

    @Column(nullable = false)
    private Long numberOfDevils;

    @Column(nullable = false)
    private GameType gameType;

    @Column(nullable = true)
    private String password;

    @Column(nullable = true)
    private Long activeGameId;

    @Column(nullable = false)
    private String hostName;

    @Column(nullable = true)
    @ElementCollection
    List<String> playerTokens;

    @OneToOne
    @JoinColumn
    private ChatEntity chat;

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

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setChat(ChatEntity chat) {
        this.chat = chat;
    }

    public ChatEntity getChat() {
        return chat;
    }

    /**Getters and Setters*/

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setActiveGameId(Long activeGameId) {
        this.activeGameId = activeGameId;
    }

    public Long getActiveGameId() {
        return activeGameId;
    }

    public Long getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(Long numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public Long getNumberOfAngles() {
        return numberOfAngles;
    }

    public void setNumberOfAngles(Long numberOfAngles) {
        this.numberOfAngles = numberOfAngles;
    }

    public Long getNumberOfDevils() {
        return numberOfDevils;
    }

    public void setNumberOfDevils(Long numberOfDevils) {
        this.numberOfDevils = numberOfDevils;
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
