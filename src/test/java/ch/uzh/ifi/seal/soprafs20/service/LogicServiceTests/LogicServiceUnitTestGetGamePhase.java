package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;


import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePhaseDTO;
import ch.uzh.ifi.seal.soprafs20.service.ActiveGameService;
import ch.uzh.ifi.seal.soprafs20.service.LogicService;
import ch.uzh.ifi.seal.soprafs20.service.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;



@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceUnitTestGetGamePhase {

    @Autowired
    private LogicService logicService;

    @MockBean
    private ActiveGameService activeGameService;


    /**Idea behind these tests: check that number matches intended gamePhaseName*/

    @Test
    public void giveBackCorrectGamePhaseChooseMysteryWordAndCorrectTime(){
        //GameEntity that shall be returned
        GameEntity game = new GameEntity();
        game.setStateForLogicService(State.ChooseMysteryWord);
        game.setTimeStart(0L);

        given(activeGameService.getGameById(anyLong())).willReturn(game);

        GamePhaseDTO gamePhaseDTOActual = logicService.getGamePhase(2L);

        assertEquals("ChooseMysteryWord", gamePhaseDTOActual.getPhase() );
        assertEquals(1,gamePhaseDTOActual.getPhaseNumber());
        assertEquals(0L, gamePhaseDTOActual.getTimeStart());
    }
    @Test
    public void giveBackCorrectGamePhaseGiveClues(){
        //GameEntity that shall be returned
        GameEntity game = new GameEntity();
        game.setTimeStart(0L);
        game.setStateForLogicService(State.GiveClues);

        given(activeGameService.getGameById(anyLong())).willReturn(game);

        GamePhaseDTO gamePhaseDTOActual = logicService.getGamePhase(2L);

        assertEquals(gamePhaseDTOActual.getPhase(), "GiveClues");
        assertEquals(gamePhaseDTOActual.getPhaseNumber(), 2);
    }
    @Test
    public void giveBackCorrectGamePhaseGiveGuess(){
        //GameEntity that shall be returned
        GameEntity game = new GameEntity();
        game.setStateForLogicService(State.GiveGuess);
        game.setTimeStart(0L);

        given(activeGameService.getGameById(anyLong())).willReturn(game);

        GamePhaseDTO gamePhaseDTOActual = logicService.getGamePhase(2L);

        assertEquals("GiveGuess", gamePhaseDTOActual.getPhase());
        assertEquals(3, gamePhaseDTOActual.getPhaseNumber());
    }
    @Test
    public void giveBackCorrectGamePhaseWordReveal(){
        //GameEntity that shall be returned
        GameEntity game = new GameEntity();
        game.setStateForLogicService(State.WordReveal);
        game.setTimeStart(0L);

        given(activeGameService.getGameById(anyLong())).willReturn(game);

        GamePhaseDTO gamePhaseDTOActual = logicService.getGamePhase(2L);

        assertEquals("WordReveal", gamePhaseDTOActual.getPhase());
        assertEquals(4, gamePhaseDTOActual.getPhaseNumber());
    }

    @Test
    public void giveBackCorrectGamePhaseHasEnded(){
        //GameEntity that shall be returned
        GameEntity game = new GameEntity();
        game.setStateForLogicService(State.hasEnded);
        game.setTimeStart(0L);

        given(activeGameService.getGameById(anyLong())).willReturn(game);

        GamePhaseDTO gamePhaseDTOActual = logicService.getGamePhase(2L);

        assertEquals("hasEnded", gamePhaseDTOActual.getPhase());
        assertEquals(10, gamePhaseDTOActual.getPhaseNumber());
    }
}
