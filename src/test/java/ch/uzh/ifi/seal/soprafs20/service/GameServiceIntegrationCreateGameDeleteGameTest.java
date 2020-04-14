package ch.uzh.ifi.seal.soprafs20.service;


import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.GameType;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
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
public class GameServiceIntegrationCreateGameDeleteGameTest {

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
        game.setHostName("Peter");
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
        assertEquals(newGame.getHostName(), game.getHostName());

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
        assertEquals(newGame.getHostName(), game.getHostName());

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
        assertEquals(newGame.getHostName(), game.getHostName());

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
        assertEquals(newGame.getHostName(), game.getHostName());
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
        assertEquals(newGame.getHostName(), game.getHostName());

    }

    /**Empty Name Should Fail*/
    @Test
    public void gameCreationWithInvalidName() {
        game.setGameName("");
        assertThrows(ConflictException.class, () -> gameService.createGame(game));

    }

    /**Delete a game Successfully*/
    @Test
    public void gameDeletionValid() {
        PlayerEntity player = new PlayerEntity();
        player.setUsername("Peter");

        //Create Game first
        GameSetUpEntity newGame = gameService.createGame(game);
        Boolean worked = gameService.deleteGameSetUpEntity(newGame.getId(), player);
        assertTrue(worked);
        assertNull(gameSetUpRepository.findByGameName("GAME1"));
    }

    /**HostName is wrong*/
    @Test
    public void gameDeletionHostInvalid() {
        PlayerEntity player = new PlayerEntity();
        player.setUsername("Petra");

        //Create Game first
        GameSetUpEntity newGame = gameService.createGame(game);
        assertThrows(UnauthorizedException.class, () ->  gameService.deleteGameSetUpEntity(newGame.getId(), player));

    }

    /**GameId does not exist*/
    @Test
    public void gameNotFoundForDeletion() {
        PlayerEntity player = new PlayerEntity();
        player.setUsername("Petra");

        //Create Game first
        GameSetUpEntity newGame = gameService.createGame(game);
        assertThrows(NotFoundException.class, () ->  gameService.deleteGameSetUpEntity(18732L, player));

    }

}