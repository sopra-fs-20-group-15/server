package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class addValidCluesUnitTest {
    @Autowired
    LSSGiveClues lSSGiveClues;
    @MockBean
    PlayerRepository playerRepository;
    GameEntity game=new GameEntity();
    PlayerEntity p1=new PlayerEntity();
    Devil d1=new Devil();
    Angel a1=new Angel();
    Map<String, String> clueMap=new HashMap<>();

    @BeforeEach
    public void setup(){
        p1.setUsername("One");
        p1.setToken("1");
        p1.setStatus(PlayerStatus.ONLINE);
        a1.setName("angel");
        a1.setToken("a1");
        d1.setName("devil");
        d1.setToken("d1");
        List<Angel> angels=new ArrayList<>();
        angels.add(a1);
        List<Devil> devils=new ArrayList<>();
        devils.add(d1);
        game.setActiveMysteryWord("sdfsdf");
        game.setAngels(angels);
        game.setDevils(devils);
    }

    @Test
    public void worksWhenAllCluesAreValid(){
        clueMap.put("1","One");
        clueMap.put("a1","Two");
        clueMap.put("d1","Three");

        game.setClueMap(clueMap);

        when(playerRepository.findByToken("1")).thenReturn(p1);

        lSSGiveClues.addValidClues(game);

        assertEquals(3,game.getValidClues().size());
        assertTrue(game.getValidClues().containsKey("angel"));
        assertTrue(game.getValidClues().containsKey("devil"));
        assertTrue(game.getValidClues().containsKey("One"));
        assertEquals("One",game.getValidClues().get("One"));
        assertEquals("Two",game.getValidClues().get("angel"));
        assertEquals("Three",game.getValidClues().get("devil"));
    }

    @Test
    public void worksWhenSomeCluesAreValid(){
        clueMap.put("1","One");
        clueMap.put("a1","One");
        clueMap.put("d1","Three");

        game.setClueMap(clueMap);

        when(playerRepository.findByToken("1")).thenReturn(p1);

        lSSGiveClues.addValidClues(game);

        System.out.println(game.getValidClues().entrySet().toString());
        assertEquals(1,game.getValidClues().size());
        assertTrue(game.getValidClues().containsKey("devil"));
        assertEquals("Three",game.getValidClues().get("devil"));
    }

    @Test
    public void worksWhenNoCluesAreValid(){
        clueMap.put("1","One");
        clueMap.put("a1","One");
        clueMap.put("d1","One");

        game.setClueMap(clueMap);

        when(playerRepository.findByToken("1")).thenReturn(p1);

        lSSGiveClues.addValidClues(game);

        assertEquals(0,game.getValidClues().size());
    }

}
