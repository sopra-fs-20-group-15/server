package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;


@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceUpdateScoreBoardIntegrationTest extends TestSETUPLogicService {

    @BeforeEach
    public void setup2() {
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