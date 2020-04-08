package ch.uzh.ifi.seal.soprafs20.GameLogic;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.PlayerNotAvailable;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ValidationService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public ValidationService(@Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }
/**
 * Checks if the given Player is the active player of a game
 * @Param: String playerToken, Long gameId
 * @throws: 404 not found, wenn es Player oder Game nicht gibt
 * @return : boolean: true, wenn player aktiver player des games ist
 * */
    boolean playerIsActivePlayerOfGame(String playerToken, Long gameId){
        PlayerEntity playerByToken = playerRepository.findByToken(playerToken);
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        //Check, that both player and game exist
        if (gameOp.isEmpty()) throw new NotFoundException("No gameEntity with specified ID exists.");
        if (playerByToken ==null) throw new PlayerNotAvailable("No player with same token as your session exists.");
        //Check, that player is active Player of that game

    }
    boolean playerIsPassivePlayerOfGame(String playerToken, Long gameId){
        PlayerEntity playerByToken = playerRepository.findByToken(playerToken);
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        //Check, that both player and game exist
        if (gameOp.isEmpty()) throw new NotFoundException("No gameEntity with specified ID exists.");
        if (playerByToken ==null) throw new PlayerNotAvailable("No player with same token as your session exists.");
    }
    boolean playerIsPartOfGame(String playerToken, Long gameId){
        PlayerEntity playerByToken = playerRepository.findByToken(playerToken);
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        //Check, that both player and game exist
        if (gameOp.isEmpty()) throw new NotFoundException("No gameEntity with specified ID exists.");
        if (playerByToken ==null) throw new PlayerNotAvailable("No player with same token as your session exists.");
    }
}
