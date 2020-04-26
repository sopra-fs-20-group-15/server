package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceIntegrationTestGetClues extends TestSETUPCreatesActiveGame {

    @BeforeEach
    public void setup2() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        createdActiveGame.setTimeStart(123L);
        logicService.giveClue(createdActiveGame, cluePostDTO);
    }

    @Test
    public void cluesAreSetAndGetClueWorksWithOnlyHumanPlayers() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        assertFalse(createdActiveGame.getValidClues().isEmpty());
        List<ClueGetDTO> listOfClues = logicService.getClues(createdActiveGame);

        assertEquals(2, listOfClues.size());

        assertEquals("Clue", listOfClues.get(0).getClue());
        assertEquals(p2.getUsername(), listOfClues.get(0).getPlayerName());

        assertEquals("Table", listOfClues.get(1).getClue());
        assertEquals(p3.getUsername(), listOfClues.get(1).getPlayerName());
    }

    @Test
    public void cluesAreSetAndGetClueWorksWithBots() {
        createdActiveGame.setActiveMysteryWord("Test");
//      add bots
        Angel angel=new Angel();
        angel.setName("Angelo_Merte");
        angel.setToken("Merte");

        List<Angel> angels=new ArrayList<>();
        angels.add(angel);

        createdActiveGame.setAngels(angels);

        Map<String, String> map=createdActiveGame.getClueMap();
        map.put(angel.getToken(),"Angel");
        createdActiveGame.setClueMap(map);

        Devil devil=new Devil();
        devil.setName("Lucius");
        devil.setToken("Malfoy");

        List<Devil> devils=new ArrayList<>();
        devils.add(devil);

        createdActiveGame.setDevils(devils);

        map=createdActiveGame.getClueMap();
        map.put(devil.getToken(),"Devil");
        createdActiveGame.setClueMap(map);

        CluePostDTO cluePostDTO = new CluePostDTO();

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        assertFalse(createdActiveGame.getValidClues().isEmpty());
        List<ClueGetDTO> listOfClues = logicService.getClues(createdActiveGame);

        assertEquals(4, listOfClues.size());

        assertEquals("Devil", listOfClues.get(0).getClue());
        assertEquals(devil.getName(), listOfClues.get(0).getPlayerName());

        assertEquals("Clue", listOfClues.get(1).getClue());
        assertEquals(p2.getUsername(), listOfClues.get(1).getPlayerName());

        assertEquals("Angel", listOfClues.get(3).getClue());
        assertEquals(angel.getName(), listOfClues.get(3).getPlayerName());

        assertEquals("Table", listOfClues.get(2).getClue());
        assertEquals(p3.getUsername(), listOfClues.get(2).getPlayerName());
    }

    @Test
    public void validCluesHaveNotBeenSetYet() {
        assertThrows(NoContentException.class, () -> {logicService.getClues(createdActiveGame); });
    }

}