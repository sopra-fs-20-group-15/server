package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Player;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
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
 * Player Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Player> getUsers() {
        return this.userRepository.findAll();
    }

    public Player createUser(Player newPlayer) {
        newPlayer.setToken(UUID.randomUUID().toString());
        newPlayer.setStatus(UserStatus.OFFLINE);

        checkIfUserExists(newPlayer);

        if (newPlayer.getPassword().equals("")|| newPlayer.getUsername().equals("")) throw new IllegalRegistrationInput("Username and/or password can't consist of an empty string!");

        // saves the given entity but data is only persisted in the database once flush() is called
        newPlayer = userRepository.save(newPlayer);
        userRepository.flush();

        log.debug("Created Information for Player: {}", newPlayer);
        return newPlayer;
    }

    public Player loginUser(Player potPlayer){
        Player player = userRepository.findByUsername(potPlayer.getUsername());
        if (player ==null) throw new UserNotAvailable(String.format("No player with this username exists."));
        else if (player.getPassword().equals(potPlayer.getPassword())) {
            if (player.getStatus().equals(UserStatus.OFFLINE)) {
                player.setStatus(UserStatus.ONLINE);
                return player;
            }
            else throw new UserAlreadyLoggedIn();
        }
        else throw new UserCredentialsWrong(String.format("Incorrect password."));
    }

    public void logOutUser(Player playerInput){
        Player player = userRepository.findByToken(playerInput.getToken());
        if (player ==null) throw new UserNotAvailable("No player with same token as your session exists.");
        else if (player.getStatus().equals(UserStatus.ONLINE)) {
            player.setStatus(UserStatus.OFFLINE);
        }
        else throw new UserAlreadyLoggedOut();
    }

    public Player getUser (Player playerInput){
        Optional<Player> userOp =this.userRepository.findById(playerInput.getId());
        if (userOp.isEmpty()) throw new UserNotAvailable("No user with this id exists, that can be fetched.");
        return userOp.get();

    }

    public void updateUser (Player player, String userId){
        Optional<Player> userOp =this.userRepository.findById(Long.parseLong(userId));
        if (userOp.isEmpty()) throw new UserNotAvailable("No player with specified ID exists.");
        else if (userOp.get().getToken().equals(player.getToken())) {
            if (player.getUsername()!=null) {
                if (player.getUsername().equals(userOp.get().getUsername()));
                else if (this.userRepository.findByUsername(player.getUsername())!=null) throw new UsernameAlreadyExists("Username is already in use!");
                else userOp.get().setUsername(player.getUsername());
            }
        }
        else throw new UserCredentialsWrong("You are not authorized to change this player, since tokens do not match.");
    }



    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the Player entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param playerToBeCreated
     * @throws UsernameAlreadyExists
     * @see Player
     */
    private void checkIfUserExists(Player playerToBeCreated) {
        Player playerByUsername = userRepository.findByUsername(playerToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (playerByUsername != null) {
            throw new UsernameAlreadyExists(String.format(baseErrorMessage, "username", "is"));
        }

    }
}
