package ch.uzh.ifi.seal.soprafs20.service.PlayerServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;

import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.CredentialException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@WebAppConfiguration
@SpringBootTest
public class PlayerServiceIntegrationTest {

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    @BeforeTransaction
    public void setup() {
        playerRepository.deleteAll();
    }



    @Test
    public void createPlayer_validInputs_success() {
        // given
        assertNull(playerRepository.findByUsername("testUsername"));

        PlayerEntity testUser = new PlayerEntity();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");

        // when
        PlayerEntity createdUser = playerService.createUser(testUser);

        // then
        assertNotNull(createdUser.getId());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(PlayerStatus.OFFLINE, createdUser.getStatus());
    }

    @Test
    public void createPlayer_duplicateUsername_throwsException() {
        assertNull(playerRepository.findByUsername("testUsername"));

        PlayerEntity testUser = new PlayerEntity();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        PlayerEntity createdUser = playerService.createUser(testUser);

        // attempt to create second user with same username
        PlayerEntity testUser2 = new PlayerEntity();

        // change the name but forget about the username
        testUser2.setUsername("testUsername");
        testUser.setPassword("testPassword");

        // check that an error is thrown
        String exceptionMessage = "The username provided is not unique. Therefore, the user could not be created!";
        UsernameAlreadyExists exception = assertThrows(UsernameAlreadyExists.class, () -> playerService.createUser(testUser2), exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    public void createUser_emptyStrings_throwsException() {
        assertNull(playerRepository.findByUsername("testUsername"));

//        given users with empty strings in username, password or both

        PlayerEntity testUser = new PlayerEntity();
        testUser.setUsername("");
        testUser.setPassword("testPassword");

        PlayerEntity testUser2 = new PlayerEntity();
        testUser2.setUsername("testUsername");
        testUser2.setPassword("");

        PlayerEntity testUser3 = new PlayerEntity();
        testUser3.setUsername("");
        testUser3.setPassword("");


        // check that an error is thrown
        String exceptionMessage = "Username and/or password can't consist of an empty string!";
        IllegalRegistrationInput exception = assertThrows(IllegalRegistrationInput.class, () -> playerService.createUser(testUser), exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
        IllegalRegistrationInput exception2 = assertThrows(IllegalRegistrationInput.class, () -> playerService.createUser(testUser2), exceptionMessage);
        assertEquals(exceptionMessage, exception2.getMessage());
        IllegalRegistrationInput exception3 = assertThrows(IllegalRegistrationInput.class, () -> playerService.createUser(testUser3), exceptionMessage);
        assertEquals(exceptionMessage, exception3.getMessage());
    }

    @Test
    public void login_validCredentials() {
        assertNull(playerRepository.findByUsername("testUsername"));

        PlayerEntity testUser = new PlayerEntity();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        PlayerEntity newUser= playerService.createUser(testUser);

        PlayerEntity loggedInUser= playerService.loginUser(newUser);

//        check if everything has gone right
        assertEquals(loggedInUser.getStatus(),PlayerStatus.ONLINE);
        assertEquals(newUser.getId(), loggedInUser.getId());
        assertEquals(newUser.getUsername(), loggedInUser.getUsername());
        assertEquals(newUser.getPassword(), loggedInUser.getPassword());
        assertEquals(newUser.getToken(), loggedInUser.getToken());
    }

    @Test
    public void login_validCredentials_but_alreadyLoggedIn() {
        assertNull(playerRepository.findByUsername("testUsername"));

        PlayerEntity testUser = new PlayerEntity();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        PlayerEntity newUser= playerService.createUser(testUser);

//        loginUser
        playerService.loginUser(newUser);

        // check that an error is thrown if user is already logged in
        assertThrows(PlayerAlreadyLoggedIn.class, () -> playerService.loginUser(newUser));

    }

    @Test
    public void login_invalidCredentials() {
        assertNull(playerRepository.findByUsername("testUsername"));

        PlayerEntity testUser = new PlayerEntity();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        PlayerEntity newUser= playerService.createUser(testUser);

        PlayerEntity testUser2 = new PlayerEntity();
        testUser2.setUsername("Wrong");
        testUser2.setPassword("testPassword");

        PlayerEntity testUser3 = new PlayerEntity();
        testUser3.setUsername("testUsername");
        testUser3.setPassword("Wrong");


        // check that an error is thrown if wrong username or password is used
        String exceptionMessage = "No playerEntity with this username exists.";
        String exceptionMessage2 = "Incorrect password.";
        PlayerNotAvailable exception = assertThrows(PlayerNotAvailable.class, () -> playerService.loginUser(testUser2), exceptionMessage);
        assertEquals(exceptionMessage, exception.getMessage());
        PlayerCredentialsWrong exception2 = assertThrows(PlayerCredentialsWrong.class, () -> playerService.loginUser(testUser3), exceptionMessage2);
        assertEquals(exceptionMessage2, exception2.getMessage());
    }

    @Test
    public void logout_valid_Token() {
        assertNull(playerRepository.findByUsername("testUsername"));

        PlayerEntity testUser = new PlayerEntity();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        PlayerEntity newUser= playerService.createUser(testUser);

        playerService.loginUser(newUser);
        playerService.logOutUser(newUser);

        assertEquals(newUser.getStatus(), PlayerStatus.OFFLINE);
    }

    @Test
    public void logout_invalid_Token() {
        assertNull(playerRepository.findByUsername("testUsername"));

        PlayerEntity testUser = new PlayerEntity();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        PlayerEntity newUser= playerService.createUser(testUser);
        PlayerEntity testUser2= new PlayerEntity();
        testUser2.setToken("tuktuk");

//    make sure error is thrown because of wrong token
        String exceptionMsg ="No playerEntity with same token as your session exists.";
        PlayerNotAvailable exception = assertThrows(PlayerNotAvailable.class, () -> playerService.logOutUser(testUser2), exceptionMsg);
        assertEquals(exceptionMsg, exception.getMessage());
    }

    @Test
    public void logout_alreadyLoggedOut() {
        assertNull(playerRepository.findByUsername("testUsername"));

//        keep user offline
        PlayerEntity testUser = new PlayerEntity();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        PlayerEntity newUser= playerService.createUser(testUser);

//    make sure error is thrown because user is already logged out

        assertThrows(PlayerAlreadyLoggedOut.class, () -> playerService.logOutUser(testUser));
    }

    @Test
    public void getPlayer_userIdExists() {
        assertNull(playerRepository.findByUsername("testUsername"));

        PlayerEntity testUser = new PlayerEntity();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        PlayerEntity newUser= playerService.createUser(testUser);
        PlayerEntity getUser= playerService.getUser(testUser);


//    make sure everything works
        assertEquals(getUser.getId(), newUser.getId());
        assertEquals(getUser.getUsername(), newUser.getUsername());
        assertEquals(getUser.getPassword(), newUser.getPassword());
        assertEquals(getUser.getToken(), newUser.getToken());
    }

    @Test
    public void getPlayer_userIdDoesNotExists() {
        assertNull(playerRepository.findByUsername("testUsername"));

        PlayerEntity testUser = new PlayerEntity();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        PlayerEntity newUser= playerService.createUser(testUser);
        newUser.setId(111L);


//    make sure exception thrown when id does not exist
        String exceptionMsg= "No user with this id exists, that can be fetched.";
        PlayerNotAvailable exception= assertThrows(PlayerNotAvailable.class, () -> playerService.getUser(newUser));
        assertEquals(exception.getMessage(),exceptionMsg);
    }
}
