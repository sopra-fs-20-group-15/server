package ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.LSSGiveClues;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@Transactional
@WebAppConfiguration
@SpringBootTest
public class addClueToClueMapUnitTest {
    @MockBean
    PlayerRepository playerRepository;
    @Autowired
    LSSGiveClues lSSGiveClues;
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
        when(playerRepository.findByToken(Mockito.anyString())).thenReturn(player);

        lSSGiveClues.addClueToClueMap(game,cluePostDTO);

        assertEquals(1,game.getClueMap().size());
        assertTrue(game.getClueMap().containsKey(player.getToken()));
        assertEquals(cluePostDTO.getClue(),game.getClueMap().get(player.getToken()));
    }

    @Test
    public void addClueToClueMapWorksCorrectlyOnMapWithEntries(){
        when(playerRepository.findByToken(Mockito.anyString())).thenReturn(player);
        game.getClueMap().put("pseudoToken","pseudoClue");

        lSSGiveClues.addClueToClueMap(game,cluePostDTO);

        assertEquals(2,game.getClueMap().size());
        assertTrue(game.getClueMap().containsKey(player.getToken()));
        assertEquals(cluePostDTO.getClue(),game.getClueMap().get(player.getToken()));
    }
}



