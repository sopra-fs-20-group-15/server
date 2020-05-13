package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.service.*;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceGiveClueIntegrationTest extends TestSETUPCreatesActiveGame {

    @Autowired
    public LogicServiceGiveClueIntegrationTest(@Qualifier("cardService") CardService cardService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService, @Qualifier("logicService") LogicService logicService, @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, @Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("activeGameService") ActiveGameService activeGameService, LSStateChooseMysteryWord lsStateChooseMysteryWord, LSSGiveClues lssGiveClues, LSSGiveGuess lssGiveGuess, LSSWordReveal lssWordReveal, LSSGameHasEnded lssGameHasEnded) {
        super(cardService, logicService, gameSetUpService, playerService, playerRepository, activeGameService, gameRepository, gameSetUpRepository,lsStateChooseMysteryWord, lssGiveClues, lssGiveGuess, lssWordReveal, lssGameHasEnded);
    }

    @Autowired
    private GameSetUpRepository gameSetUpRepository;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private LogicService logicService;

    @BeforeTransaction
    public void clean(){
        gameSetUpRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    public void passivePlayerGivesClue() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        p2.setTimePassed(123L);
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);
        assertNotEquals(123L,p2.getTimePassed());
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Clue");
        assertTrue(createdActiveGame.getValidClues().isEmpty());
    }

    @Test
    public void passivePlayerGivesClueTwice() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Clue");
        assertThrows(UnauthorizedException.class, () -> {logicService.giveClue(createdActiveGame.getId(), cluePostDTO);});
    }

    @Test
    public void allPassivePlayerGiveClues() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);

        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Clue");
        assertTrue(createdActiveGame.getClueMap().containsKey("Three"));
        assertEquals(createdActiveGame.getClueMap().get("Three"), "Table");
        assertFalse(createdActiveGame.getValidClues().isEmpty());
        assertTrue(createdActiveGame.getValidClues().containsValue("Clue") &&
                createdActiveGame.getValidClues().containsValue("Table"));
    }

    @Test
    public void allPassivePlayerGiveCluesWithBots() {
        createdActiveGame.setActiveMysteryWord("Test");
        Devil devil=new Devil();
        devil.setToken("d");
        devil.setName("devil");
        List<Devil> listd=new ArrayList<>();
        listd.add(devil);
        createdActiveGame.setDevils(listd);
        Angel angel=new Angel();
        angel.setToken("a");
        angel.setName("angel");
        List<Angel> lista=new ArrayList<>();
        lista.add(angel);
        createdActiveGame.setAngels(lista);

        Map<String, String> clueMap=createdActiveGame.getClueMap();
        clueMap.put("a","Microphone");
        clueMap.put("d","Stone");

        CluePostDTO cluePostDTO=new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);

        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Clue");
        assertTrue(createdActiveGame.getClueMap().containsKey("Three"));
        assertEquals(createdActiveGame.getClueMap().get("Three"), "Table");
        assertFalse(createdActiveGame.getValidClues().isEmpty());
        assertTrue(createdActiveGame.getValidClues().containsValue("Clue") &&
                createdActiveGame.getValidClues().containsValue("Table") &&
                createdActiveGame.getValidClues().containsValue("Stone") &&
                createdActiveGame.getValidClues().containsValue("Microphone"));
    }

    @Test
    public void cluesTooCloseToMysteryWordGetThrownOut() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Testing");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);

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
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("table");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);

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
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("towering");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);

        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Tower");
        assertTrue(createdActiveGame.getClueMap().containsKey("Three"));
        assertEquals(createdActiveGame.getClueMap().get("Three"), "towering");
        assertTrue(createdActiveGame.getValidClues().isEmpty());
        assertTrue(!createdActiveGame.getValidClues().containsValue("Tower")
                && !createdActiveGame.getValidClues().containsValue("towering"));
    }
    /**Should only work in correct phase*/

    @Test
    public void giveClueDoesNotWorkInChooseMysteryWord() {
        createdActiveGame.setStateForLogicService(State.ChooseMysteryWord);

        assertThrows(NoContentException.class, () -> {logicService.giveClue(createdActiveGame.getId(), new CluePostDTO());});
    }

    @Test
    public void giveClueDoesNotWorkInGiveGuess() {
        createdActiveGame.setStateForLogicService(State.GiveGuess);

        assertThrows(NoContentException.class, () -> {logicService.giveClue(createdActiveGame.getId(), new CluePostDTO());});
    }

    @Test
    public void giveClueDoesNotWorkInWordReveal() {
        createdActiveGame.setStateForLogicService(State.WordReveal);

        assertThrows(NoContentException.class, () -> {logicService.giveClue(createdActiveGame.getId(), new CluePostDTO());});
    }

    @Test
    public void giveClueDoesNotWorkInHasEnded() {
        createdActiveGame.setStateForLogicService(State.hasEnded);

        assertThrows(NoContentException.class, () -> {logicService.giveClue(createdActiveGame.getId(), new CluePostDTO());});
    }
}
