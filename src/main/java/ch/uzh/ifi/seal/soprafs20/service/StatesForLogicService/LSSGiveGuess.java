package ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.ScoreCalculator;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Scoreboard;
import ch.uzh.ifi.seal.soprafs20.GameLogic.WordComparer;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.service.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


@Service
@Transactional
public class LSSGiveGuess implements LogicServiceState{

    private final WordComparer wordComparer;

    @Autowired
    public LSSGiveGuess() {
        this.wordComparer = new WordComparer();
    }

    /**sets the timer for game*/
    protected void setTimeStart(GameEntity game){
        game.setTimeStart(System.currentTimeMillis());
    }

    /**sets the timer for the player*/
    protected void setTimePassed(GameEntity game, PlayerEntity player){
        player.setTimePassed(System.currentTimeMillis()-game.getTimeStart());
    }

    public GameEntity initializeTurn(GameEntity game){
        throw new NoContentException("You cannot initialize in this phase!");
    }

    public String setMysteryWord(GameEntity game, Long wordId){
        throw new NoContentException("The MysteryWord has already been set!");

    }

    public String getMysteryWord(GameEntity game){
        return game.getActiveMysteryWord();
    }

    public void giveClue(Long GameId, CluePostDTO cluePostDTO){
        throw new NoContentException("All clues have already been given!");
    }

    protected List<ClueGetDTO> createListOfClueGetDTOs (GameEntity game){
        List<ClueGetDTO> list = new ArrayList<>();
        for (String playerName: game.getValidClues().keySet()) {
            ClueGetDTO clueGetDTO =new ClueGetDTO();
            clueGetDTO.setClue(game.getValidClues().get(playerName));
            clueGetDTO.setPlayerName(playerName);
            list.add(clueGetDTO);
        }
        return list;
    }

    public List<ClueGetDTO> getClues(GameEntity game) {
            return createListOfClueGetDTOs(game);
    }

    /**updates the scoreboard*/
    protected void updateScoreboard(GameEntity game){
        for (PlayerEntity player: game.getPlayers()) {
            //for active players
            if (game.getActivePlayerId().equals(player.getId())) {
                Scoreboard scoreboard =game.getScoreboard();
                scoreboard.updateScore(player, ScoreCalculator.calculateScoreActivePlayer(player, game.getIsValidGuess()));
                //Update the amount of correctly guessed mystery words.
                if (game.getIsValidGuess()){
                    Map<String, Integer> correctlyGuessedMysteryWordsPerPlayer = game.getScoreboard().getCorrectlyGuessedMysteryWordsPerPlayer();
                    correctlyGuessedMysteryWordsPerPlayer.replace(player.getUsername(), correctlyGuessedMysteryWordsPerPlayer.get(player.getUsername())+ 1);
                    game.getScoreboard().setCorrectlyGuessedMysteryWordsPerPlayer(correctlyGuessedMysteryWordsPerPlayer);
                }
                game.setScoreboard(scoreboard);
            }
            //for passive players
            else {
                int numOfDuplicates = game.getAnalyzedClues().get(game.getClueMap().get(player.getToken()));
                boolean validClue = game.getValidClues().containsKey(player.getUsername());
                Scoreboard scoreboard = game.getScoreboard();
                scoreboard.updateScore(player, ScoreCalculator.calculateScorePassivePlayer(player, game.getIsValidGuess(), validClue, numOfDuplicates));
                game.setScoreboard(scoreboard);
            }
        }
    }

    /**Set the Guess
     *@Param: GameEntity, String
     *@Returns: void
     * */
    public void setGuess(GameEntity game, String guess){
        boolean isValidGuess = wordComparer.compareMysteryWords(game.getActiveMysteryWord(), guess);
        game.setGuess(guess);
        game.setIsValidGuess(isValidGuess);
        for (PlayerEntity player:game.getPlayers()) {
            if (player.getId().equals(game.getActivePlayerId())) setTimePassed(game, player);
        }
        //draw an extra card if the guess was wrong
        if (!isValidGuess && !(game.getCardIds().size() == 0)){
            game.drawCardFromStack();
        }
        //Time to dish out some points fam!
        updateScoreboard(game);
        game.setStateForLogicService(State.WordReveal);
        game.setTimeStart(System.currentTimeMillis());
        if ( game.getCardIds().size() == 0){
            game.setStateForLogicService(State.hasEnded);
        }
    }

    public String getGuess(GameEntity game) {
        throw new NoContentException("The guess has to be set first!");
    }

    /**If active does not give a guess in time, do this automatically*/
    public void endRoundAutomatically(GameEntity game){
        setGuess(game ,"invalid_guess");
    }
}
