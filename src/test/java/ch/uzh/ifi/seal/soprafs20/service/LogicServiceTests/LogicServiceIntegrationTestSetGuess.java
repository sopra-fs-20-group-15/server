package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GuessPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.*;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceIntegrationTestSetGuess extends TestSETUPCreatesActiveGame{

    @Autowired
    public LogicServiceIntegrationTestSetGuess(@Qualifier("cardService") CardService cardService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService, @Qualifier("logicService") LogicService logicService, @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, @Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("activeGameService") ActiveGameService activeGameService, LSStateChooseMysteryWord lsStateChooseMysteryWord, LSSGiveClues lssGiveClues, LSSGiveGuess lssGiveGuess, LSSWordReveal lssWordReveal, LSSGameHasEnded lssGameHasEnded) {
        super(cardService, logicService, gameSetUpService, playerService, playerRepository, activeGameService, gameRepository, gameSetUpRepository, lsStateChooseMysteryWord, lssGiveClues, lssGiveGuess, lssWordReveal, lssGameHasEnded);
    }

    protected CardEntity card;

    @BeforeTransaction
    public void clean(){
        gameSetUpRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    /**MysteryWord and Clues are set artificially*/
    @BeforeEach
    public void setup2() {
        createdActiveGame.setStateForLogicService(State.ChooseMysteryWord);
        createdActiveGame.setActiveMysteryWord("");
        createdActiveGame.setTimeStart(123L);
        createdActiveGame.drawCardFromStack();
        card=logicService.getCardFromGameById(createdActiveGame.getId());
        List<String> list=new ArrayList<>();
        list.add("Elf");
        list.add("Cat");
        list.add("Shoe");
        list.add("Fire");
        list.add("Voldemort");
        card.setWords(list);
        logicService.setMysteryWord(createdActiveGame.getId(),3L);

        CluePostDTO cluePostDTO=new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Gremlin");
        logicService.giveClue(createdActiveGame.getId(),cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Nutcracker");
        logicService.giveClue(createdActiveGame.getId(),cluePostDTO);
    }

    @Test
    public void activePlayerGivesCorrectGuess() {
        Map<String, Integer> scoresBefore= Map.copyOf(createdActiveGame.getScoreboard().getScore());
        GuessPostDTO guessPostDTO = new GuessPostDTO();
        guessPostDTO.setPlayerToken("One");
        guessPostDTO.setGuess("Shoe");
        logicService.setGuess(createdActiveGame.getId(), guessPostDTO.getGuess());
        assertEquals(createdActiveGame.getGuess(), "Shoe");
        assertTrue(createdActiveGame.getIsValidGuess());
        for (Map.Entry<String, Integer> entry: scoresBefore.entrySet()) {
            assertNotEquals(entry.getValue(),createdActiveGame.getScoreboard().getScore().get(entry.getKey()));
        }
        // is the number of correctly guessed mysteryWords for that player updated?
        assertEquals(1,createdActiveGame.getScoreboard().getCorrectlyGuessedMysteryWordsPerPlayer().get("OneName"));
        // is no supplementary card drawn?
        assertEquals(12, createdActiveGame.getCardIds().size());
    }

    @Test
    public void activePlayerGivesIncorrectGuess() {
        GuessPostDTO guessPostDTO = new GuessPostDTO();
        guessPostDTO.setPlayerToken("Two");
        guessPostDTO.setGuess("Tree");
        logicService.setGuess(createdActiveGame.getId(), guessPostDTO.getGuess());
        assertEquals(createdActiveGame.getGuess(), "Tree");
        assertFalse(createdActiveGame.getIsValidGuess());
        assertEquals(0,createdActiveGame.getScoreboard().getCorrectlyGuessedMysteryWordsPerPlayer().get("TwoName"));
        //A card should be drawn if the guess was wrong
        assertEquals(11, createdActiveGame.getCardIds().size());
    }

    /**Edge Case: Do no problems arise if the cardStack is already empty and the guess is wrong -> second card should not be drawn and no error thrown*/
    @Test
    public void activePlayerGivesIncorrectGuessAndTheCardStackIsEmpty() {
        createdActiveGame.setCardIds(new ArrayList<Long>());

        GuessPostDTO guessPostDTO = new GuessPostDTO();
        guessPostDTO.setPlayerToken("Two");
        guessPostDTO.setGuess("Tree");
        logicService.setGuess(createdActiveGame.getId(), guessPostDTO.getGuess());
        assertEquals(createdActiveGame.getGuess(), "Tree");
        assertFalse(createdActiveGame.getIsValidGuess());
        assertEquals(0,createdActiveGame.getScoreboard().getCorrectlyGuessedMysteryWordsPerPlayer().get("TwoName"));
        assertEquals(0, createdActiveGame.getCardIds().size());
    }

    /**Not possible in any phase except GiveGuess*/

    @Test
    public void giveGuessFailsInChooseMysteryWord() {
        createdActiveGame.setStateForLogicService(State.ChooseMysteryWord);
        assertThrows(NoContentException.class, () -> {logicService.setGuess(createdActiveGame.getId(), "Test"); });
    }

    @Test
    public void giveGuessFailsInGiveClue() {
        createdActiveGame.setStateForLogicService(State.GiveClues);
        assertThrows(NoContentException.class, () -> {logicService.setGuess(createdActiveGame.getId(), "Test"); });
    }
    @Test
    public void giveGuessFailsWordReveal() {
        createdActiveGame.setStateForLogicService(State.WordReveal);
        assertThrows(NoContentException.class, () -> {logicService.setGuess(createdActiveGame.getId(), "Test"); });
    }
    @Test
    public void giveGuessFailsInHasEnded() {
        createdActiveGame.setStateForLogicService(State.hasEnded);
        assertThrows(NoContentException.class, () -> {logicService.setGuess(createdActiveGame.getId(), "Test"); });
    }
}
