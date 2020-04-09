package ch.uzh.ifi.seal.soprafs20.GameLogic;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.PlayerNotAvailable;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * PlayerEntity Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    @Autowired
    public GameService(@Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameRepository") GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    public GameEntity getGameById(Long id){
        Optional<GameEntity> gameOp = gameRepository.findById(id);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        return gameOp.get();
    }

    public void updateLeaderBoard(GameEntity gameEntity){
        for (PlayerEntity playerEntity : gameEntity.getScoreBoard().keySet()){
            Optional<PlayerEntity> playerToBeUpdated = playerRepository.findById(playerEntity.getId());
            playerToBeUpdated.ifPresent(value -> value.setScore(value.getScore() + gameEntity.getScoreBoard().get(playerEntity)));
        }
    }


}
