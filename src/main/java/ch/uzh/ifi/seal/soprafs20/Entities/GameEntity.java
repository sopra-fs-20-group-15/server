package ch.uzh.ifi.seal.soprafs20.Entities;

import ch.uzh.ifi.seal.soprafs20.GameLogic.*;

import javax.persistence.*;
import java.util.ArrayList;
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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Long activeCardId;

    @Column(nullable = true)
    private String activeMysteryWord;

    @Column(nullable = true)
    private Long activePlayerId;

    @Column(nullable = true)
    private Boolean rightGuess;

    @Column(nullable = true)
    private Boolean validClue;

    @Column(nullable = true)
    private Boolean validCluesAreSet = false;


    @ElementCollection
    List<PlayerEntity> players;

    @ElementCollection
    private List<Angel> angels;

    @ElementCollection
    private List<Devil> devils;

    @ElementCollection
    List<Long> passivePlayerIds;

    @ElementCollection
    List<Long> CardIds;

    @ElementCollection
    Map<String, String> clueMap;

    @ElementCollection
    List<String> validClues;

    @Embedded
    private Scoreboard scoreboard;

    @Column(nullable = true)
    private Long Milliseconds;

    @Column
    private String Guess;

    @Column
    private boolean isValidGuess;

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

    public void setClueMap(Map<String, String> clueList) {
        this.clueMap = clueList;
    }

    public Map<String, String> getClueMap() {
        return clueMap;
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


    public Boolean getValidClue() {
        return validClue;
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

    public void setMilliseconds(Long milliseconds) {
        Milliseconds = milliseconds;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setAngels(List<Angel> angels) {
        this.angels = angels;
    }

    public List<Angel> getAngels() {
        return angels;
    }

    public void setDevils(List<Devil> devils) {
        this.devils = devils;
    }

    public List<Devil> getDevils() {
        return devils;
    }

    public int getNumOfBots(){
        return devils.size()+angels.size();
    }

    public List<Bot> getNamesOfBots(){
        List<Bot> bots = new ArrayList<>();
        bots.addAll(angels);
        bots.addAll(devils);
        return bots;
    }

    public void setPassivePlayerIds(List<Long> passivePlayerIds) {
        this.passivePlayerIds = passivePlayerIds;
    }

    private int getNumOfDuplicates(PlayerEntity player){
        String  clue = clueMap.get(player.getUsername()).toLowerCase();
        int cnt=-1;
        for (String clue2: clueMap.values()
             ) {
            if (clue.equals(clue2.toLowerCase())) cnt++;
        }
        return cnt;
    }

     public void updateScoreboard(){
        for (PlayerEntity player: players) {
            if (activePlayerId.equals(player.getId())) scoreboard.updateScore(player,
                    ScoreCalculator.calculateScoreActivePlayer(player,isValidGuess, 33000 - getMilliseconds()));
            else if (passivePlayerIds.contains(player.getId())) {
                if (validClues.contains(clueMap.get(player.getUsername()))) scoreboard.updateScore(player,
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

    public void setCardIds(List<Long> cardIds) {
        CardIds = cardIds;
    }

    public void setIsValidGuess(boolean validGuess) {
        isValidGuess = validGuess;
    }

    public boolean getIsValidGuess(){
        return this.isValidGuess;
    }
}
