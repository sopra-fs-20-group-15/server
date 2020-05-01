package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class botsAddCluesUnitTest {
    @Autowired
    LogicService logicService;
    Angel angel=new Angel();
    Devil devil=new Devil();
    GameEntity game=new GameEntity();
    String mysteryWord="Fish";

    @BeforeEach
    public void setup(){
        angel.setName("Angel");
        angel.setToken("a");
        devil.setName("Devil");
        devil.setToken("d");
        game.setClueMap(new HashMap<>());
        List<Angel> angels=new ArrayList<>();
        angels.add(angel);
        List<Devil> devils=new ArrayList<>();
        devils.add(devil);
        game.setAngels(angels);
        game.setDevils(devils);
    }

    @Test
    public void angelGivesClue(){
        game.setDevils(new ArrayList<>());
        logicService.botsAddClues(game, mysteryWord);

        assertEquals(1, game.getClueMap().size());
        assertTrue(game.getClueMap().containsKey(angel.getToken()));
    }

    @Test
    public void devilGivesClue(){
        game.setAngels(new ArrayList<>());
        logicService.botsAddClues(game, mysteryWord);

        assertEquals(1, game.getClueMap().size());
        assertTrue(game.getClueMap().containsKey(devil.getToken()));
    }

    @Test
    public void angelAndDevilGiveClue(){
        logicService.botsAddClues(game, mysteryWord);

        assertEquals(2, game.getClueMap().size());
        assertTrue(game.getClueMap().containsKey(devil.getToken()));
        assertTrue(game.getClueMap().containsKey(angel.getToken()));
    }

    @Test
    public void doesNothingIfNoBots(){
        game.setAngels(new ArrayList<>());
        game.setDevils(new ArrayList<>());
        logicService.botsAddClues(game, mysteryWord);

        assertEquals(0, game.getClueMap().size());
    }
}
