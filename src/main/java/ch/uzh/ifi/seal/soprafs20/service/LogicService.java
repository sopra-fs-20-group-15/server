package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Scoreboard;
import ch.uzh.ifi.seal.soprafs20.GameLogic.WordComparer;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.WordPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.*;

/**
 * PlayerEntity Service

 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class LogicService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final WordComparer wordComparer;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final CardService cardService;

    @Autowired
    public LogicService(@Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameRepository") GameRepository gameRepository, CardService cardService) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.wordComparer = new WordComparer();
        this.cardService = cardService;
    }

    /**Puts the active player at the end of the passive Players List, takes the passive Player at 0 and make him the active player*/
    protected GameEntity goOnePlayerFurther(GameEntity game){
        List<Long> passivePlayerIds = game.getPassivePlayerIds();
        passivePlayerIds.add(game.getActivePlayerId());
        Long playerId = passivePlayerIds.remove(0);
        game.setActivePlayerId(playerId);
        game.setPassivePlayerIds(passivePlayerIds);
        return game;
    }

    protected GameEntity drawCardFromStack(GameEntity game){
        if (game.getCardIds().size() > 0){
            List<Long> cardIds = game.getCardIds();
            game.setActiveCardId(cardIds.remove(cardIds.size()-1));
            return game;
        }
        else{
            throw new ConflictException("The CardStack is empty!The game should have ended already!");
        }
    }

    public GameEntity initializeTurn(Long gameId){
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        GameEntity game = gameOp.get();
        if (!game.getHasBeenInitialized()){
            if (game.getHasEnded()){
                game.setHasBeenInitialized(true);
                game.setActiveMysteryWord("");
                game.setHasEnded(false);
                //Update the players
                goOnePlayerFurther(game);
                //Update Cards
                drawCardFromStack(game);
                game.setRightGuess(false);
                game.setValidClue(false);
                game.setValidCluesAreSet(false);
                game.setClueMap(new HashMap<String, String>());
                game.setValidClues(new HashMap<String, String>());
                game.setGuess("");
                game.setIsValidGuess(false);
                return game;
/**Milliseconds?*/
        }
            else {throw new ConflictException("The game has not ended yet!");}}
        else {throw new NoContentException("The Game has already been initialized!");}
    }

    /**Gets a gameId and gives back the active Card of that game*/
    public CardEntity getCardFromGameById(Long gameId){
        //Get Game
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        GameEntity game = gameOp.get();
        //Check if activeCard has already been set and if so, get the Card
        Long cardId = game.getActiveCardId();
        CardEntity card;
        if (cardId != null){
            card = cardService.getCardById(cardId);
            return card;
        }
        else {throw new NotFoundException("The active Card of this game has not been set yet!");}
    }


    /**Set the MysteryWord*/

    public String setMysteryWord(Long gameId, Long wordId){
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        GameEntity game = gameOp.get();
        if (game.getActiveMysteryWord().isBlank()){
            CardEntity card = cardService.getCardById(game.getActiveCardId());
            String word = cardService.chooseWordOnCard(wordId, card);
            game.setActiveMysteryWord(word);
            return word;
        }
        else {throw new NoContentException("The MysteryWord has already been set");}
    }

    /**Get the MysteryWord*/

    public String getMysteryWord(Long gameId){
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        GameEntity game = gameOp.get();
        if (!game.getActiveMysteryWord().isBlank()){
            return game.getActiveMysteryWord();
        }
        else {throw new NotFoundException("The MysteryWord not been set yet!");}
    }

    public void setGuess(GameEntity game, String guess){
        boolean isValidGuess = wordComparer.compareMysteryWords(game.getActiveMysteryWord(), guess);
        game.setGuess(guess);
        game.setIsValidGuess(isValidGuess);
    }

    public void giveClue(String playerToken, GameEntity game, CluePostDTO cluePostDTO){
        if (game.getClueMap().get(playerToken)==null) {
            Map<String, String> clueMap =game.getClueMap();
            clueMap.put(cluePostDTO.getPlayerToken(),cluePostDTO.getClue());
            game.setClueMap(clueMap);
        }
        else throw new UnauthorizedException("You have already submitted a clue for this round!");
        if (game.getClueMap().size()==game.getPlayers().size()+game.getNumOfBots()-1){
            ArrayList<String> clues = new ArrayList<String>(game.getClueMap().values());
            String mystery=game.getActiveMysteryWord();
            Map<String,Integer> analyzedClues=wordComparer.compareClues(clues, mystery);
            Map<String,String> validClues = new HashMap<>();
            for ( Map.Entry<String,String> entry: game.getClueMap().entrySet()){
                if (playerRepository.findByToken(entry.getKey())!=null) {
                    if (analyzedClues.get(entry.getValue())==0) validClues.put(playerRepository.findByToken(entry.getKey()).getUsername(),
                        entry.getValue());
                }
                else {
                    for (Angel angel : game.getAngels()){
                        if (angel.getToken().equals(entry.getKey())) validClues.put(angel.getName(), entry.getValue());
                    }
                    for (Devil devil : game.getDevils()){
                        if (devil.getToken().equals(entry.getKey())) validClues.put(devil.getName(), entry.getValue());
                    }
                }
            }
            game.setValidClues(validClues);
            game.setValidCluesAreSet(true);
        }
    }


    public List<ClueGetDTO> getClues(GameEntity game) {
//        check if clues have already been set for this round
        if (game.getValidCluesAreSet()) {
            List<ClueGetDTO> list = new ArrayList<>();
            for (String playerName: game.getValidClues().keySet()) {
                ClueGetDTO clueGetDTO =new ClueGetDTO();
                clueGetDTO.setClue(game.getValidClues().get(playerName));
                clueGetDTO.setPlayerName(playerName);
                list.add(clueGetDTO);
            }
            return list;
        }
//        if clues have not been set throw NoContentException
        else throw new NoContentException("Clues are not ready yet!");
    }
}
