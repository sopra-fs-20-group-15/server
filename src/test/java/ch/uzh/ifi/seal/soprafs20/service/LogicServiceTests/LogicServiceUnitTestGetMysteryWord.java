package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.service.ActiveGameService;
import ch.uzh.ifi.seal.soprafs20.service.LogicService;
import ch.uzh.ifi.seal.soprafs20.service.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

/**Should work in all phases after ChooseMysteryWord*/
@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceUnitTestGetMysteryWord {

    @Autowired
    private LogicService logicService;

    @MockBean
    private ActiveGameService activeGameService;

    @Test
    public void getMysteryWordWorks(){
        //GameEntity that shall be returned
        GameEntity game = new GameEntity();
        game.setActiveMysteryWord("Test");
        game.setStateForLogicService(State.GiveClues);

        given(activeGameService.getGameById(anyLong())).willReturn(game);

        String mysteryWordActual = logicService.getMysteryWord(2L);

        assertEquals("Test", mysteryWordActual );
    }

    @Test
    public void getMysteryWordWorksInGiveGuess(){
        //GameEntity that shall be returned
        GameEntity game = new GameEntity();
        game.setActiveMysteryWord("Test");
        game.setStateForLogicService(State.GiveGuess);

        given(activeGameService.getGameById(anyLong())).willReturn(game);

        String mysteryWordActual = logicService.getMysteryWord(2L);

        assertEquals("Test", mysteryWordActual );
    }

    @Test
    public void getMysteryWordWorksInWordReveal(){
        //GameEntity that shall be returned
        GameEntity game = new GameEntity();
        game.setActiveMysteryWord("Test");
        game.setStateForLogicService(State.WordReveal);

        given(activeGameService.getGameById(anyLong())).willReturn(game);

        String mysteryWordActual = logicService.getMysteryWord(2L);

        assertEquals("Test", mysteryWordActual );
    }

    @Test
    public void getMysteryWordFailsInChooseMysteryWord(){
        //GameEntity that shall be returned
        GameEntity game = new GameEntity();
        game.setActiveMysteryWord("Test");
        game.setStateForLogicService(State.ChooseMysteryWord);

        given(activeGameService.getGameById(anyLong())).willReturn(game);
            assertThrows(NoContentException.class, () -> {logicService.getMysteryWord(2L);});

    }

    @Test
    public void getMysteryWordFailsInGameHasEnded(){
        //GameEntity that shall be returned
        GameEntity game = new GameEntity();
        game.setActiveMysteryWord("Test");
        game.setStateForLogicService(State.hasEnded);

        given(activeGameService.getGameById(anyLong())).willReturn(game);
        assertThrows(NoContentException.class, () -> {logicService.getMysteryWord(2L);});

    }
}

