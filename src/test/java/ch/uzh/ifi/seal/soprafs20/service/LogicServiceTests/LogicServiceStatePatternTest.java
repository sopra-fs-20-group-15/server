package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.service.*;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceStatePatternTest extends TestSETUPCreatesActiveGame{

    @Qualifier("cardRepository")
    @Autowired
    CardRepository cardRepository;

    @Autowired
    public LogicServiceStatePatternTest(@Qualifier("cardService") CardService cardService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService, @Qualifier("logicService") LogicService logicService, @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, @Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("activeGameService") ActiveGameService activeGameService, LSStateChooseMysteryWord lsStateChooseMysteryWord, LSSGiveClues lssGiveClues, LSSGiveGuess lssGiveGuess, LSSWordReveal lssWordReveal, LSSGameHasEnded lssGameHasEnded) {
        super(cardService, logicService, gameSetUpService, playerService, playerRepository, activeGameService, gameRepository, gameSetUpRepository, lsStateChooseMysteryWord, lssGiveClues, lssGiveGuess, lssWordReveal, lssGameHasEnded);
    }

    @BeforeTransaction
    public void clean(){
        gameSetUpRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
        cardRepository.deleteAll();
    }

    @Test
    public void ChangesStateCorrectly() {
        //Initializes game before it starts
        createdActiveGame.setStateForLogicService(State.WordReveal);
        logicService.initializeTurn(createdActiveGame.getId());

        //First Phase
        logicService.setMysteryWord(createdActiveGame.getId(), 1L);
        assertEquals(createdActiveGame.getStateForLogicService(), State.GiveClues);

        //Second Phase
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("One");
        cluePostDTO.setClue("Pancake");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);
        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);
        assertEquals(createdActiveGame.getStateForLogicService(), State.GiveGuess);

        //Third Phase
        logicService.setGuess(createdActiveGame.getId(), "123");
        assertEquals(createdActiveGame.getStateForLogicService(), State.WordReveal);

        //Fourth Phase
        logicService.initializeTurn(createdActiveGame.getId());
        assertEquals(createdActiveGame.getStateForLogicService(), State.ChooseMysteryWord);


    }
}
