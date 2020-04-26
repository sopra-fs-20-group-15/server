package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.PlayerNotAvailable;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
/**Helper Method for the Controller and Services
 * Checks if a player is part of a game and which role he or she has in that game
 * Ps. At the moment there is a code duplicate of six lines but putting them into a helper method is complicated*/
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
 * @returns: boolean: true, if it is the active player
 * @throws: 404 not found, if player or game does not exist
 * @throws: 401 unauthorized, if player ist not the active player of the game
 * */
    public boolean checkPlayerIsActivePlayerOfGame(String playerToken, Long gameId){
        PlayerEntity playerByToken = playerRepository.findByToken(playerToken);
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        //Check, that both player and game exist
        if (gameOp.isEmpty()) throw new NotFoundException("No gameEntity with specified ID exists.");
        if (playerByToken ==null) throw new PlayerNotAvailable("No player with same token as your session exists.");
        //Check, that player is active Player of that game
        GameEntity game = gameOp.get();
        if (game.getActivePlayerId() == playerByToken.getId())
            return true;
        else
            throw new UnauthorizedException("This player is not the active player!");

    }

    /**
     * Checks if the given Player is the passive player of a game
     * @Param: String playerToken, Long gameId
     * @returns: boolean: true, if it is a passive player
     * @throws: 404 not found, if player or game does not exist
     * @throws: 401 unauthorized, if player ist not a passive player of the game
     * */
    public boolean checkPlayerIsPassivePlayerOfGame(String playerToken, Long gameId){
        PlayerEntity playerByToken = playerRepository.findByToken(playerToken);
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        //Check, that both player and game exist
        if (gameOp.isEmpty()) throw new NotFoundException("No gameEntity with specified ID exists.");
        if (playerByToken ==null) throw new PlayerNotAvailable("No player with same token as your session exists.");
        GameEntity game = gameOp.get();
        //Check that player is one of the passive players
        if (game.getPassivePlayerIds().contains(playerByToken.getId()))
            return true;
        else
            throw new UnauthorizedException("This player is not a passive player!");
    }

    /**
     * Checks if the given Player is the passive player of a game
     * @Param: String playerToken, Long gameId
     * @returns: boolean: true, if player is part of the game
     * @throws: 404 not found, if player or game does not exist
     * @throws: 401 unauthorized, if the player is not part of the game
     * */
    public boolean checkPlayerIsPartOfGame(String playerToken, Long gameId){
        PlayerEntity playerByToken = playerRepository.findByToken(playerToken);
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        //Check, that both player and game exist
        if (gameOp.isEmpty()) throw new NotFoundException("No gameEntity with specified ID exists.");
        if (playerByToken == null) throw new PlayerNotAvailable("No player with same token as your session exists.");
        GameEntity game = gameOp.get();
        //Check that player is part of the game
        if (game.getPassivePlayerIds().contains(playerByToken.getId()) || game.getActivePlayerId() == playerByToken.getId())
            return true;
        else
            throw new UnauthorizedException("This player is not part of the game!");
    }
}
