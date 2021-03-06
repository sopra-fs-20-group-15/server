package ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.GameLogic.WordComparer;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.service.ActiveGameService;
import ch.uzh.ifi.seal.soprafs20.service.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class LSSGiveClues implements LogicServiceState{

    private final WordComparer wordComparer;
    private final PlayerRepository playerRepository;
    private final ActiveGameService gameService;

    @Autowired
    public LSSGiveClues(@Qualifier("playerRepository") PlayerRepository playerRepository, ActiveGameService gameService) {
        this.wordComparer = new WordComparer();
        this.playerRepository = playerRepository;
        this.gameService = gameService;
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
        throw new NoContentException("You cannot initialize before the turn has ended!");
    }

    public String setMysteryWord(GameEntity game, Long wordId){
        throw new NoContentException("The MysteryWord has already been set!");

    }

    public String getMysteryWord(GameEntity game){
            return game.getActiveMysteryWord();
    }

    /**adds all the valid clues to validClues*/
    protected void addValidClues(GameEntity game){
        // let the wordComparer do it's magic and return the analyzed clues
        ArrayList<String> clues = new ArrayList<String>(game.getClueMap().values());
        String mystery=game.getActiveMysteryWord();
        game.setAnalyzedClues(wordComparer.compareClues(clues, mystery));

        //initialize validClues Map and fill it with
        Map<String,String> validClues = new HashMap<>();
        for (Map.Entry<String,String> entry: game.getClueMap().entrySet()){
            //put human players' clues into validClues
            if (playerRepository.findByToken(entry.getKey())!=null) {
                if (game.getAnalyzedClues().get(entry.getValue())==0) validClues.put(playerRepository.findByToken(entry.getKey()).getUsername(),
                        entry.getValue());
            }
            //put bots' clues into validClues
            else {
                for (Angel angel : game.getAngels()){
                    if (angel.getToken().equals(entry.getKey()) && game.getAnalyzedClues().get(entry.getValue())==0)
                        validClues.put(angel.getName(), entry.getValue());
                }
                for (Devil devil : game.getDevils()){
                    if (devil.getToken().equals(entry.getKey()) && game.getAnalyzedClues().get(entry.getValue())==0)
                        validClues.put(devil.getName(), entry.getValue());
                }
            }
        }
        //update validClues of game and set flag
        game.setValidClues(validClues);
        setTimeStart(game);
        game.setStateForLogicService(State.GiveGuess);
    }


    /**adds single clue to clueMap*/
    protected void addClueToClueMap(GameEntity game, CluePostDTO cluePostDTO){
        Map<String, String> clueMap =game.getClueMap();
        clueMap.put(cluePostDTO.getPlayerToken(),cluePostDTO.getClue());
        game.setClueMap(clueMap);

        setTimePassed(game, playerRepository.findByToken(cluePostDTO.getPlayerToken()));
    }

    public void giveClue(Long gameId, CluePostDTO cluePostDTO){
        GameEntity game = gameService.getGameById(gameId);
        if (game.getPassivePlayerIds().size() == 0){
            addValidClues(game);
        }
        else{
            //Check if player has already given clue, if not let him commit a clue
            if (game.getClueMap().get(cluePostDTO.getPlayerToken())==null) {
                addClueToClueMap(game,cluePostDTO);
            }
            else throw new UnauthorizedException("You have already submitted a clue for this round!");
            //Check if all players have given clues, if so set validClues
            if (game.getClueMap().size()==game.getPlayers().size()+game.getNumOfBots()-1){
                addValidClues(game);
            }
        }
    }

    public List<ClueGetDTO> getClues(GameEntity game)
    {throw new NoContentException("The MysteryWord has to be chosen first!");
    }

    public void setGuess(GameEntity game, String guess) {
        throw new NoContentException("The clues have to be set first!");
    }

    public String getGuess(GameEntity game) {
        throw new NoContentException("The clues have to be set first!");
    }

    /**if a human player is inactive, check who has not submitted a clue yet and give an invalid clue for him or her*/
    public void endRoundAutomatically(GameEntity game){
        Map<String, String> clueMap = game.getClueMap();
        for (PlayerEntity player : game.getPlayers()){
            if (!player.getId().equals(game.getActivePlayerId())) {
                //If the clueMap does not contain a clue yet
                if (!clueMap.containsKey(player.getToken())) {
                    CluePostDTO cluePostDTO = new CluePostDTO();
                    cluePostDTO.setPlayerToken(player.getToken());
                    //Take the active mystery word as clue so that the clue is invalid ()
                    cluePostDTO.setClue(game.getActiveMysteryWord());
                    giveClue(game.getId(), cluePostDTO);
                }
            }
        }
    }
}
