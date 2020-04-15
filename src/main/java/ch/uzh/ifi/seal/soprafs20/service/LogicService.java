package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;

import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.GameLogic.WordComparer;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Autowired
    public LogicService(@Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameRepository") GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.wordComparer = new WordComparer();
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
            game.setValidClues(wordComparer.compareClues(clues, mystery));
            game.setValidCluesAreSet(true);
        }
    }


    public List<ClueGetDTO> getClues(GameEntity game) {
//        check if clues have already been set for this round
        if (game.getValidCluesAreSet()) {
            List<ClueGetDTO> list = new ArrayList<>();
            for (String clue: game.getValidClues()) {
                ClueGetDTO clueGetDTO =new ClueGetDTO();
                clueGetDTO.setClue(clue);
                for (Map.Entry<String, String> entry : game.getClueMap().entrySet()) {
                    if (Objects.equals(clue.toLowerCase(), entry.getValue().toLowerCase())) {
//                        check if valid clue was given by bot, if so return bot name
                        if (playerRepository.findByToken(entry.getKey())==null) {
                            for (Angel angel : game.getAngels()){
                                if (angel.getToken().equals(entry.getKey())) clueGetDTO.setPlayerName(angel.getName());
                            }
                            for (Devil devil : game.getDevils()){
                                if (devil.getToken().equals(entry.getKey())) clueGetDTO.setPlayerName(devil.getName());
                            }
                        }
//                        map playerName to DTO if human player
                        else clueGetDTO.setPlayerName(playerRepository.findByToken(entry.getKey()).getUsername());
                    }
                }
                list.add(clueGetDTO);
            }
            return list;
        }
//        if clues have not been set throw NoContentException
        else throw new NoContentException("Clues are not ready yet!");
    }
}
