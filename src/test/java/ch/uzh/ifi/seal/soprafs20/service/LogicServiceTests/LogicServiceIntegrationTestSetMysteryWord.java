package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;
import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.Helper.TestSETUPCreatesActiveGame;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.service.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional
@SpringBootTest
public class LogicServiceIntegrationTestSetMysteryWord extends TestSETUPCreatesActiveGame {

    CardEntity card;
    @BeforeEach
    public void setup2() {
        createdActiveGame.setStateForLogicService(State.ChooseMysteryWord);
        createdActiveGame.setActiveMysteryWord("");
        createdActiveGame.drawCardFromStack();
        card=logicService.getCardFromGameById(createdActiveGame.getId());
        List<String> list=new ArrayList<>();
        list.add("Elf");
        list.add("Cat");
        list.add("Mouse");
        list.add("Fire");
        list.add("Voldemort");
        card.setWords(list);
    }

    @Test
    public void setMysteryWordsWorksWithOnlyHumanPlayers() {

        logicService.setMysteryWord(createdActiveGame.getId(),1L);
        assertEquals(createdActiveGame.getActiveMysteryWord(), logicService.getCardFromGameById(createdActiveGame.getId()).getWords().get(0));
        assertFalse(createdActiveGame.getValidCluesAreSet());
    }

    @Test
    public void setMysteryWordWorksWithBotsOnlyAsPassivePlayers() {
        // remove human players and add bots
        createdActiveGame.setPassivePlayerIds(new ArrayList<>());

//      add bots
        Angel angel = new Angel();
        angel.setName("Angelo_Merte");
        angel.setToken("Merte");

        List<Angel> angels = new ArrayList<>();
        angels.add(angel);

        createdActiveGame.setAngels(angels);

        Map<String, String> map = createdActiveGame.getClueMap();
        map.put(angel.getToken(), "Angel");
        createdActiveGame.setClueMap(map);

        Devil devil = new Devil();
        devil.setName("Lucius");
        devil.setToken("Malfoy");

        List<Devil> devils = new ArrayList<>();
        devils.add(devil);

        createdActiveGame.setDevils(devils);

        createdActiveGame.getPlayers().remove(1);
        createdActiveGame.getPlayers().remove(1);

        //check that mysteryWord is set and that validClues are set bc there are no human players aside from the active one
        logicService.setMysteryWord(createdActiveGame.getId(),1L);
        assertEquals(createdActiveGame.getActiveMysteryWord(), card.getWords().get(0));
        assertTrue(createdActiveGame.getValidCluesAreSet());
        assertNotNull(createdActiveGame.getTimeStart());
    }

    @Test
    public void setMysteryWordsFailsBecauseMysteryWordAlreadySet() {
        logicService.setMysteryWord(createdActiveGame.getId(), 1L);
        assertThrows(NoContentException.class, ()->{logicService.setMysteryWord(createdActiveGame.getId(),1L);});
    }

    @Test
    public void setMysteryWordsFailsBecauseGameIdDoesNotExist() {
        assertThrows(NotFoundException.class, ()->{logicService.setMysteryWord(55L,1L);});
    }

}


