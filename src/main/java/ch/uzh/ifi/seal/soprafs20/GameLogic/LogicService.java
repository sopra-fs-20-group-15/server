package ch.uzh.ifi.seal.soprafs20.GameLogic;


import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;

import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        //somehow update game with guess, and isValidGuess

    }


}
