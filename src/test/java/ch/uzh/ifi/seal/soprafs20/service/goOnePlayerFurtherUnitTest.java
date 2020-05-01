package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class goOnePlayerFurtherUnitTest {
    @Autowired
    LogicService logicService;
    List<Long> passivePlayers=new ArrayList<>();
    GameEntity game=new GameEntity();

    @BeforeEach
    public void setup(){
        game.setActivePlayerId(0L);
        passivePlayers.add(1L);
        game.setPassivePlayerIds(passivePlayers);
    }

    @Test
    public void worksWithTwoHumanPlayers(){
        logicService.goOnePlayerFurther(game);

        assertEquals(1,game.getPassivePlayerIds().size());
        assertEquals(0L,game.getPassivePlayerIds().get(0));
        assertEquals(1L,game.getActivePlayerId());
    }

    @Test
    public void worksWithMoreThanTwoPlayers(){
        passivePlayers.add(2L);
        game.setPassivePlayerIds(passivePlayers);

        logicService.goOnePlayerFurther(game);

        assertEquals(2,game.getPassivePlayerIds().size());
        assertEquals(2L,game.getPassivePlayerIds().get(0));
        assertEquals(0L,game.getPassivePlayerIds().get(1));
        assertEquals(1L,game.getActivePlayerId());
    }

    @Test
    public void worksWithOnePlayerWhenHeIsPlayingWithBots(){
        game.setPassivePlayerIds(new ArrayList<>());

        logicService.goOnePlayerFurther(game);

        assertEquals(0,game.getPassivePlayerIds().size());
        assertEquals(0L,game.getActivePlayerId());
    }
}
