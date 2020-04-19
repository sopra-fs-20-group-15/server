package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GuessPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceSetGuessIntegrationTest {

    @Autowired
    private GameSetUpRepository gameSetUpRepository;

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

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

        createdGame = gameService.createGame(game);

        createdActiveGame = gameService.getGameById(gameService.createActiveGame(createdGame.getId(), "One").getId());
        logicService.initializeTurn(createdActiveGame.getId());
        logicService.setMysteryWord(createdActiveGame.getId(),3L);
        createdActiveGame.setActiveMysteryWord("Shoe");

        CluePostDTO cluePostDTO=new CluePostDTO();
        cluePostDTO.setPlayerToken("One");
        cluePostDTO.setClue("Gremlin");
        logicService.giveClue(createdActiveGame,cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Nutcracker");
        logicService.giveClue(createdActiveGame,cluePostDTO);
    }

    @Test
    public void activePlayerGivesCorrectGuess() {
        Map<String, Integer> scoresBefore= Map.copyOf(createdActiveGame.getScoreboard().getScore());
        GuessPostDTO guessPostDTO = new GuessPostDTO();
        guessPostDTO.setPlayerToken("Two");
        guessPostDTO.setGuess("Shoe");
        logicService.setGuess(createdActiveGame, guessPostDTO.getGuess());
        assertEquals(createdActiveGame.getGuess(), "Shoe");
        assertTrue(createdActiveGame.getIsValidGuess());
        for (Map.Entry<String, Integer> entry: scoresBefore.entrySet()) {
            assertNotEquals(entry.getValue(),createdActiveGame.getScoreboard().getScore().get(entry.getKey()));
        }
    }

    @Test
    public void activePlayerGivesIncorrectGuess() {
        GuessPostDTO guessPostDTO = new GuessPostDTO();
        guessPostDTO.setPlayerToken("Two");
        guessPostDTO.setGuess("Tree");
        logicService.setGuess(createdActiveGame, guessPostDTO.getGuess());
        assertEquals(createdActiveGame.getGuess(), "Tree");
        assertFalse(createdActiveGame.getIsValidGuess());
    }
}