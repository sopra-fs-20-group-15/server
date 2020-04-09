package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Player;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
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
 * @see PlayerService
 */
/**
@WebAppConfiguration
@SpringBootTest
public class PlayerServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerService playerService;

    @BeforeEach
    public void setup() {
        playerRepository.deleteAll();
    }

    @Test
    public void createUser_validInputs_success() {
        // given
        assertNull(playerRepository.findByUsername("testUsername"));

        Player testPlayer = new Player();
        testPlayer.setName("testName");
        testPlayer.setUsername("testUsername");

        // when
        Player createdPlayer = playerService.createUser(testPlayer);

        // then
        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getName(), createdPlayer.getName());
        assertEquals(testPlayer.getUsername(), createdPlayer.getUsername());
        assertNotNull(createdPlayer.getToken());
        assertEquals(PlayerStatus.OFFLINE, createdPlayer.getStatus());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        assertNull(playerRepository.findByUsername("testUsername"));

        Player testPlayer = new Player();
        testPlayer.setName("testName");
        testPlayer.setUsername("testUsername");
        Player createdPlayer = playerService.createUser(testPlayer);

        // attempt to create second user with same username
        Player testPlayer2 = new Player();

        // change the name but forget about the username
        testPlayer2.setName("testName2");
        testPlayer2.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> playerService.createUser(testPlayer2));
    }
}*/
