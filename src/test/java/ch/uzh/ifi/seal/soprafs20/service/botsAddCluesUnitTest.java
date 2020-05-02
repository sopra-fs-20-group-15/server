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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        assertFalse(game.getClueMap().get(angel.getToken()).isBlank());
    }

    @Test
    public void devilGivesClue(){
        game.setAngels(new ArrayList<>());
        logicService.botsAddClues(game, mysteryWord);

        assertEquals(1, game.getClueMap().size());
        assertTrue(game.getClueMap().containsKey(devil.getToken()));
        assertFalse(game.getClueMap().get(devil.getToken()).isBlank());
    }

    @Test
    public void angelAndDevilGiveClue(){
        logicService.botsAddClues(game, mysteryWord);

        assertEquals(2, game.getClueMap().size());
        assertTrue(game.getClueMap().containsKey(devil.getToken()));
        assertTrue(game.getClueMap().containsKey(angel.getToken()));
        assertFalse(game.getClueMap().get(devil.getToken()).isBlank());
        assertFalse(game.getClueMap().get(angel.getToken()).isBlank());

    }

    @Test
    public void multipleAngelsAndDevilsGiveClues(){
        Angel angel2=new Angel();
        angel2.setName("Angel2");
        angel2.setToken("a2");
        Devil devil2=new Devil();
        devil2.setName("Devil2");
        devil2.setToken("d2");

        List<Angel> angels=game.getAngels();
        angels.add(angel2);
        game.setAngels(angels);
        List<Devil> devils=game.getDevils();
        devils.add(devil2);
        game.setDevils(devils);

        logicService.botsAddClues(game, mysteryWord);

        assertEquals(4, game.getClueMap().size());
        assertTrue(game.getClueMap().containsKey(devil.getToken()));
        assertFalse(game.getClueMap().get(devil.getToken()).isBlank());
        assertTrue(game.getClueMap().containsKey(devil2.getToken()));
        assertFalse(game.getClueMap().get(devil2.getToken()).isBlank());
        assertTrue(game.getClueMap().containsKey(angel.getToken()));
        assertFalse(game.getClueMap().get(angel.getToken()).isBlank());
        assertTrue(game.getClueMap().containsKey(angel2.getToken()));
        assertFalse(game.getClueMap().get(angel2.getToken()).isBlank());
    }



    @Test
    public void doesNothingIfNoBots(){
        game.setAngels(new ArrayList<>());
        game.setDevils(new ArrayList<>());
        logicService.botsAddClues(game, mysteryWord);

        assertEquals(0, game.getClueMap().size());
    }
}
