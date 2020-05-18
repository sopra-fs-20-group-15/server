package ch.uzh.ifi.seal.soprafs20.service.ActiveGameServiceTest;

import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.service.*;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.LSSGameHasEnded;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.LSSGiveClues;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.LSSGiveGuess;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.LSSWordReveal;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.LSStateChooseMysteryWord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTestRemovePlayerFromGame extends TestSETUPCreatesActiveGame{

    @Autowired
    public GameServiceIntegrationTestRemovePlayerFromGame(@Qualifier("cardService") CardService cardService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService, @Qualifier("logicService") LogicService logicService, @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, @Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("activeGameService") ActiveGameService activeGameService, LSStateChooseMysteryWord lsStateChooseMysteryWord, LSSGiveClues lssGiveClues, LSSGiveGuess lssGiveGuess, LSSWordReveal lssWordReveal, LSSGameHasEnded lssGameHasEnded) {
        super(cardService, logicService, gameSetUpService, playerService, playerRepository, activeGameService, gameRepository, gameSetUpRepository, lsStateChooseMysteryWord, lssGiveClues, lssGiveGuess, lssWordReveal, lssGameHasEnded);
    }


    @Test
    public void SuccessfullyRemoveOnePlayer() {
        //preparations
        createdActiveGame.setStateForLogicService(State.WordReveal);
        logicService.initializeTurn(createdActiveGame.getId());
        createdActiveGame.setStateForLogicService(State.WordReveal);
        //try to remove the player
        gameService.removePlayerFromGame(createdActiveGame.getId(), p1.getToken());

        assertEquals(2, createdActiveGame.getPlayers().size());
        assertEquals(1, createdActiveGame.getPassivePlayerIds().size());
    }

    @Test
    public void SuccessfullyRemoveMultiplePlayers() {//preparations
        createdActiveGame.setStateForLogicService(State.WordReveal);
        logicService.initializeTurn(createdActiveGame.getId());
        createdActiveGame.setStateForLogicService(State.WordReveal);
        //try to remove the player
        gameService.removePlayerFromGame(createdActiveGame.getId(), p1.getToken());
        gameService.removePlayerFromGame(createdActiveGame.getId(), p3.getToken());

        assertEquals(1, createdActiveGame.getPlayers().size());
        assertEquals(0, createdActiveGame.getPassivePlayerIds().size());

    }

    @Test
    public void SuccessfullyRemoveMultiplePlayersAndDeleteGame() {
        createdActiveGame.setStateForLogicService(State.WordReveal);
        logicService.initializeTurn(createdActiveGame.getId());
        createdActiveGame.setStateForLogicService(State.WordReveal);
        //try to remove the player
        gameService.removePlayerFromGame(createdActiveGame.getId(), p1.getToken());
        gameService.removePlayerFromGame(createdActiveGame.getId(), p2.getToken());
        gameService.removePlayerFromGame(createdActiveGame.getId(), p3.getToken());

        assertThrows(NotFoundException.class, ()->  gameService.getGameById(createdActiveGame.getId()));
    }

    @Test
    public void CannotRemovePlayerBecauseWrongPhase() {
        createdActiveGame.setStateForLogicService(State.WordReveal);
        logicService.initializeTurn(createdActiveGame.getId());
        createdActiveGame.setStateForLogicService(State.ChooseMysteryWord);
        //try to remove the player
        assertThrows(ConflictException.class, ()->   gameService.removePlayerFromGame(createdActiveGame.getId(), p1.getToken()));
    }
}
