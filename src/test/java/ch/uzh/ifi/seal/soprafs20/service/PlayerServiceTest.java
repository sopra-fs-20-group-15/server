package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Player;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private Player testPlayer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setName("testName");
        testPlayer.setUsername("testUsername");

        // when -> any object is being save in the userRepository -> return the dummy testPlayer
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testPlayer);
    }

    @Test
    public void createUser_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy testPlayer
        Player createdPlayer = userService.createUser(testPlayer);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testPlayer.getId(), createdPlayer.getId());
        assertEquals(testPlayer.getName(), createdPlayer.getName());
        assertEquals(testPlayer.getUsername(), createdPlayer.getUsername());
        assertNotNull(createdPlayer.getToken());
        assertEquals(UserStatus.OFFLINE, createdPlayer.getStatus());
    }

    @Test
    public void createUser_duplicateName_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testPlayer);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testPlayer));
    }

    @Test
    public void createUser_duplicateInputs_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testPlayer);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testPlayer);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testPlayer);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testPlayer));
    }


}
