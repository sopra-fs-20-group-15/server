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


import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceGiveClueIntegrationTest {

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
    private GameEntity  createdActiveGame;

    private PlayerEntity p1;
    private PlayerEntity p2;
    private PlayerEntity p3;

    @BeforeTransaction
    public void clean(){
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

        PlayerEntity playerOne= new PlayerEntity();
        PlayerEntity playerTwo= new PlayerEntity();
        PlayerEntity playerThree= new PlayerEntity();

        playerOne.setUsername("OneName");
        playerOne.setPassword("One");
        playerOne.setToken("One");
        playerOne.setStatus(PlayerStatus.ONLINE);
        p1=playerRepository.save(playerOne);

        playerTwo.setUsername("TwoName");
        playerTwo.setPassword("Two");
        playerTwo.setToken("Two");
        playerTwo.setStatus(PlayerStatus.ONLINE);
        p2=playerRepository.save(playerTwo);

        playerThree.setUsername("ThreeName");
        playerThree.setToken("Three");
        playerThree.setPassword("Three");
        playerThree.setStatus(PlayerStatus.ONLINE);
        p3=playerRepository.save(playerThree);

        List<String> playerTokens=new ArrayList<>();
        playerTokens.add("One");
        playerTokens.add("Two");
        playerTokens.add("Three");


        game.setPlayerTokens(playerTokens);

        game.setHostName(p1.getUsername());
        game.setGameName("GameName");

        createdGame =gameSetUpService.createGame(game);

        createdActiveGame =gameService.getGameById(gameService.createActiveGame(createdGame.getId(), "One").getId());
        createdActiveGame.setActiveMysteryWord("RandomMysteryWord");
        createdActiveGame.setTimeStart(123L);
    }

    @Test
    public void passivePlayerGivesClue() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        p2.setTimePassed(123L);
        logicService.giveClue(createdActiveGame, cluePostDTO);
        assertNotEquals(123L,p2.getTimePassed());
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Clue");
        assertTrue(createdActiveGame.getValidClues().isEmpty());
        assertFalse(createdActiveGame.getValidCluesAreSet());
    }

    @Test
    public void passivePlayerGivesClueTwice() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        logicService.giveClue(createdActiveGame, cluePostDTO);
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Clue");
        assertThrows(UnauthorizedException.class, () -> {logicService.giveClue(createdActiveGame, cluePostDTO);});
    }

    @Test
    public void allPassivePlayerGiveClues() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        assertTrue(createdActiveGame.getValidCluesAreSet());
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Clue");
        assertTrue(createdActiveGame.getClueMap().containsKey("Three"));
        assertEquals(createdActiveGame.getClueMap().get("Three"), "Table");
        assertFalse(createdActiveGame.getValidClues().isEmpty());
        for (String key :createdActiveGame.getValidClues().keySet()) System.out.println(key);
        assertTrue(createdActiveGame.getValidClues().containsValue("Clue") &&
                createdActiveGame.getValidClues().containsValue("Table"));
    }

    @Test
    public void cluesTooCloseToMysteryWordGetThrownOut() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Testing");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        assertTrue(createdActiveGame.getValidCluesAreSet());
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Testing");
        assertTrue(createdActiveGame.getClueMap().containsKey("Three"));
        assertEquals(createdActiveGame.getClueMap().get("Three"), "Table");
        assertFalse(createdActiveGame.getValidClues().isEmpty());
        assertTrue(!createdActiveGame.getValidClues().containsValue("Testing") &&
                createdActiveGame.getValidClues().containsValue("Table"));
    }

    @Test
    public void identicalCluesGetThrownOut() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Table");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("table");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        assertTrue(createdActiveGame.getValidCluesAreSet());
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Table");
        assertTrue(createdActiveGame.getClueMap().containsKey("Three"));
        assertEquals(createdActiveGame.getClueMap().get("Three"), "table");
        assertTrue(createdActiveGame.getValidClues().isEmpty());
        assertTrue(!createdActiveGame.getValidClues().containsValue("Table")
                && !createdActiveGame.getValidClues().containsValue("table"));
    }

    @Test
    public void cluesWithSameStemGetThrownOut() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Tower");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("towering");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        assertTrue(createdActiveGame.getValidCluesAreSet());
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Tower");
        assertTrue(createdActiveGame.getClueMap().containsKey("Three"));
        assertEquals(createdActiveGame.getClueMap().get("Three"), "towering");
        assertTrue(createdActiveGame.getValidClues().isEmpty());
        assertTrue(!createdActiveGame.getValidClues().containsValue("Tower")
                && !createdActiveGame.getValidClues().containsValue("towering"));
    }
}
