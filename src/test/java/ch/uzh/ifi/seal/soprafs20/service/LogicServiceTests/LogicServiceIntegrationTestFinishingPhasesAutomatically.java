package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.service.*;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceIntegrationTestFinishingPhasesAutomatically extends TestSETUPCreatesActiveGame {

    @SpyBean
    LogicService logicService2;
    @Qualifier("cardRepository")
    @Autowired
    CardRepository cardRepository;
    @Autowired
    public LogicServiceIntegrationTestFinishingPhasesAutomatically(@Qualifier("cardService") CardService cardService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService, @Qualifier("logicService") LogicService logicService, @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, @Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("activeGameService") ActiveGameService activeGameService, LSStateChooseMysteryWord lsStateChooseMysteryWord, LSSGiveClues lssGiveClues, LSSGiveGuess lssGiveGuess, LSSWordReveal lssWordReveal, LSSGameHasEnded lssGameHasEnded) {
        super(cardService, logicService, gameSetUpService, playerService, playerRepository, activeGameService, gameRepository, gameSetUpRepository, lsStateChooseMysteryWord, lssGiveClues, lssGiveGuess, lssWordReveal, lssGameHasEnded);
    }

    /**ChooseMysteryWord does not finish automatically when timer has not finished yet*/
    @Test
    public void ChooseMysteryWordFinishesAutomaticallyFailsBecauseTimeNotEndedYet() {
        //Preparations
        createdActiveGame.setStateForLogicService(State.WordReveal);
        logicService.initializeTurn(createdActiveGame.getId());
        createdActiveGame.setTimeStart(0L);

        //Mock the current time of the System -> Uses logicService2 to spy
        Mockito.doReturn(30000L).when(logicService2).getSystemCurrentMillis();

        //Perform the phase switch
        logicService2.checkThatPhaseHasNotEndedYet(createdActiveGame.getId());

        //Check that a MysteryWord has NOT been chosen automatically
        assertEquals(State.ChooseMysteryWord, createdActiveGame.getStateForLogicService());
        assertTrue(createdActiveGame.getActiveMysteryWord().isBlank());
    }

    /**Give Clue can finish automatically*/
    @Test
    public void GiveClueFinishesAutomatically() {
        //Preparations
        createdActiveGame.setStateForLogicService(State.WordReveal);
        logicService.initializeTurn(createdActiveGame.getId());
        createdActiveGame.setTimeStart(0L);
        createdActiveGame.setActiveMysteryWord("P");
        createdActiveGame.setStateForLogicService(State.GiveClues);
        Map<String, String> clueMap = new HashMap<>();
        clueMap.put(p1.getToken(), "ClueOfP1");
        createdActiveGame.setClueMap(clueMap);

        //Mock the current time of the System -> Uses logicService2 to spy
        Mockito.doReturn(60000L).when(logicService2).getSystemCurrentMillis();

        //Perform the phase switch
        logicService2.checkThatPhaseHasNotEndedYet(createdActiveGame.getId());

        //Check that give Clues has been performed
        assertEquals(2, createdActiveGame.getClueMap().size());
        //Check that the phase has been switched
        assertEquals(State.GiveGuess, createdActiveGame.getStateForLogicService());
        //Check that the timer has been reinitialized
        assertNotEquals(0L, createdActiveGame.getTimeStart());
    }

    /**GiveClue does not finish automatically when timer has not finished yet*/
    @Test
    public void GiveClueFinishesNOTAutomaticallyBecauseTimeNotEndedYet() {
        //Preparations
        createdActiveGame.setStateForLogicService(State.WordReveal);
        logicService.initializeTurn(createdActiveGame.getId());
        createdActiveGame.setTimeStart(0L);
        createdActiveGame.setStateForLogicService(State.GiveClues);

        //Mock the current time of the System -> Uses logicService2 to spy
        Mockito.doReturn(50000L).when(logicService2).getSystemCurrentMillis();

        //Perform the phase switch
        logicService2.checkThatPhaseHasNotEndedYet(createdActiveGame.getId());

        //Check that a MysteryWord has NOT been chosen automatically
        assertEquals(State.GiveClues, createdActiveGame.getStateForLogicService());
        assertEquals(0, createdActiveGame.getClueMap().size());
    }

    /**Give Guess can finish automatically*/
    @Test
    public void GiveGuessFinishesAutomatically() {
        //Preparations
        createdActiveGame.setStateForLogicService(State.WordReveal);
        logicService.initializeTurn(createdActiveGame.getId());
        createdActiveGame.setTimeStart(0L);
        createdActiveGame.setActiveMysteryWord("Test");
        createdActiveGame.setStateForLogicService(State.GiveGuess);
        Map<String, String> clueMap = new HashMap<>();
        clueMap.put(p1.getToken(), "ClueOfP1");
        clueMap.put(p3.getToken(), "ClueOfP2");
        createdActiveGame.setClueMap(clueMap);
        Map<String, Integer> analyzedClues= new HashMap<>();
        analyzedClues.put("ClueOfP1", 0);
        analyzedClues.put("ClueOfP2", 0);
        createdActiveGame.setAnalyzedClues(analyzedClues);
        //Mock the current time of the System -> Uses logicService2 to spy
        Mockito.doReturn(70000L).when(logicService2).getSystemCurrentMillis();

        //Perform the phase switch
        logicService2.checkThatPhaseHasNotEndedYet(createdActiveGame.getId());

        //Check that give Clues has been performed
        assertEquals("invalid_guess", createdActiveGame.getGuess());
        //Check that the phase has been switched
        assertEquals(State.WordReveal, createdActiveGame.getStateForLogicService());
        //Check that the timer has been reinitialized
        assertNotEquals(0L, createdActiveGame.getTimeStart());
    }

    /**GiveGuess does not finish automatically when timer has not finished yet*/
    @Test
    public void GiveGuessFinishesNOTAutomaticallyBecauseTimeNotEndedYet() {
        //Preparations
        createdActiveGame.setStateForLogicService(State.WordReveal);
        logicService.initializeTurn(createdActiveGame.getId());
        createdActiveGame.setTimeStart(0L);
        createdActiveGame.setStateForLogicService(State.GiveGuess);

        //Mock the current time of the System -> Uses logicService2 to spy
        Mockito.doReturn(60000L).when(logicService2).getSystemCurrentMillis();

        //Perform the phase switch
        logicService2.checkThatPhaseHasNotEndedYet(createdActiveGame.getId());

        //Check that a MysteryWord has NOT been chosen automatically
        assertEquals(State.GiveGuess, createdActiveGame.getStateForLogicService());
        assertTrue(createdActiveGame.getGuess().isBlank());
    }

    /**WordReveal can finish automatically*/
    @Test
    public void WordRevealFinishesAutomatically() {
        //Preparations
        createdActiveGame.setStateForLogicService(State.WordReveal);
        logicService.initializeTurn(createdActiveGame.getId());
        createdActiveGame.setTimeStart(0L);
        createdActiveGame.setStateForLogicService(State.WordReveal);

        //Mock the current time of the System -> Uses logicService2 to spy
        Mockito.doReturn(70000L).when(logicService2).getSystemCurrentMillis();

        //Perform the phase switch
        logicService2.checkThatPhaseHasNotEndedYet(createdActiveGame.getId());

        //Check that the phase has been switched
        assertEquals(State.ChooseMysteryWord, createdActiveGame.getStateForLogicService());
        //Check that the timer has been reinitialized
        assertNotEquals(0L, createdActiveGame.getTimeStart());
    }

    /**WordReveal does not finish automatically when timer has not finished yet*/
    @Test
    public void WordRevealFinishesNOTAutomaticallyBecauseTimeNotEndedYet() {
        //Preparations
        createdActiveGame.setStateForLogicService(State.WordReveal);
        logicService.initializeTurn(createdActiveGame.getId());
        createdActiveGame.setTimeStart(0L);
        createdActiveGame.setStateForLogicService(State.WordReveal);

        //Mock the current time of the System -> Uses logicService2 to spy
        Mockito.doReturn(10000L).when(logicService2).getSystemCurrentMillis();

        //Perform the phase switch
        logicService2.checkThatPhaseHasNotEndedYet(createdActiveGame.getId());

        //Check that a MysteryWord has NOT been chosen automatically
        assertEquals(State.WordReveal, createdActiveGame.getStateForLogicService());
    }

}
