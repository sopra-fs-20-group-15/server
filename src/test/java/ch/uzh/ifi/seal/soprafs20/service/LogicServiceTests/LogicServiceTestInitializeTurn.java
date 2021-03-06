package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.service.LogicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;


@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceTestInitializeTurn {

    @Autowired
    private LogicService logicService;


    /**Test Helper Method drawCardFromStack*/
    @Test
    public void drawCardFromStackWorks(){
        GameEntity activeGame = new GameEntity();
        activeGame.setActiveCardId(1L);
        List<Long> idList = new ArrayList<Long>();
        idList.add(2L);
        idList.add(3L);
        idList.add(4L);
        activeGame.setCardIds(idList);

        //Values before
        Long oldActiveCardId = activeGame.getActiveCardId();
        Long oldIdFromFirstCardOnStack = idList.get(idList.size()-1); //The stack is implemented in the way that the "Last card" is at index 0 and the first card on size()-1
        int oldStackSize = activeGame.getCardIds().size();

        //Call function
        GameEntity changedGame = activeGame.drawCardFromStack();

        //Values after
        Long newActiveCardId = activeGame.getActiveCardId();
        int newStackSize = activeGame.getCardIds().size();

        assertNotEquals(oldActiveCardId, newActiveCardId);
        assertEquals(oldIdFromFirstCardOnStack, newActiveCardId);
        assertEquals(oldStackSize-1, newStackSize);
    }

}
