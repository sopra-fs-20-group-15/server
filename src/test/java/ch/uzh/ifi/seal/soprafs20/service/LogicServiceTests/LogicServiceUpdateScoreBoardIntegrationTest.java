package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.service.ActiveGameService;
import ch.uzh.ifi.seal.soprafs20.service.GameSetUpService;
import ch.uzh.ifi.seal.soprafs20.service.LogicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


import java.util.*;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceUpdateScoreBoardIntegrationTest {

    @Autowired
    private GameSetUpRepository gameSetUpRepository;

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ActiveGameService gameService;

    @Autowired
    private GameSetUpService gameSetUpService;

    @Autowired
    private LogicService logicService;

    private GameSetUpEntity game = new GameSetUpEntity();

    private GameSetUpEntity createdGame;
    private GameEntity createdActiveGame;

    private PlayerEntity p1;
    private PlayerEntity p2;
    private PlayerEntity p3;

    @BeforeTransaction
    public void clean() {
        gameSetUpRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @BeforeEach
    public void setup() {
        game.setNumberOfPlayers(3L);
        game.setNumberOfAngles(0L);
        game.setNumberOfDevils(0L);
        game.setGameType(PRIVATE);
        game.setPassword("Cara");
        PlayerEntity playerOne = new PlayerEntity();
        PlayerEntity playerTwo = new PlayerEntity();
        PlayerEntity playerThree = new PlayerEntity();

        playerOne.setUsername("OneName");
        playerOne.setPassword("One");
        playerOne.setToken("One");
        playerOne.setStatus(PlayerStatus.ONLINE);
        p1 = playerRepository.save(playerOne);

        playerTwo.setUsername("TwoName");
        playerTwo.setPassword("Two");
        playerTwo.setToken("Two");
        playerTwo.setStatus(PlayerStatus.ONLINE);
        p2 = playerRepository.save(playerTwo);

        playerThree.setUsername("ThreeName");
        playerThree.setToken("Three");
        playerThree.setPassword("Three");
        playerThree.setStatus(PlayerStatus.ONLINE);
        p3 = playerRepository.save(playerThree);

        List<String> playerTokens = new ArrayList<>();
        playerTokens.add("One");
        playerTokens.add("Two");
        playerTokens.add("Three");


        game.setPlayerTokens(playerTokens);

        game.setHostName(p1.getUsername());
        game.setGameName("GameName");

        createdGame =gameSetUpService.createGame(game);

        createdActiveGame = gameService.getGameById(gameService.createActiveGame(createdGame.getId(), "One").getId());
        createdActiveGame.setActiveMysteryWord("Shoe");
        createdActiveGame.setTimeStart(0L);

        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Gremlin");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Nutcracker");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        createdActiveGame.setGuess("Shoe");
        createdActiveGame.setIsValidGuess(true);
    }

    @Test
    public void updateScoreWorksWithValidGuess() {
        p1.setTimePassed(5000L);
        p2.setTimePassed(5000L);
        p3.setTimePassed(5000L);

        Map scoreBefore= Map.copyOf(createdActiveGame.getScoreboard().getScore());
        Map scoreBoard=createdActiveGame.getScoreboard().getScore();

        createdActiveGame.updateScoreboard();

        assertNotEquals(scoreBefore.get(p1.getUsername()),scoreBoard.get(p1.getUsername()));
        assertNotEquals(scoreBefore.get(p2.getUsername()),scoreBoard.get(p2.getUsername()));
        assertNotEquals(scoreBefore.get(p3.getUsername()),scoreBoard.get(p3.getUsername()));
        assertEquals(25, scoreBoard.get(p1.getUsername()));
        assertEquals(20, scoreBoard.get(p2.getUsername()));
        assertEquals(20, scoreBoard.get(p2.getUsername()));
    }

    @Test
    public void updateScoreWorksWithValidGuessButDuplicatesClues() {
        p1.setTimePassed(5000L);
        p2.setTimePassed(5000L);
        p3.setTimePassed(5000L);

        Map<String, String> clueMap=new HashMap<>();
        clueMap.put("Two", "test");
        clueMap.put("Three", "test");

        Map<String, Integer> analyzedMap=new HashMap<>();
        analyzedMap.put("test", 1);


        createdActiveGame.setClueMap(clueMap);
        createdActiveGame.setValidClues(new HashMap<>());
        createdActiveGame.setAnalyzedClues(analyzedMap);


        Map scoreBefore= Map.copyOf(createdActiveGame.getScoreboard().getScore());
        Map scoreBoard=createdActiveGame.getScoreboard().getScore();

        createdActiveGame.updateScoreboard();

        assertNotEquals(scoreBefore.get(p1.getUsername()),scoreBoard.get(p1.getUsername()));
        assertNotEquals(scoreBefore.get(p2.getUsername()),scoreBoard.get(p2.getUsername()));
        assertNotEquals(scoreBefore.get(p3.getUsername()),scoreBoard.get(p3.getUsername()));
        assertEquals(25, scoreBoard.get(p1.getUsername()));
        assertEquals(10, scoreBoard.get(p2.getUsername()));
        assertEquals(10, scoreBoard.get(p2.getUsername()));
    }

    @Test
    public void updateScoreWorksWithInvalidGuessButDuplicatesClues() {
        p1.setTimePassed(5000L);
        p2.setTimePassed(5000L);
        p3.setTimePassed(5000L);

        Map<String, String> clueMap=new HashMap<>();
        clueMap.put("Two", "test");
        clueMap.put("Three", "test");

        Map<String, Integer> analyzedMap=new HashMap<>();
        analyzedMap.put("test", 1);


        createdActiveGame.setClueMap(clueMap);
        createdActiveGame.setValidClues(new HashMap<>());
        createdActiveGame.setAnalyzedClues(analyzedMap);


        Map scoreBefore= Map.copyOf(createdActiveGame.getScoreboard().getScore());
        Map scoreBoard=createdActiveGame.getScoreboard().getScore();

        createdActiveGame.setIsValidGuess(false);

        createdActiveGame.updateScoreboard();

        assertEquals(scoreBefore.get(p1.getUsername()),scoreBoard.get(p1.getUsername()));
        assertNotEquals(scoreBefore.get(p2.getUsername()),scoreBoard.get(p2.getUsername()));
        assertNotEquals(scoreBefore.get(p3.getUsername()),scoreBoard.get(p3.getUsername()));
        assertEquals(0, scoreBoard.get(p1.getUsername()));
        assertEquals(-5, scoreBoard.get(p2.getUsername()));
        assertEquals(-5, scoreBoard.get(p2.getUsername()));
    }
}