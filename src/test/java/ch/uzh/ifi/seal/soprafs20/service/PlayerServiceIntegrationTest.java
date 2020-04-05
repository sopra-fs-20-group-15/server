package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Player;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class PlayerServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void createUser_validInputs_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        Player testPlayer = new Player();
        testPlayer.setName("testName");
        testPlayer.setUsername("testUsername");

        // when
        Player createdPlayer = userService.createUser(testPlayer);

        // then
        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getName(), createdPlayer.getName());
        assertEquals(testPlayer.getUsername(), createdPlayer.getUsername());
        assertNotNull(createdPlayer.getToken());
        assertEquals(UserStatus.OFFLINE, createdPlayer.getStatus());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        assertNull(userRepository.findByUsername("testUsername"));

        Player testPlayer = new Player();
        testPlayer.setName("testName");
        testPlayer.setUsername("testUsername");
        Player createdPlayer = userService.createUser(testPlayer);

        // attempt to create second user with same username
        Player testPlayer2 = new Player();

        // change the name but forget about the username
        testPlayer2.setName("testName2");
        testPlayer2.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testPlayer2));
    }
}
