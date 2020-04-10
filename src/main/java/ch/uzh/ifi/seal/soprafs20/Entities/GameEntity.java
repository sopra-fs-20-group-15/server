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
@Table(name = "GAME")
public class GameEntity {
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
/
    @Column(nullable = true)
    private Long activeCardId;

    @Column(nullable = true)
    private String activeWord;

    @Column(nullable = true)
    private Long activePlayerId;

    @Column(nullable = true)
    private Boolean rightGuess;

    @Column(nullable = true)
    private Boolean validClue;
/**
    @ElementCollection
    List<Long> passivePlayerIds;

    @ElementCollection
    List<Long> CardIds;

    @ElementCollection
    Map<PlayerEntity,Integer> ScoreBoard;
 */

    @Column(nullable = true)
    private Long nrOfDuplicates;

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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActiveCardId() {
        return activeCardId;
    }

    public void setActiveCardId(Long cardId) {
        this.activeCardId = cardId;
    }

    public String getActiveWord() {
        return activeWord;
    }

    public void setActiveWord(String activeWord) {
        this.activeWord = activeWord;
    }

    public Boolean getRightGuess() {
        return rightGuess;
    }

    public Boolean getValidClue() {
        return validClue;
    }

    public Long getActivePlayerId() {
        return activePlayerId;
    }



    public Long getNrOfDuplicates() {
        return nrOfDuplicates;
    }

    public void setActivePlayerId(Long activePlayerId) {
        this.activePlayerId = activePlayerId;
    }




    public void setRightGuess(Boolean rightGuess) {
        this.rightGuess = rightGuess;
    }

    public void setValidClue(Boolean validClue) {
        this.validClue = validClue;
    }

    public void setNrOfDuplicates(Long nrOfDuplicates) {
        this.nrOfDuplicates = nrOfDuplicates;
    }
/**
    public Map<PlayerEntity, Integer> getScoreBoard() {
        return ScoreBoard;
    }

    public List<Long> getCardIds() {
        return CardIds;
    }

    public List<Long> getPassivePlayerIds() {
        return passivePlayerIds;
    }


    public void setPassivePlayerIds(List<Long> passivePlayerIds) {
        this.passivePlayerIds = passivePlayerIds;
    }

    public void setScoreBoard(Map<PlayerEntity, Integer> scoreBoard) {
        ScoreBoard = scoreBoard;
    }

    public void setCardIds(List<Long> cardIds) {
        CardIds = cardIds;
    }*/
}
