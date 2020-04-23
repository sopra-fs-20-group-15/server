package ch.uzh.ifi.seal.soprafs20.service.GameServiceTests;


import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;
import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PUBLIC;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;

import ch.uzh.ifi.seal.soprafs20.service.GameSetUpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationPutPlayerIntoGameAndRemovePlayerTest {

    @Qualifier("gameSetUpEntityRepository")
    @Autowired
    private GameSetUpRepository gameSetUpRepository;

    @Autowired
    private GameSetUpService gameService;

    private GameSetUpEntity game = new GameSetUpEntity();
    private GameSetUpEntity createdGame = new GameSetUpEntity();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        gameSetUpRepository.deleteAll();

        game.setGameName("ABC");
        game.setNumberOfPlayers(5L);
        game.setNumberOfAngles(2L);
        game.setNumberOfDevils(0L);
        game.setGameType(PRIVATE);
        game.setPassword("Cara");
        List<String> playerTokens = new ArrayList<String>();
        playerTokens.add("A");
        playerTokens.add("B");
        game.setPlayerTokens(playerTokens);
        //Valid host gets already checked beforehand
        game.setHostName("Peter");
        createdGame = gameService.createGame(game);

    }

    /**Successful Join of Private Game*/
    @Test
    public void JoinPrivateGameSuccessfully() {
        //Player to put into game
        PlayerEntity player = new PlayerEntity();
        player.setToken("C");
        //PlayerTokens List
        List<String> playerTokens = new ArrayList<String>();
        playerTokens.add("A");
        playerTokens.add("B");
        playerTokens.add("C");

        //Method call
        GameSetUpEntity newGame = gameService.putPlayerIntoGame(createdGame.getId(),player,createdGame.getPassword());

        List<String> playerTokensFromGame = new ArrayList<String>();
        playerTokensFromGame = newGame.getPlayerTokens();
        //Test itself
        assertEquals(playerTokensFromGame.get(0), playerTokens.get(0));
        assertEquals(playerTokensFromGame.get(1), playerTokens.get(1));
        assertEquals(playerTokensFromGame.get(2), playerTokens.get(2));


    }
    /**Successful Join of Public Game*/
    @Test
    public void JoinPublicGameSuccessfully() {
        //Adjust game
        game.setGameType(PUBLIC);
        game.setPassword("");
        createdGame = gameService.createGame(game);
        //Player to put into game
        PlayerEntity player = new PlayerEntity();
        player.setToken("C");
        //PlayerTokens List
        List<String> playerTokens = new ArrayList<String>();
        playerTokens.add("A");
        playerTokens.add("B");
        playerTokens.add("C");

        //Method call
        GameSetUpEntity newGame = gameService.putPlayerIntoGame(createdGame.getId(),player,"");

        List<String> playerTokensFromGame = new ArrayList<String>();
        playerTokensFromGame = newGame.getPlayerTokens();
        //Test itself
        assertEquals(playerTokensFromGame.get(0), playerTokens.get(0));
        assertEquals(playerTokensFromGame.get(1), playerTokens.get(1));
        assertEquals(playerTokensFromGame.get(2), playerTokens.get(2));
    }
    /**Player is already in Game -> Nothing happens*/
    @Test
    public void PlayerAlreadyInGame() {
        //Adjust game
        List<String> playerTokens1 = new ArrayList<String>();
        playerTokens1.add("A");
        playerTokens1.add("B");
        playerTokens1.add("C");
        game.setPlayerTokens(playerTokens1);
        createdGame = gameService.createGame(game);
        //Player to put into game
        PlayerEntity player = new PlayerEntity();
        player.setToken("C");


        //Test itself
        assertThrows(NoContentException.class, () ->gameService.putPlayerIntoGame(createdGame.getId(),player,createdGame.getPassword()));

    }
    /**Private Game and The Password is invalid*/
    @Test
    public void InvalidPassword() {
        //Player to put into game
        PlayerEntity player = new PlayerEntity();
        player.setToken("C");


        //Test itself
        assertThrows(UnauthorizedException.class, () ->gameService.putPlayerIntoGame(createdGame.getId(),player,"wrongPassword"));

    }
    
    /**The Game is already full (depends also on number of bots!)*/
    @Test
    public void GameAlreadyFull() {
        //Adjust game -> 3 human players and 2 bots are equal to the max of five players
        List<String> playerTokens1 = new ArrayList<String>();
        playerTokens1.add("A");
        playerTokens1.add("B");
        playerTokens1.add("D");
        game.setPlayerTokens(playerTokens1);
        createdGame = gameService.createGame(game);
        //Player to put into game
        PlayerEntity player = new PlayerEntity();
        player.setToken("C");


        //Test itself
        assertThrows(UnauthorizedException.class, () ->gameService.putPlayerIntoGame(createdGame.getId(),player,createdGame.getPassword()));

    }
    /**The game does not exist*/
    @Test
    public void GameIdDoesNotExistPutPlayerIntoGame() {
        //Player to remove
        PlayerEntity player = new PlayerEntity();
        player.setToken("C");


        //Test itself
        assertThrows(NotFoundException.class, () ->gameService.putPlayerIntoGame(32L, player, "ABC"));
    }

    /**Successful removal of a player*/
    @Test
    public void RemovePlayerSuccessfully() {
        //Player to remove
        PlayerEntity player = new PlayerEntity();
        player.setToken("B");
        //PlayerTokens List
        List<String> playerTokens = new ArrayList<String>();
        playerTokens.add("A");

        //Method call
        GameSetUpEntity newGame = gameService.removePlayerFromGame(createdGame.getId(),player);

        List<String> playerTokensFromGame = new ArrayList<String>();
        playerTokensFromGame = newGame.getPlayerTokens();
        //Test itself
        //Player "A" is still there
        assertEquals(playerTokensFromGame.get(0), playerTokens.get(0));
        //Size of array is correct
        assertEquals(playerTokensFromGame.size(), 1);
    }

    /**The player is not part of said game*/
    @Test
    public void PlayerNotInGame() {
        //Player to remove
        PlayerEntity player = new PlayerEntity();
        player.setToken("C");


        //Test itself
        assertThrows(NotFoundException.class, () ->gameService.removePlayerFromGame(createdGame.getId(),player));
    }

    /**The game does not exist*/
    @Test
    public void GameIdDoesNotExist() {
        //Player to remove
        PlayerEntity player = new PlayerEntity();
        player.setToken("C");


        //Test itself
        assertThrows(NotFoundException.class, () ->gameService.removePlayerFromGame(13L,player));
    }

}
