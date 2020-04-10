package ch.uzh.ifi.seal.soprafs20.Entities;

import ch.uzh.ifi.seal.soprafs20.GameLogic.ScoreCalculator;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Scoreboard;

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
    private Boolean validCluesAreSet = false;

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

    @Embedded
    private Scoreboard scoreboard;

    @Column(nullable = false)
    private Long Milliseconds;

    public void setValidCluesAreSet(Boolean validCluesAreSet) {
        this.validCluesAreSet = validCluesAreSet;
    }

    public Boolean getValidCluesAreSet() {
        return validCluesAreSet;
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


    public Long getActivePlayerId() {
        return activePlayerId;
    }

    public Long getId() {
        return id;
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

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setPassivePlayerIds(List<Long> passivePlayerIds) {
        this.passivePlayerIds = passivePlayerIds;
    }

    private int getNumOfDuplicates(PlayerEntity player){
        String  clue = clueList.get(player.getUsername()).toLowerCase();
        int cnt=-1;
        for (String clue2: clueList.values()
             ) {
            if (clue.equals(clue2.toLowerCase())) cnt++;
        }
        return cnt;
    }

     public void updateScoreboard(){
        for (PlayerEntity player: players) {
            if (activePlayerId.equals(player.getId())) scoreboard.updateScore(player,
                    ScoreCalculator.calculateScoreActivePlayer(player, isValidGuess, 33000 - getMilliseconds()));
            else if (passivePlayerIds.contains(player.getId())) {
                if (validClues.contains(clueList.get(player.getUsername()))) scoreboard.updateScore(player,
                        ScoreCalculator.calculateScorePassivePlayer(player,isValidGuess,true,
                                33000 -getMilliseconds(), getNumOfDuplicates(player)));
                else scoreboard.updateScore(player,
                        ScoreCalculator.calculateScorePassivePlayer(player,isValidGuess,false,
                                33000 -getMilliseconds(), getNumOfDuplicates(player)));
            }
        }

     }
      public String getGuess() {
        	return Guess;
      }

    public void setGuess(String guess) {
        Guess = guess;
    }
}
