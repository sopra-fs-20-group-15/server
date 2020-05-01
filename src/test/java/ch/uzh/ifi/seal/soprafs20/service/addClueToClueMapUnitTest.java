package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class addClueToClueMapUnitTest {
    @MockBean
    PlayerService playerService;
    @Autowired
    LogicService logicService;
    GameEntity game=new GameEntity();
    Map<String, String> clueMap=new HashMap<>();
    CluePostDTO cluePostDTO=new CluePostDTO();
    PlayerEntity player=new PlayerEntity();
    Long beforeTime;

    @BeforeEach
    public void setup(){
        game.setClueMap(clueMap);
        game.setTimeStart(System.currentTimeMillis());
        cluePostDTO.setPlayerToken("token");
        cluePostDTO.setClue("clue");
        player.setToken("token");
        player.setUsername("player");
        player.setStatus(PlayerStatus.ONLINE);
        player.setTimePassed(0L);
        beforeTime=player.getTimePassed();
    }
    @Test
    public void addClueToClueMapWorksCorrectlyOnEmptyMap(){
        when(playerService.getPlayerByToken(Mockito.anyString())).thenReturn(player);

        logicService.addClueToClueMap(game,cluePostDTO);

        assertEquals(1,game.getClueMap().size());
        assertTrue(game.getClueMap().containsKey(player.getToken()));
        assertEquals(cluePostDTO.getClue(),game.getClueMap().get(player.getToken()));
        assertTrue(beforeTime<player.getTimePassed());
    }

    @Test
    public void addClueToClueMapWorksCorrectlyOnMapWithEntries(){
        when(playerService.getPlayerByToken(Mockito.anyString())).thenReturn(player);
        game.getClueMap().put("pseudoToken","pseudoClue");

        logicService.addClueToClueMap(game,cluePostDTO);

        assertEquals(2,game.getClueMap().size());
        assertTrue(game.getClueMap().containsKey(player.getToken()));
        assertEquals(cluePostDTO.getClue(),game.getClueMap().get(player.getToken()));
        assertTrue(beforeTime<player.getTimePassed());
    }
}



