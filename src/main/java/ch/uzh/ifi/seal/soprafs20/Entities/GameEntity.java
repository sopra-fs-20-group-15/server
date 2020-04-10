package ch.uzh.ifi.seal.soprafs20.Entities;

import javax.persistence.*;
import java.util.List;
import java.util.Map;


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
    private Long activeCardId;

    @Column(nullable = false)
    private String activeMysteryWord;

    @Column(nullable = false)
    private Long activePlayerId;

    @Column(nullable = false)
    private String Guess;

    @Column(nullable = false)
    private Boolean isValidGuess;

    @Column(nullable = false)
    private Boolean validClue;

    @ElementCollection
    List<PlayerEntity> players;

    @ElementCollection
    List<Long> passivePlayerIds;

    @ElementCollection
    List<Long> CardIds;

    @ElementCollection
    Map<String, String> clueList;

    @ElementCollection
    List<String> validClues;

    @ElementCollection
    Map<PlayerEntity,Integer> ScoreBoard;

    @Column(nullable = false)
    private Long Milliseconds;

    @Column(nullable = false)
    private Long nrOfDuplicates;

    public GameEntity() {
    }

    public void setValidClues(List<String> validClues) {
        this.validClues = validClues;
    }

    public List<String> getValidClues() {
        return validClues;
    }

    public void setClueList(Map<String, String> clueList) {
        this.clueList = clueList;
    }

    public Map<String, String> getClueList() {
        return clueList;
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerEntity> players) {
        this.players = players;
    }

    public Long getActiveCardId() {
        return activeCardId;
    }

    public void setActiveCardId(Long cardId) {
        this.activeCardId = cardId;
    }

    public String getActiveMysteryWord() {
        return activeMysteryWord;
    }

    public void setActiveMysteryWord(String activeMysteryWord) {
        this.activeMysteryWord = activeMysteryWord;
    }

    public Boolean getIsValidGuess() {
        return isValidGuess;
    }

    public Boolean getValidClue() {
        return validClue;
    }

    public Long getActivePlayerId() {
        return activePlayerId;
    }

    public Long getId() {
        return id;
    }

    public Map<PlayerEntity, Integer> getScoreBoard() {
        return ScoreBoard;
    }

    public List<Long> getCardIds() {
        return CardIds;
    }

    public List<Long> getPassivePlayerIds() {
        return passivePlayerIds;
    }

    public Long getMilliseconds() {
        return Milliseconds;
    }

    public Long getNrOfDuplicates() {
        return nrOfDuplicates;
    }

    public void setActivePlayerId(Long activePlayerId) {
        this.activePlayerId = activePlayerId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCardIds(List<Long> cardIds) {
        CardIds = cardIds;
    }

    public void setMilliseconds(Long milliseconds) {
        Milliseconds = milliseconds;
    }

    public void setIsValidGuess(Boolean isValidGuess) {
        this.isValidGuess = isValidGuess;
    }

    public void setValidClue(Boolean validClue) {
        this.validClue = validClue;
    }

    public void setNrOfDuplicates(Long nrOfDuplicates) {
        this.nrOfDuplicates = nrOfDuplicates;
    }

    public void setPassivePlayerIds(List<Long> passivePlayerIds) {
        this.passivePlayerIds = passivePlayerIds;
    }

    public void setScoreBoard(Map<PlayerEntity, Integer> scoreBoard) {
        ScoreBoard = scoreBoard;
    }

    public String getGuess() {
        return Guess;
    }

    public void setGuess(String guess) {
        Guess = guess;
    }
}