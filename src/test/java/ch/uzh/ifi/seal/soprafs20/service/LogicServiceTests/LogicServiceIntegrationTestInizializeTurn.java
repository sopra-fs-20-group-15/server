package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.service.State;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceIntegrationTestInizializeTurn extends TestSETUPCreatesActiveGame {

 /**Initialize Turn works*/
    @Test
    public void InitializeTurnWorks() {
        createdActiveGame.setStateForLogicService(State.WordReveal);

        GameEntity initializedGame = logicService.initializeTurn(createdActiveGame.getId());


        //ActiveCard, CardIds, ActivePlayerId and PassivePlayerIds get already tested in LogicServiceTestInitializeTurn by analysing the helper functions
        //Evaluation
        assertEquals(initializedGame.getActiveMysteryWord(), "");
        assertEquals(initializedGame.getRightGuess(), false);
        assertEquals(initializedGame.getClueMap(), new HashMap<String, String>());
        assertEquals(initializedGame.getValidClues(), new HashMap<String, String>());
        assertEquals(initializedGame.getAnalyzedClues(), new HashMap<String, Integer>());
        assertNull(initializedGame.getTimeStart());
        assertEquals(initializedGame.getGuess(), "");
        assertEquals(initializedGame.getIsValidGuess(), false);
    }

    /**Initialize Turn does not work since the game has not ended yet*/
    @Test
    public void InitializeTurnErrorBecauseGameHasAlreadyBeenInitialized() {
        GameEntity initializedGame = logicService.initializeTurn(createdActiveGame.getId());

        assertThrows(NoContentException.class, () -> {logicService.initializeTurn(createdActiveGame.getId());});
    }

    /**Initialize Turn does not work since the game has already been initialized*/
    @Test
    public void InitializeTurnErrorBecauseGameIsInChooseMysteryWord() {
        createdActiveGame.setStateForLogicService(State.ChooseMysteryWord);

        assertThrows(NoContentException.class, () -> {logicService.initializeTurn(createdActiveGame.getId());});
    }

    /**Initialize Turn does not work since the game has already been initialized*/
    @Test
    public void InitializeTurnErrorBecauseGameIsInGiveClues() {
        createdActiveGame.setStateForLogicService(State.GiveClues);

        assertThrows(NoContentException.class, () -> {logicService.initializeTurn(createdActiveGame.getId());});
    }

    /**Initialize Turn does not work since the game has already been initialized*/
    @Test
    public void InitializeTurnErrorBecauseGameIsInGiveGuess() {
        createdActiveGame.setStateForLogicService(State.GiveGuess);

        assertThrows(NoContentException.class, () -> {logicService.initializeTurn(createdActiveGame.getId());});
    }

}
