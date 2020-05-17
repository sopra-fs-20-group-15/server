package ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.service.State;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class LSSWordReveal implements LogicServiceState{

    /**Puts the active player at the end of the passive Players List, takes the passive Player at 0 and make him the active player*/
    protected GameEntity goOnePlayerFurther(GameEntity game){
        List<Long> passivePlayerIds = game.getPassivePlayerIds();
        // check that active player was not removed; if not: make him a passive player
        if (game.getActivePlayerId()!=null) passivePlayerIds.add(game.getActivePlayerId());
        Long playerId = passivePlayerIds.remove(0);
        game.setActivePlayerId(playerId);
        game.setPassivePlayerIds(passivePlayerIds);
        return game;
    }

    public GameEntity initializeTurn(GameEntity game) {
        //First remove all players that did unceremoniously leave the game
        game.getHandlerForLeavingPlayers().removeInactivePlayers(game);
        game.setActiveMysteryWord("");
        //Update the players
        if (game.getPassivePlayerIds().size()>0) goOnePlayerFurther(game);
        //Update Cards
        game.drawCardFromStack();
        for (PlayerEntity player : game.getPlayers()) {
            player.setTimePassed(null);
        }
        game.setClueMap(new HashMap<String, String>());
        game.setValidClues(new HashMap<String, String>());
        game.setAnalyzedClues(new HashMap<String, Integer>());
        game.setTimeStart(System.currentTimeMillis());
        game.setGuess("");
        game.setIsValidGuess(false);

        //StateChange
        game.setStateForLogicService(State.ChooseMysteryWord);
        return game;
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

    public void setGuess(GameEntity game, String guess){
        throw new NoContentException("the guess has already been set!");
    }

    public String getGuess(GameEntity game) {
        return game.getGuess();
    }

    /**if no player initializes, do this automatically*/
    public void endRoundAutomatically(GameEntity game){
        initializeTurn(game);
    }
}
