package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;

import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
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
    private final GameSetUpRepository gameSetUpRepository;

    @Autowired
    public GameService(@Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository,@Qualifier("gameRepository") GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.gameSetUpRepository = gameSetUpRepository;
    }

    public GameEntity getGameById(Long id){
        Optional<GameEntity> gameOp = gameRepository.findById(id);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        return gameOp.get();
    }

    /**Creates a game. GameToken should be checked beforehand so that player exists*/
    public GameSetUpEntity createGame(GameSetUpEntity game) {
        //Check, if parameters are acceptable
        if (game.getNumberOfPlayers() < 8 && game.getNumberOfPlayers() > 2) {
            if (game.getNumberOfBots() > -1 && game.getNumberOfBots() < game.getNumberOfPlayers()){
             if(game.getGameType().name().equals("PRIVATE")){
                if(game.getPassword() != null && ! game.getPassword().isEmpty()){
                    GameSetUpEntity newGame = gameSetUpRepository.save(game);
                    gameRepository.flush();
                    return newGame;
                }
                else{
                    throw new ConflictException("The Password should not be empty or null!");
                }
             }
             // If it is a public game
             else{
                 GameSetUpEntity newGame = gameSetUpRepository.save(game);
                 gameRepository.flush();
                 return newGame;
             }
            }
            else{
                throw new ConflictException("The number of Player should be smaller than the number of players and bigger than 0");
            }
        }
        else{
            throw new ConflictException("The number of Player should be smaller than 7 and bigger than 3");
        }
    }

    /**Puts a player into a gameSetUp if all the requirements for that are met*/
    public void putPlayerIntoGame(Long gameId, PlayerEntity player){
//Check if gameSetUpId exists
    }
    public void updateLeaderBoard(GameEntity game){
        for (PlayerEntity player : game.getScoreboard().getEndScore().keySet()){
            Optional<PlayerEntity> playerToBeUpdated = playerRepository.findById(player.getId());
            playerToBeUpdated.ifPresent(value -> value.setScore(value.getScore() + game.getScoreboard().getEndScore().get(player)));
        }
    }


}
