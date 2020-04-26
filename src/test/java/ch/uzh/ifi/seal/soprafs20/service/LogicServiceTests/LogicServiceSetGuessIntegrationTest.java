package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GuessPostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceSetGuessIntegrationTest extends TestSETUPCreatesActiveGame {

    @BeforeEach
    public void setup2() {

        logicService.initializeTurn(createdActiveGame.getId());
        logicService.setMysteryWord(createdActiveGame.getId(),3L);
        createdActiveGame.setActiveMysteryWord("Shoe");

        CluePostDTO cluePostDTO=new CluePostDTO();
        cluePostDTO.setPlayerToken("One");
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
        guessPostDTO.setPlayerToken("Two");
        guessPostDTO.setGuess("Shoe");
        logicService.setGuess(createdActiveGame, guessPostDTO.getGuess());
        assertEquals(createdActiveGame.getGuess(), "Shoe");
        assertTrue(createdActiveGame.getIsValidGuess());
        for (Map.Entry<String, Integer> entry: scoresBefore.entrySet()) {
            assertNotEquals(entry.getValue(),createdActiveGame.getScoreboard().getScore().get(entry.getKey()));
        }
        assertEquals(1,createdActiveGame.getScoreboard().getCorrectlyGuessedMysteryWordsPerPlayer().get("TwoName"));
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
        assertEquals(false,createdActiveGame.getHasBeenInitialized());
        assertEquals(11, createdActiveGame.getCardIds().size());
    }
}
