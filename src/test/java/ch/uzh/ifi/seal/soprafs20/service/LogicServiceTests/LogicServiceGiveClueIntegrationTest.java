package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.service.LogicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceGiveClueIntegrationTest extends TestSETUPLogicService {

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
