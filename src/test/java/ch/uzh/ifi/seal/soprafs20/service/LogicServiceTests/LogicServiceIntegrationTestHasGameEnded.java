package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
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

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceIntegrationTestHasGameEnded extends TestSETUPCreatesActiveGame {

    @Autowired
    public LogicServiceIntegrationTestHasGameEnded(@Qualifier("cardService") CardService cardService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService, @Qualifier("logicService") LogicService logicService, @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, @Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("activeGameService") ActiveGameService activeGameService, LSStateChooseMysteryWord lsStateChooseMysteryWord, LSSGiveClues lssGiveClues, LSSGiveGuess lssGiveGuess, LSSWordReveal lssWordReveal, LSSGameHasEnded lssGameHasEnded) {
        super(cardService, logicService, gameSetUpService, playerService, playerRepository, activeGameService, gameRepository, gameSetUpRepository, lsStateChooseMysteryWord, lssGiveClues, lssGiveGuess, lssWordReveal, lssGameHasEnded);
    }


 /**Check a case where the game has not ended yet*/
    @Test
    public void GameHasNotEnded() {
        //c
        Boolean hasEnded = logicService.hasGameEnded(createdActiveGame.getId());

        assertNotEquals(createdActiveGame.getStateForLogicService(), State.hasEnded);

    }

/**Check a case where the game has ended
 * -> Compared to earlier versions, "hasGamEnded" only checks the state of the game, but does not set "hasEnded" anymore -> is in giveGuess now*/
    @Test
    public void GameHasEnded() {

        createdActiveGame.setStateForLogicService(State.hasEnded);
        //c
        Boolean hasEnded = logicService.hasGameEnded(createdActiveGame.getId());

        assertEquals(createdActiveGame.getStateForLogicService(), State.hasEnded);


    }
}
