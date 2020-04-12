package ch.uzh.ifi.seal.soprafs20.service;


import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.constant.GameType;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationCreateGameTest {

    @Qualifier("gameSetUpEntityRepository")
    @Autowired
    private GameSetUpRepository gameSetUpRepository;

    @Autowired
    private GameService gameService;

    private GameSetUpEntity game = new GameSetUpEntity();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        gameSetUpRepository.deleteAll();

        game.setGameName("GAME1");
        game.setNumberOfPlayers(3L);
        game.setNumberOfAngles(1L);
        game.setNumberOfDevils(1L);
        game.setGameType(PRIVATE);
        game.setPassword("Cara");
        //Valid host gets already checked beforehand
        game.setHostId(1L);
    }

    /**
     * Test createGame
     */
    @Test
    public void gameCreationWithThreePlayers() {
        game.setNumberOfPlayers(3L);

        GameSetUpEntity newGame = gameService.createGame(game);

        //Test itself
        assertEquals(newGame.getGameName(), game.getGameName());
        assertEquals(newGame.getNumberOfPlayers(), game.getNumberOfPlayers());
        assertEquals(newGame.getNumberOfAngles(), game.getNumberOfAngles());
        assertEquals(newGame.getNumberOfDevils(), game.getNumberOfDevils());
        assertEquals(newGame.getGameType(), game.getGameType());
        assertEquals(newGame.getPassword(), game.getPassword());
        assertEquals(newGame.getHostId(), game.getHostId());

    }

    @Test
    public void gameCreationWithSevenPlayers() {
        game.setNumberOfPlayers(7L);

        GameSetUpEntity newGame = gameService.createGame(game);

        //Test itself
        assertEquals(newGame.getGameName(), game.getGameName());
        assertEquals(newGame.getNumberOfPlayers(), game.getNumberOfPlayers());
        assertEquals(newGame.getNumberOfAngles(), game.getNumberOfAngles());
        assertEquals(newGame.getNumberOfDevils(), game.getNumberOfDevils());
        assertEquals(newGame.getGameType(), game.getGameType());
        assertEquals(newGame.getPassword(), game.getPassword());
        assertEquals(newGame.getHostId(), game.getHostId());

    }

    /**should fail: not enough or too many players*/

    @Test
    public void gameCreationWithTwoPlayers() {
        game.setNumberOfPlayers(2L);

        //Test itself
        assertThrows(ConflictException.class, () -> gameService.createGame(game));

    }

    @Test
    public void gameCreationWithEightPlayers() {
        game.setNumberOfPlayers(8L);
        assertThrows(ConflictException.class, () -> gameService.createGame(game));

    }

    /**Test BotCreation*/
    @Test
    public void gameCreationWithZeroBots() {
        game.setNumberOfAngles(0L);
        game.setNumberOfDevils(0L);

        GameSetUpEntity newGame = gameService.createGame(game);

        //Test itself
        assertEquals(newGame.getGameName(), game.getGameName());
        assertEquals(newGame.getNumberOfPlayers(), game.getNumberOfPlayers());
        assertEquals(newGame.getNumberOfAngles(), game.getNumberOfAngles());
        assertEquals(newGame.getNumberOfDevils(), game.getNumberOfDevils());
        assertEquals(newGame.getGameType(), game.getGameType());
        assertEquals(newGame.getPassword(), game.getPassword());
        assertEquals(newGame.getHostId(), game.getHostId());

    }

    @Test
    public void gameCreationWithMaxBots() {

        GameSetUpEntity newGame = gameService.createGame(game);

        //Test itself
        assertEquals(newGame.getGameName(), game.getGameName());
        assertEquals(newGame.getNumberOfPlayers(), game.getNumberOfPlayers());
        assertEquals(newGame.getNumberOfAngles(), game.getNumberOfAngles());
        assertEquals(newGame.getNumberOfDevils(), game.getNumberOfDevils());
        assertEquals(newGame.getGameType(), game.getGameType());
        assertEquals(newGame.getPassword(), game.getPassword());
        assertEquals(newGame.getHostId(), game.getHostId());

    }

    /**should fail: not enough or too many players*/

    @Test
    public void gameCreationWithNegativeNumberOfBots() {
        game.setNumberOfAngles(-1L);

        //Test itself
        assertThrows(ConflictException.class, () -> gameService.createGame(game));

    }

    @Test
    public void gameCreationWithTooManyBots() {
        game.setNumberOfDevils(3L);
        assertThrows(ConflictException.class, () -> gameService.createGame(game));

    }
    /**Private without password should fail*/
    @Test
    public void gameCreationWithEmptyPassword() {
        game.setPassword("");
        assertThrows(ConflictException.class, () -> gameService.createGame(game));

    }
    /**Public without password should work*/
    @Test
    public void gameCreationWithEmptyPasswordButPublic() {
        game.setPassword("");
        game.setGameType(GameType.PUBLIC);


        GameSetUpEntity newGame = gameService.createGame(game);

        //Test itself
        assertEquals(newGame.getGameName(), game.getGameName());
        assertEquals(newGame.getNumberOfPlayers(), game.getNumberOfPlayers());
        assertEquals(newGame.getNumberOfAngles(), game.getNumberOfAngles());
        assertEquals(newGame.getNumberOfDevils(), game.getNumberOfDevils());
        assertEquals(newGame.getGameType(), game.getGameType());
        assertEquals(newGame.getPassword(), game.getPassword());
        assertEquals(newGame.getHostId(), game.getHostId());

    }

    /**Empty Name Should Fail*/
    @Test
    public void gameCreationWithInvalidName() {
        game.setGameName("");
        assertThrows(ConflictException.class, () -> gameService.createGame(game));

    }

}