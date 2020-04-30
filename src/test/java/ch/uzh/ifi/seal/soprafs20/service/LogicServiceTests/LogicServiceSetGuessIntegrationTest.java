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
import ch.uzh.ifi.seal.soprafs20.service.ActiveGameService;
import ch.uzh.ifi.seal.soprafs20.service.GameSetUpService;
import ch.uzh.ifi.seal.soprafs20.service.LogicService;
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

    protected CardEntity card;

    @BeforeTransaction
    public void clean(){
        gameSetUpRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @BeforeEach
    public void setup2() {
        createdActiveGame.setActiveMysteryWord("");
        createdActiveGame.setTimeStart(123L);
        logicService.drawCardFromStack(createdActiveGame);
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
        logicService.giveClue(createdActiveGame,cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Nutcracker");
        logicService.giveClue(createdActiveGame,cluePostDTO);
    }

    @Test
    public void activePlayerGivesCorrectGuess() {
        Map<String, Integer> scoresBefore= Map.copyOf(createdActiveGame.getScoreboard().getScore());
        GuessPostDTO guessPostDTO = new GuessPostDTO();
        guessPostDTO.setPlayerToken("One");
        guessPostDTO.setGuess("Shoe");
        logicService.setGuess(createdActiveGame, guessPostDTO.getGuess());
        assertEquals(createdActiveGame.getGuess(), "Shoe");
        assertTrue(createdActiveGame.getIsValidGuess());
        for (Map.Entry<String, Integer> entry: scoresBefore.entrySet()) {
            assertNotEquals(entry.getValue(),createdActiveGame.getScoreboard().getScore().get(entry.getKey()));
        }
        assertEquals(1,createdActiveGame.getScoreboard().getCorrectlyGuessedMysteryWordsPerPlayer().get("OneName"));
        assertEquals(false,createdActiveGame.getHasBeenInitialized());
        assertEquals(12, createdActiveGame.getCardIds().size());
    }

    @Test
    public void activePlayerGivesIncorrectGuess() {
        GuessPostDTO guessPostDTO = new GuessPostDTO();
        guessPostDTO.setPlayerToken("Two");
        guessPostDTO.setGuess("Tree");
        logicService.setGuess(createdActiveGame, guessPostDTO.getGuess());
        assertEquals(createdActiveGame.getGuess(), "Tree");
        assertFalse(createdActiveGame.getIsValidGuess());
        assertEquals(0,createdActiveGame.getScoreboard().getCorrectlyGuessedMysteryWordsPerPlayer().get("TwoName"));
        assertFalse(createdActiveGame.getHasBeenInitialized());
        assertEquals(11, createdActiveGame.getCardIds().size());
    }

    @Test
    public void activePlayerGivesIncorrectGuessAndTheCardStackIsEmpty() {
        createdActiveGame.setCardIds(new ArrayList<Long>());

        GuessPostDTO guessPostDTO = new GuessPostDTO();
        guessPostDTO.setPlayerToken("Two");
        guessPostDTO.setGuess("Tree");
        logicService.setGuess(createdActiveGame, guessPostDTO.getGuess());
        assertEquals(createdActiveGame.getGuess(), "Tree");
        assertFalse(createdActiveGame.getIsValidGuess());
        assertEquals(0,createdActiveGame.getScoreboard().getCorrectlyGuessedMysteryWordsPerPlayer().get("TwoName"));
        assertFalse(createdActiveGame.getHasBeenInitialized());
        assertEquals(0, createdActiveGame.getCardIds().size());
    }
}
