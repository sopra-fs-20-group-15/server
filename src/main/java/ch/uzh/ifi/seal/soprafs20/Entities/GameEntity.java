package ch.uzh.ifi.seal.soprafs20.Entities;

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
    private Long activeCardId;

    @Column(nullable = false)
    private String activeWord;

    @Column(nullable = false)
    private Long activePlayerId;

    @Column(nullable = false)
    private Boolean rightGuess;

    @Column(nullable = false)
    private Boolean validClue;

    @ElementCollection
    List<Long> passivePlayerIds;

    @ElementCollection
    List<Long> CardIds;

    @ElementCollection
    Map<PlayerEntity,Integer> ScoreBoard;

    @Column(nullable = false)
    private Long Milliseconds;

    @Column(nullable = false)
    private Long nrOfDuplicates;

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

    public void setRightGuess(Boolean rightGuess) {
        this.rightGuess = rightGuess;
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

}
