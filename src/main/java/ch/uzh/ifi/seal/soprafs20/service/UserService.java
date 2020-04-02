package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
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

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.OFFLINE);

        checkIfUserExists(newUser);

        if (newUser.getPassword().equals("")||newUser.getUsername().equals("")) throw new IllegalRegistrationInput("Username and/or password can't consist of an empty string!");

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User loginUser(User potUser){
        User user= userRepository.findByUsername(potUser.getUsername());
        if (user==null) throw new UserNotAvailable(String.format("No user with this username exists."));
        else if (user.getPassword().equals(potUser.getPassword())) {
            if (user.getStatus().equals(UserStatus.OFFLINE)) {
                user.setStatus(UserStatus.ONLINE);
                return user;
            }
            else throw new UserAlreadyLoggedIn();
        }
        else throw new UserCredentialsWrong(String.format("Incorrect password."));
    }

    public void logOutUser(User userInput){
        User user= userRepository.findByToken(userInput.getToken());
        if (user==null) throw new UserNotAvailable("No user with same token as your session exists.");
        else if (user.getStatus().equals(UserStatus.ONLINE)) {
            user.setStatus(UserStatus.OFFLINE);
        }
        else throw new UserAlreadyLoggedOut();
    }

    public User getUser (User userInput){
        Optional<User> userOp =this.userRepository.findById(userInput.getId());
        if (userOp.isEmpty()) throw new UserNotAvailable("No user with this id exists, that can be fetched.");
        return userOp.get();

    }

    public void updateUser (User user, String userId){
        Optional<User> userOp =this.userRepository.findById(Long.parseLong(userId));
        if (userOp.isEmpty()) throw new UserNotAvailable("No user with specified ID exists.");
        else if (userOp.get().getToken().equals(user.getToken())) {
            if (user.getUsername()!=null) {
                if (user.getUsername().equals(userOp.get().getUsername()));
                else if (this.userRepository.findByUsername(user.getUsername())!=null) throw new UsernameAlreadyExists("Username is already in use!");
                else userOp.get().setUsername(user.getUsername());
            }
        }
        else throw new UserCredentialsWrong("You are not authorized to change this user, since tokens do not match.");
    }



    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws UsernameAlreadyExists
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new UsernameAlreadyExists(String.format(baseErrorMessage, "username", "is"));
        }

    }
}
