package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class LSSWordReveal implements LogicServiceState{

    /**Puts the active player at the end of the passive Players List, takes the passive Player at 0 and make him the active player*/
    protected GameEntity goOnePlayerFurther(GameEntity game){
        List<Long> passivePlayerIds = game.getPassivePlayerIds();
        passivePlayerIds.add(game.getActivePlayerId());
        Long playerId = passivePlayerIds.remove(0);
        game.setActivePlayerId(playerId);
        game.setPassivePlayerIds(passivePlayerIds);
        return game;
    }

    public GameEntity initializeTurn(GameEntity game) {
        game.setHasBeenInitialized(true);
        game.setActiveMysteryWord("");
        game.setHasEnded(false);
        //Update the players
        goOnePlayerFurther(game);
        //Update Cards
        game.drawCardFromStack();
        for (PlayerEntity player : game.getPlayers()) {
            player.setTimePassed(null);
        }
        game.setValidCluesAreSet(false);
        game.setClueMap(new HashMap<String, String>());
        game.setValidClues(new HashMap<String, String>());
        game.setAnalyzedClues(new HashMap<String, Integer>());
        game.setTimeStart(null);
        game.setGuess("");
        game.setIsValidGuess(false);

        //StateChange
        game.setStateForLogicService(State.ChooseMysteryWord);
        return game;
    }

    public String setMysteryWord(GameEntity game, Long wordId){
        throw new ConflictException("The MysteryWord has already been set!");

    }

    public String getMysteryWord(GameEntity game){
        return game.getActiveMysteryWord();
    }

    public void giveClue(GameEntity game, CluePostDTO cluePostDTO){
        throw new ConflictException("All clues have already been given!");
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

    public void setGuess(GameEntity game, String guess){
        throw new ConflictException("the guess has already been set!");
    }


    public String getGuess(GameEntity game) {
        return game.getGuess();
    }
}