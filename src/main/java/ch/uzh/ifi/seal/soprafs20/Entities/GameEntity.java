package ch.uzh.ifi.seal.soprafs20.Entities;

import ch.uzh.ifi.seal.soprafs20.GameLogic.*;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.service.State;

import javax.persistence.*;
import java.util.List;
import java.util.Map;


@Entity
//Even though there is a red line beneath "GAME", it should still work
@Table(name = "GAME")
public class GameEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column
    private PlayerCollection playerCollection=new PlayerCollection();

    @Column
    private Cards cards=new Cards();

    @Column
    private Clues clues=new Clues();

    @Column
    private Guess guess=new Guess();

    @Column
    private Turn turn=new Turn();

    @Column
    private Scoreboard scoreboard;

    @Column
    private State stateForLogicService;


    public State getStateForLogicService() {
        return stateForLogicService;
    }

    public void setStateForLogicService(State stateForLogicService) {
        this.stateForLogicService = stateForLogicService;
    }

    public void setTimeStart(Long timeStart) {
        turn.setTimeStart(timeStart);
    }

    public Long getTimeStart() {
        return turn.getTimeStart();
    }

    public Map<String, Integer> getAnalyzedClues() {
        return clues.getAnalyzedClues();
    }

    public void setAnalyzedClues(Map<String, Integer> analyzedClues) {
        clues.setAnalyzedClues(analyzedClues);
    }

    public void setValidCluesAreSet(Boolean validCluesAreSet) {
        clues.setValidCluesAreSet(validCluesAreSet);
    }

    public Boolean getValidCluesAreSet() {
        return clues.getValidCluesAreSet();
    }

    public void setValidClues(Map<String, String> validClues) {
        clues.setValidClues(validClues);
    }

    public Map<String, String> getValidClues() {
        return clues.getValidClues();
    }

    public void setClueMap(Map<String, String> clueList) {
        clues.setClueMap(clueList);
    }

    public Map<String, String> getClueMap() {
        return clues.getClueMap();
    }

    public List<PlayerEntity> getPlayers() {
        return playerCollection.getPlayers();
    }

    public void setPlayers(List<PlayerEntity> players) {
        this.playerCollection.setPlayers(players);
    }


    public Long getActiveCardId() {
        return cards.getActiveCardId();
    }

    public void setActiveCardId(Long cardId) {
        cards.setActiveCardId(cardId);
    }

    public String getActiveMysteryWord() {
        return cards.getActiveMysteryWord();
    }

    public void setActiveMysteryWord(String activeMysteryWord) {
        cards.setActiveMysteryWord(activeMysteryWord);
    }

    public Long getActivePlayerId() {
        return playerCollection.getActivePlayerId();
    }

    public Long getId() {
        return id;
    }

    public Boolean getHasBeenInitialized() {
        return turn.getHasBeenInitialized();
    }

    public void setHasBeenInitialized(Boolean hasBeenInitialized) {
        turn.setHasBeenInitialized(hasBeenInitialized);;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public List<Long> getCardIds() {
        return cards.getCardIds();
    }

    public List<Long> getPassivePlayerIds() {
        return playerCollection.getPassivePlayerIds();
    }

    public void setActivePlayerId(Long activePlayerId) {
        playerCollection.setActivePlayerId(activePlayerId);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setAngels(List<Angel> angels) {
        playerCollection.setAngels(angels);
    }

    public List<Angel> getAngels() {
        return playerCollection.getAngels();
    }

    public void setDevils(List<Devil> devils) {
        playerCollection.setDevils(devils);
    }

    public List<Devil> getDevils() {
        return playerCollection.getDevils();
    }

    public int getNumOfBots(){
        return playerCollection.getAngels().size()+playerCollection.getAngels().size();
    }

    public String getGuess() {
        return guess.getGuess();
    }

    public void setGuess(String guess) {
        this.guess.setGuess(guess);
    }

    public void setCardIds(List<Long> cardIds) {
        cards.setCardIds(cardIds);
    }

    public void setIsValidGuess(boolean validGuess) {
        guess.setValidGuess(validGuess);
    }

    public boolean getIsValidGuess(){
        return guess.getIsValidGuess();
    }

    public Boolean getHasEnded() {
        return turn.getHasEnded();
    }

    public void setHasEnded(Boolean hasEnded) {
        turn.setHasEnded(hasEnded);
    }

    public List<Bot> getBots(){
        return playerCollection.getBots();
    }

    public void setPassivePlayerIds(List<Long> passivePlayerIds) {
        playerCollection.setPassivePlayerIds(passivePlayerIds);
    }


    /**Draws card from stack and sets it as the current active card
     *@Param: GameEntity
     *@Returns: GameEntity
     *@Throws: 409: Empty stack
     *     */
    public GameEntity drawCardFromStack(){
        if (this.getCardIds().size() > 0){
            List<Long> cardIds = this.getCardIds();
            this.setActiveCardId(cardIds.remove(cardIds.size()-1));
            return this;
        }
        else{
            throw new ConflictException("The CardStack is empty! The game should have ended already!");
        }
    }

}
