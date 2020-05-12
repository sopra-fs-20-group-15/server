package ch.uzh.ifi.seal.soprafs20.service.ActiveGameServiceTest;

import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationDeleteActiveGameTest extends TestSETUPCreatesActiveGame {

    @Autowired
    public GameServiceIntegrationDeleteActiveGameTest(@Qualifier("cardService") CardService cardService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService, @Qualifier("logicService") LogicService logicService, @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, @Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("activeGameService") ActiveGameService activeGameService, LSStateChooseMysteryWord lsStateChooseMysteryWord, LSSGiveClues lssGiveClues, LSSGiveGuess lssGiveGuess, LSSWordReveal lssWordReveal, LSSGameHasEnded lssGameHasEnded) {
        super(cardService, logicService, gameSetUpService, playerService, playerRepository, activeGameService, gameRepository, gameSetUpRepository, lsStateChooseMysteryWord, lssGiveClues, lssGiveGuess, lssWordReveal, lssGameHasEnded);
    }

    @Test
    public void SuccessfullyDeletesGame() {
        createdActiveGame.setStateForLogicService(State.hasEnded);

        gameService.deleteActiveGame(createdActiveGame.getId());

        assertThrows(NotFoundException.class, ()-> gameService.getGameById(createdActiveGame.getId()));
    }


    @Test
    public void CannotDeleteSinceGameHasNotEndedYet() {

        assertThrows(ConflictException.class, ()->  gameService.deleteActiveGame(createdActiveGame.getId()));
    }
}
