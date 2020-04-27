package ch.uzh.ifi.seal.soprafs20.Entities;

import ch.uzh.ifi.seal.soprafs20.GameLogic.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Entity
//Even though there is a red line beneath "GAME", it should still work
@Table(name = "GAME")
public class GameEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Boolean hasBeenInitialized;

    @Column(nullable = true)
    private Long activeCardId;

    @Column(nullable = true)
    private String activeMysteryWord;

    @Column(nullable = true)
    private Long activePlayerId;

    @Column(nullable = true)
    private Boolean rightGuess;

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
    Map<String,String> validClues;

    @ElementCollection
    Map<String,Integer> analyzedClues;

    @Embedded
    private Scoreboard scoreboard;

    @Column
    private String Guess;

    @Column
    private boolean isValidGuess;

    @Column
    private Long timeStart;

    @Column(nullable = true)
    private Boolean hasEnded;

    public void setTimeStart(Long timeStart) {
        this.timeStart = timeStart;
    }

    public Long getTimeStart() {
        return timeStart;
    }

    public Map<String, Integer> getAnalyzedClues() {
        return analyzedClues;
    }

    public void setAnalyzedClues(Map<String, Integer> analyzedClues) {
        this.analyzedClues = analyzedClues;
    }

    public void setValidCluesAreSet(Boolean validCluesAreSet) {
        this.validCluesAreSet = validCluesAreSet;
    }

    public Boolean getValidCluesAreSet() {
        return validCluesAreSet;
    }

    public void setValidClues(Map<String, String> validClues) {
        this.validClues = validClues;
    }

    public Map<String, String> getValidClues() {
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

    public Long getActivePlayerId() {
        return activePlayerId;
    }

    public Long getId() {
        return id;
    }

    public Boolean getRightGuess() {
        return rightGuess;
    }

    public void setRightGuess(Boolean rightGuess) {
        this.rightGuess = rightGuess;
    }

    public Boolean getHasBeenInitialized() {
        return hasBeenInitialized;
    }

    public void setHasBeenInitialized(Boolean hasBeenInitialized) {
        this.hasBeenInitialized = hasBeenInitialized;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public boolean isValidGuess() {
        return isValidGuess;
    }

    public void setValidGuess(boolean validGuess) {
        isValidGuess = validGuess;
    }

    public List<Long> getCardIds() {
        return CardIds;
    }

    public List<Long> getPassivePlayerIds() {
        return passivePlayerIds;
    }

    public void setActivePlayerId(Long activePlayerId) {
        this.activePlayerId = activePlayerId;
    }

    public void setId(Long id) {
        this.id = id;
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
         for (PlayerEntity player: this.getPlayers()) {
             if (this.getActivePlayerId().equals(player.getId())) {
                 Scoreboard sc =this.getScoreboard();
                 sc.updateScore(player, ScoreCalculator.calculateScoreActivePlayer(player, this.getIsValidGuess()));
                 if (this.getIsValidGuess()){
                     Map<String, Integer> correctlyGuessedMysteryWordsPerPlayer = scoreboard.getCorrectlyGuessedMysteryWordsPerPlayer();
                     correctlyGuessedMysteryWordsPerPlayer.replace(player.getUsername(), correctlyGuessedMysteryWordsPerPlayer.get(player.getUsername())+ 1);
                     scoreboard.setCorrectlyGuessedMysteryWordsPerPlayer(correctlyGuessedMysteryWordsPerPlayer);
                 }
                 this.setScoreboard(sc);
             }
             else {
                 int numOfDuplicates = this.getAnalyzedClues().get(this.getClueMap().get(player.getToken()));
                 boolean validClue = this.getValidClues().containsKey(player.getUsername());
                 Scoreboard sc = this.getScoreboard();
                 sc.updateScore(player, ScoreCalculator.calculateScorePassivePlayer(player, this.getIsValidGuess(), validClue, numOfDuplicates));
                 this.setScoreboard(sc);
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

    public Boolean getHasEnded() {
        return hasEnded;
    }

    public void setHasEnded(Boolean hasEnded) {
        this.hasEnded = hasEnded;
    }
}
