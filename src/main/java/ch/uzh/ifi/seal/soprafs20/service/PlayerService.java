package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * PlayerEntity Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;


    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository,@Qualifier("gameRepository") GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;

    }

    /**Getters*/

    public PlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public PlayerEntity getPlayerById(long id){
        Optional<PlayerEntity> playerOp = playerRepository.findById(id);
        if (playerOp.isEmpty()) throw new NotFoundException("No player with this id exists!");
        return playerOp.get();

    }

    public PlayerEntity getPlayerByToken(String token){
        PlayerEntity player = playerRepository.findByToken(token);
        if (player == null) throw new PlayerNotAvailable("No playerEntity with same token as your session exists.");
        return player;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the PlayerEntity entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param playerEntityToBeCreated
     * @throws UsernameAlreadyExists
     * @see PlayerEntity
     */
    private void checkIfUserNameAlreadyInUse(PlayerEntity playerEntityToBeCreated) {
        PlayerEntity playerEntityByUsername = playerRepository.findByUsername(playerEntityToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (playerEntityByUsername != null) {
            throw new UsernameAlreadyExists(String.format(baseErrorMessage, "username", "is"));
        }

    }

    /** Creates a User
     * @Param: PlayerEntity newPlayerEntity
     * @Returns: PlayerEntity
     * @Throws: 409: The username exists already
     * @Throws: 422: Unprocessable Entity; username or password is empty
     * */
    public PlayerEntity createUser(PlayerEntity newPlayerEntity) {
        newPlayerEntity.setToken(UUID.randomUUID().toString());
        newPlayerEntity.setStatus(PlayerStatus.OFFLINE);
        newPlayerEntity.setLeaderBoardScore(0);

        checkIfUserNameAlreadyInUse(newPlayerEntity);

        if (newPlayerEntity.getPassword().equals("")|| newPlayerEntity.getUsername().equals("")) throw new IllegalRegistrationInput("Username and/or password can't consist of an empty string!");

        // saves the given entity but data is only persisted in the database once flush() is called
        newPlayerEntity = playerRepository.save(newPlayerEntity);
        playerRepository.flush();

        log.debug("Created Information for PlayerEntity: {}", newPlayerEntity);
        return newPlayerEntity;
    }

    /** Let a player login
     * @Param: PlayerEntity
     * @Returns: PlayerEntity
     * @Throws: 401: the password is not correct for this player
     * @Throws: 404: the player does not exist
     * */
    public PlayerEntity loginUser(PlayerEntity potPlayerEntity){
        PlayerEntity playerEntity = playerRepository.findByUsername(potPlayerEntity.getUsername());
        if (playerEntity ==null) throw new PlayerNotAvailable("No playerEntity with this username exists.");
        else if (playerEntity.getPassword().equals(potPlayerEntity.getPassword())) {
            if (playerEntity.getStatus().equals(PlayerStatus.OFFLINE)) {
                playerEntity.setStatus(PlayerStatus.ONLINE);
            }
            return playerEntity;
        }
        else throw new UnauthorizedException("Incorrect password.");
    }

    /** Lets a player logout
     * @Param: PlayerEntity
     * @Throws: 404: the player does not exist
     * @Throws: 409: The player is currently playing a game and cannot logout
     * */
    public void logOutUser(PlayerEntity playerEntityInput){
        PlayerEntity playerEntity = playerRepository.findByToken(playerEntityInput.getToken());
        if (playerEntity ==null) throw new PlayerNotAvailable("No playerEntity with same token as your session exists.");
        else if (playerEntity.getStatus().equals(PlayerStatus.ONLINE)) {
            for (GameEntity game: gameRepository.findAll()) {
                if (game.getPlayers().contains(playerEntity)) throw new ConflictException("Can't logout while in a game session!");
            }
            playerEntity.setStatus(PlayerStatus.OFFLINE);
        }
        else throw new PlayerAlreadyLoggedOut();
    }

    public List<PlayerEntity> getUsers() {
        return this.playerRepository.findAll();
    }

    /** Check if player with given token exists
     * @Param: String playerToken
     * @Returns: void
     * @Throws: 404: If user does not exist a PlayerNotAvailable exception is thrown
     * */
    public void checkIfPlayerExistsByToken(String playerToken){
        getPlayerByToken(playerToken);
    }

}
