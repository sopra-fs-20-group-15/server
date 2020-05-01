package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class createListOfCluesGetDTOsUnitTest {
    @Autowired
    LogicService logicService;
    GameEntity game=new GameEntity();
    Map<String, String> validClues=new HashMap<>();

    @BeforeEach
    public void setup(){
        validClues.put("Joe", "clue1");
        validClues.put("Charlotte", "clue2");
        validClues.put("Raphy", "clue3");

        game.setValidClues(validClues);
    }

    @Test
    public void createListOfCluesGetDTOWorksWithFilledMap(){
        List<ClueGetDTO> list=logicService.createListOfClueGetDTOs(game);
        assertEquals(3, list.size());
        assertEquals("Joe",list.get(0).getPlayerName());
        assertEquals("clue1",list.get(0).getClue());
        assertEquals("Charlotte",list.get(1).getPlayerName());
        assertEquals("clue2",list.get(1).getClue());
        assertEquals("Raphy",list.get(2).getPlayerName());
        assertEquals("clue3",list.get(2).getClue());
   }

    @Test
    public void createListOfCluesGetDTOWorksWithEmptyMap(){
        game.setValidClues(new HashMap<>());
        List<ClueGetDTO> list=logicService.createListOfClueGetDTOs(game);
        assertTrue(list.isEmpty());
    }
}



