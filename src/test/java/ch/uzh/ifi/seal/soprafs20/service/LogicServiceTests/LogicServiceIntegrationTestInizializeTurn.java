package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.service.*;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceIntegrationTestInizializeTurn extends TestSETUPCreatesActiveGame {

    @Autowired
    public LogicServiceIntegrationTestInizializeTurn(@Qualifier("cardService") CardService cardService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService, @Qualifier("logicService") LogicService logicService, @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, @Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("activeGameService") ActiveGameService activeGameService, LSStateChooseMysteryWord lsStateChooseMysteryWord, LSSGiveClues lssGiveClues, LSSGiveGuess lssGiveGuess, LSSWordReveal lssWordReveal, LSSGameHasEnded lssGameHasEnded) {
        super(cardService, logicService, gameSetUpService, playerService, playerRepository, activeGameService, gameRepository, gameSetUpRepository, lsStateChooseMysteryWord, lssGiveClues, lssGiveGuess, lssWordReveal, lssGameHasEnded);
    }

 /**Initialize Turn works*/
    @Test
    public void InitializeTurnWorks() {
        createdActiveGame.setStateForLogicService(State.WordReveal);

        GameEntity initializedGame = logicService.initializeTurn(createdActiveGame.getId());


        //ActiveCard, CardIds, ActivePlayerId and PassivePlayerIds get already tested in LogicServiceTestInitializeTurn by analysing the helper functions
        //Evaluation
        assertEquals(initializedGame.getActiveMysteryWord(), "");
        assertEquals(initializedGame.getClueMap(), new HashMap<String, String>());
        assertEquals(initializedGame.getValidClues(), new HashMap<String, String>());
        assertEquals(initializedGame.getAnalyzedClues(), new HashMap<String, Integer>());
        assertNotNull(initializedGame.getTimeStart());
        assertEquals(initializedGame.getGuess(), "");
        assertFalse(initializedGame.getIsValidGuess());
    }

    /**Initialize Turn does not work since the game has not ended yet*/
    @Test
    public void InitializeTurnErrorBecauseGameHasAlreadyBeenInitialized() {

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

    /**Initialize Turn does not work since the game has ended*/
    @Test
    public void InitializeTurnErrorBecauseGameIsInHasEnded() {
        createdActiveGame.setStateForLogicService(State.hasEnded);

        assertThrows(NoContentException.class, () -> {logicService.initializeTurn(createdActiveGame.getId());});
    }

}
