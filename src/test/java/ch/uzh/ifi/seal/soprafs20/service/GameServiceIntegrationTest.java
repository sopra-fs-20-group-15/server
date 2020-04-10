//package ch.uzh.ifi.seal.soprafs20.service;
//
//
//import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;
//import static org.junit.jupiter.api.Assertions.*;
//
//import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
//import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
//import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
//import ch.uzh.ifi.seal.soprafs20.GameLogic.CardService;
//import ch.uzh.ifi.seal.soprafs20.GameLogic.GameService;
//import ch.uzh.ifi.seal.soprafs20.GameLogic.ValidationService;
//import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumber;
//import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumberbetweenOneAndFive;
//import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
//import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
//import org.junit.jupiter.api.Assertions;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@WebAppConfiguration
//@SpringBootTest
//public class GameServiceIntegrationTest {
//
//    @Qualifier("gameRepository")
//    @Autowired
//    private GameRepository gameRepository;
//
//    @Autowired
//    private GameService gameService;
//
//    private GameEntity game = new GameEntity();
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//        gameRepository.deleteAll();
//
//        game.setNumberOfPlayers(3L);
//        game.setNumberOfBots(0L);
//        game.setGameType(PRIVATE);
//        game.setPassword("Cara");
//        game.setHostId(1L);
//    }
//
//    /**
//     * Test createGame
//     */
//    @Test
//    public void gameCreationWithThreePlayers() {
//        game.setNumberOfPlayers(3L);
//
//        GameEntity newGame = gameService.createGame(game);
//
//        //Test itself
//        assertEquals(newGame.getNumberOfPlayers(), game.getNumberOfPlayers());
//        assertEquals(newGame.getNumberOfBots(), game.getNumberOfBots());
//        assertEquals(newGame.getGameType(), game.getGameType());
//        assertEquals(newGame.getPassword(), game.getPassword());
//        assertEquals(newGame.getHostId(), game.getHostId());
//        assertNotNull(newGame.getId());
//
//    }
//
//}