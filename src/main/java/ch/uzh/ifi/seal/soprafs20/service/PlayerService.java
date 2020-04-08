package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<PlayerEntity> getUsers() {
        return this.playerRepository.findAll();
    }

<<<<<<< Updated upstream
    public Player createUser(Player newPlayer) {
        newPlayer.setToken(UUID.randomUUID().toString());
        newPlayer.setStatus(PlayerStatus.OFFLINE);
=======
    public List<PlayerEntity> getUsersSortedByPointsDescending(){
        List<PlayerEntity> list = this.playerRepository.findAll();
        list.sort(Collections.reverseOrder());
        return list;
    }

    public PlayerEntity createUser(PlayerEntity newPlayerEntity) {
        newPlayerEntity.setToken(UUID.randomUUID().toString());
        newPlayerEntity.setStatus(PlayerStatus.OFFLINE);
        newPlayerEntity.setScore(0);
>>>>>>> Stashed changes

        checkIfUserExists(newPlayerEntity);

        if (newPlayerEntity.getPassword().equals("")|| newPlayerEntity.getUsername().equals("")) throw new IllegalRegistrationInput("Username and/or password can't consist of an empty string!");

        // saves the given entity but data is only persisted in the database once flush() is called
        newPlayerEntity = playerRepository.save(newPlayerEntity);
        playerRepository.flush();

        log.debug("Created Information for PlayerEntity: {}", newPlayerEntity);
        return newPlayerEntity;
    }

    public PlayerEntity loginUser(PlayerEntity potPlayerEntity){
        PlayerEntity playerEntity = playerRepository.findByUsername(potPlayerEntity.getUsername());
        if (playerEntity ==null) throw new PlayerNotAvailable(String.format("No playerEntity with this username exists."));
        else if (playerEntity.getPassword().equals(potPlayerEntity.getPassword())) {
            if (playerEntity.getStatus().equals(PlayerStatus.OFFLINE)) {
                playerEntity.setStatus(PlayerStatus.ONLINE);
                return playerEntity;
            }
            else throw new PlayerAlreadyLoggedIn();
        }
        else throw new PlayerCredentialsWrong(String.format("Incorrect password."));
    }

    public void logOutUser(PlayerEntity playerEntityInput){
        PlayerEntity playerEntity = playerRepository.findByToken(playerEntityInput.getToken());
        if (playerEntity ==null) throw new PlayerNotAvailable("No playerEntity with same token as your session exists.");
        else if (playerEntity.getStatus().equals(PlayerStatus.ONLINE)) {
            playerEntity.setStatus(PlayerStatus.OFFLINE);
        }
        else throw new PlayerAlreadyLoggedOut();
    }

    public PlayerEntity getUser (PlayerEntity playerEntityInput){
        Optional<PlayerEntity> userOp =this.playerRepository.findById(playerEntityInput.getId());
        if (userOp.isEmpty()) throw new PlayerNotAvailable("No user with this id exists, that can be fetched.");
        return userOp.get();

    }

    public void updateUser (PlayerEntity playerEntity, String userId){
        Optional<PlayerEntity> userOp =this.playerRepository.findById(Long.parseLong(userId));
        if (userOp.isEmpty()) throw new PlayerNotAvailable("No playerEntity with specified ID exists.");
        else if (userOp.get().getToken().equals(playerEntity.getToken())) {
            if (playerEntity.getUsername()!=null) {
                if (playerEntity.getUsername().equals(userOp.get().getUsername()));
                else if (this.playerRepository.findByUsername(playerEntity.getUsername())!=null) throw new UsernameAlreadyExists("Username is already in use!");
                else userOp.get().setUsername(playerEntity.getUsername());
            }
        }
        else throw new PlayerCredentialsWrong("You are not authorized to change this playerEntity, since tokens do not match.");
    }



    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the PlayerEntity entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param playerEntityToBeCreated
     * @throws UsernameAlreadyExists
     * @see PlayerEntity
     */
    private void checkIfUserExists(PlayerEntity playerEntityToBeCreated) {
        PlayerEntity playerEntityByUsername = playerRepository.findByUsername(playerEntityToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (playerEntityByUsername != null) {
            throw new UsernameAlreadyExists(String.format(baseErrorMessage, "username", "is"));
        }

    }
}
