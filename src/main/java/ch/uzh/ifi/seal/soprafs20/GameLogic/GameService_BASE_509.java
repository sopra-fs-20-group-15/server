package ch.uzh.ifi.seal.soprafs20.GameLogic;

<<<<<<<<< Temporary merge branch 1
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.PlayerNotAvailable;
=========
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
>>>>>>>>> Temporary merge branch 2
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<<<<<<<<< Temporary merge branch 1
import java.util.Optional;

/**
 * PlayerEntity Service
=========
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Player Service
>>>>>>>>> Temporary merge branch 2
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

<<<<<<<<< Temporary merge branch 1
    public GameEntity getGameById(Long id){
        Optional<GameEntity> gameOp = gameRepository.findById(id);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        return gameOp.get();
    }

    public void updateLeaderBoard(GameEntity gameEntity){
        for (PlayerEntity playerEntity : gameEntity.getScoreBoard().keySet()){
            Optional<PlayerEntity> playerToBeUpdated = playerRepository.findById(playerEntity.getId());
            playerToBeUpdated.ifPresent(value -> value.setScore(value.getScore() + gameEntity.getScoreBoard().get(playerEntity)));
=========
    public void updateLeaderBoard(Game game){
        for (Player player : game.getScoreBoard().keySet()){
            Optional<Player> playerToBeUpdated = playerRepository.findById(player.getId());
            playerToBeUpdated.ifPresent(value -> value.setScore(value.getScore() + game.getScoreBoard().get(player)));
>>>>>>>>> Temporary merge branch 2
        }
    }


}
