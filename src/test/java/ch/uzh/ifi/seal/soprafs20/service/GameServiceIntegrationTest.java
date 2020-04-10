package ch.uzh.ifi.seal.soprafs20.service;


import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.CardService;
import ch.uzh.ifi.seal.soprafs20.GameLogic.GameService;
import ch.uzh.ifi.seal.soprafs20.GameLogic.ValidationService;
import ch.uzh.ifi.seal.soprafs20.constant.GameType;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumber;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumberbetweenOneAndFive;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {

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

        game.setNumberOfPlayers(3L);
        game.setNumberOfBots(0L);
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
        assertEquals(newGame.getNumberOfPlayers(), game.getNumberOfPlayers());
        assertEquals(newGame.getNumberOfBots(), game.getNumberOfBots());
        assertEquals(newGame.getGameType(), game.getGameType());
        assertEquals(newGame.getPassword(), game.getPassword());
        assertEquals(newGame.getHostId(), game.getHostId());

    }

    @Test
    public void gameCreationWithSevenPlayers() {
        game.setNumberOfPlayers(7L);

        GameSetUpEntity newGame = gameService.createGame(game);

        //Test itself
        assertEquals(newGame.getNumberOfPlayers(), game.getNumberOfPlayers());
        assertEquals(newGame.getNumberOfBots(), game.getNumberOfBots());
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
        game.setNumberOfBots(0L);

        GameSetUpEntity newGame = gameService.createGame(game);

        //Test itself
        assertEquals(newGame.getNumberOfPlayers(), game.getNumberOfPlayers());
        assertEquals(newGame.getNumberOfBots(), game.getNumberOfBots());
        assertEquals(newGame.getGameType(), game.getGameType());
        assertEquals(newGame.getPassword(), game.getPassword());
        assertEquals(newGame.getHostId(), game.getHostId());

    }

    @Test
    public void gameCreationWithMaxBots() {
        game.setNumberOfBots(2L);

        GameSetUpEntity newGame = gameService.createGame(game);

        //Test itself
        assertEquals(newGame.getNumberOfPlayers(), game.getNumberOfPlayers());
        assertEquals(newGame.getNumberOfBots(), game.getNumberOfBots());
        assertEquals(newGame.getGameType(), game.getGameType());
        assertEquals(newGame.getPassword(), game.getPassword());
        assertEquals(newGame.getHostId(), game.getHostId());

    }

    /**should fail: not enough or too many players*/

    @Test
    public void gameCreationWithNegativeNumberOfBots() {
        game.setNumberOfBots(-1L);

        //Test itself
        assertThrows(ConflictException.class, () -> gameService.createGame(game));

    }

    @Test
    public void gameCreationWithTooManyBots() {
        game.setNumberOfBots(3L);
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
        assertEquals(newGame.getNumberOfPlayers(), game.getNumberOfPlayers());
        assertEquals(newGame.getNumberOfBots(), game.getNumberOfBots());
        assertEquals(newGame.getGameType(), game.getGameType());
        assertEquals(newGame.getPassword(), game.getPassword());
        assertEquals(newGame.getHostId(), game.getHostId());

    }
}
