package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.service.*;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    public LogicServiceIntegrationTestGetClues(@Qualifier("cardService") CardService cardService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService, @Qualifier("logicService") LogicService logicService, @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, @Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("activeGameService") ActiveGameService activeGameService, LSStateChooseMysteryWord lsStateChooseMysteryWord, LSSGiveClues lssGiveClues, LSSGiveGuess lssGiveGuess, LSSWordReveal lssWordReveal, LSSGameHasEnded lssGameHasEnded) {
        super(cardService, logicService, gameSetUpService, playerService, playerRepository, activeGameService, gameRepository, gameSetUpRepository, lsStateChooseMysteryWord, lssGiveClues, lssGiveGuess, lssWordReveal, lssGameHasEnded);
    }

    /**Extends the setup which has created an active game with three players so that clues are given which then can be retrieved*/
    @BeforeEach
    public void setup2() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        createdActiveGame.setTimeStart(123L);
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);
    }

    @Test
    public void cluesAreSetAndGetClueWorksWithOnlyHumanPlayers() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);

        assertFalse(createdActiveGame.getValidClues().isEmpty());
        List<ClueGetDTO> listOfClues = logicService.getClues(createdActiveGame.getId());

        assertEquals(2, listOfClues.size());

        assertEquals("Clue", listOfClues.get(0).getClue());
        assertEquals(p2.getUsername(), listOfClues.get(0).getPlayerName());

        assertEquals("Table", listOfClues.get(1).getClue());
        assertEquals(p3.getUsername(), listOfClues.get(1).getPlayerName());

        //Check the phase (should be GiveGuess since all clues have been set)
        assertEquals(State.GiveGuess, createdActiveGame.getStateForLogicService());

        //Check if the clues are still accessible in WordReveal
        createdActiveGame.setStateForLogicService(State.WordReveal);
        List<ClueGetDTO> clueGetDTOS = logicService.getClues(createdActiveGame.getId());
        assertEquals("Clue", clueGetDTOS.get(0).getClue());
        assertEquals(p2.getUsername(), clueGetDTOS.get(0).getPlayerName());
        assertEquals("Table", clueGetDTOS.get(1).getClue());
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
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);

        assertFalse(createdActiveGame.getValidClues().isEmpty());
        List<ClueGetDTO> listOfClues = logicService.getClues(createdActiveGame.getId());

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


    /**During ChooseMysteryWord*/
    @Test
    public void validCluesHaveNotBeenSetYetChooseMysteryWord() {
        createdActiveGame.setStateForLogicService(State.ChooseMysteryWord);
        assertThrows(NoContentException.class, () -> {logicService.getClues(createdActiveGame.getId()); });
    }

    /**During GiveClues*/
    @Test
    public void validCluesHaveNotBeenSetYet() {
        assertThrows(NoContentException.class, () -> {logicService.getClues(createdActiveGame.getId()); });
    }

    /**During GameHasEnded*/
    @Test
    public void gameHasEnded() {
        createdActiveGame.setStateForLogicService(State.hasEnded);
        assertThrows(NoContentException.class, () -> {logicService.getClues(createdActiveGame.getId()); });
    }

}