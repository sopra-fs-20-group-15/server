package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GuessPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceSetGuessIntegrationTest extends TestSETUPCreatesActiveGame{

    @Autowired
    public LogicServiceSetGuessIntegrationTest(@Qualifier("cardService") CardService cardService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService, @Qualifier("logicService") LogicService logicService, @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, @Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("activeGameService") ActiveGameService activeGameService, LSStateChooseMysteryWord lsStateChooseMysteryWord, LSSGiveClues lssGiveClues, LSSGiveGuess lssGiveGuess, LSSWordReveal lssWordReveal, LSSGameHasEnded lssGameHasEnded) {
        super(cardService, logicService, gameSetUpService, playerService, playerRepository, activeGameService, gameRepository, gameSetUpRepository, lsStateChooseMysteryWord, lssGiveClues, lssGiveGuess, lssWordReveal, lssGameHasEnded);
    }

    protected CardEntity card;

    @BeforeTransaction
    public void clean(){
        gameSetUpRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }

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
        assertEquals(1,createdActiveGame.getScoreboard().getCorrectlyGuessedMysteryWordsPerPlayer().get("OneName"));
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
        assertEquals(11, createdActiveGame.getCardIds().size());
    }

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
}
