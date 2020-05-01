package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.GameLogic.WordComparer;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Null;
import org.mockito.internal.verification.Only;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.*;

import org.springframework.expression.spel.ast.NullLiteral;
import static org.mockito.BDDMockito.given;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class addValidCluesUnitTest {
    @Autowired
    LogicService logicService;
    @MockBean
    WordComparer wordComparer;
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
        Map<String, Integer> analyzedClueMap=new HashMap<>();
        analyzedClueMap.put("house",0);
        analyzedClueMap.put("fire",1);
        analyzedClueMap.put("two",2);

        clueMap.put("1","clue1");
        clueMap.put("a1","clue2");
        clueMap.put("d1","clue3");

        game.setClueMap(clueMap);


        given(wordComparer.compareClues(Mockito.any(), Mockito.anyString())).willReturn(analyzedClueMap);
        given(playerRepository.findByToken(Mockito.anyString())).will((InvocationOnMock invocation)
        -> invocation.getArgument(0).equals("1") ? p1 : null);


        logicService.addValidClues(game);

        System.out.println(game.getAnalyzedClues().get("house"));
        assertTrue(game.getValidCluesAreSet());
        assertEquals(3,game.getValidClues().size());
        assertTrue(game.getValidClues().containsKey("angel"));
        assertTrue(game.getValidClues().containsKey("devil"));
        assertTrue(game.getValidClues().containsKey("One"));
        assertEquals("clue1",game.getValidClues().get("One"));
        assertEquals("clue2",game.getValidClues().get("angel"));
        assertEquals("clue3",game.getValidClues().get("devil"));
    }

}
