package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceIntegrationTestHasGameEnded extends TestSETUPCreatesActiveGame {


    @BeforeEach
    public void setup2() {
        createdActiveGame.setActiveMysteryWord("Test");
        createdActiveGame.setTimeStart(123L);
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        logicService.giveClue( createdActiveGame.getId(), cluePostDTO);
        logicService.initializeTurn(createdActiveGame.getId());
    }


 /**Check a case where the game has not ended yet*/
    @Test
    public void GameHasNotEnded() {
        //c
        Boolean hasEnded = logicService.hasGameEnded(createdActiveGame.getId());

        assertFalse(hasEnded);
        assertFalse(createdActiveGame.getHasEnded());

    }

/***/
    @Test
    public void GameHasEnded() {

        createdActiveGame.setCardIds(new ArrayList<Long>());
        //c
        Boolean hasEnded = logicService.hasGameEnded(createdActiveGame.getId());

        assertTrue(hasEnded);
        assertTrue(createdActiveGame.getHasEnded());


    }
}
