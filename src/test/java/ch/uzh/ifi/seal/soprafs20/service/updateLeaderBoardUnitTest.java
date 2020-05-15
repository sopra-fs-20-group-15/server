package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Scoreboard;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.service.LogicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class updateLeaderBoardUnitTest {

    @Autowired
    ActiveGameService activeGameService;
    @Autowired
    LogicService logicService;
    GameEntity game=new GameEntity();
    Scoreboard sb=new Scoreboard();
    PlayerEntity p1=new PlayerEntity();
    PlayerEntity p2=new PlayerEntity();
    PlayerEntity p3=new PlayerEntity();

    @BeforeEach
    public void setup(){
        p1.setUsername("One");
        p2.setUsername("Two");
        p3.setUsername("Three");
        p1.setPassword("123");
        p2.setPassword("123");
        p3.setPassword("123");
        p1.setStatus(PlayerStatus.ONLINE);
        p2.setStatus(PlayerStatus.ONLINE);
        p3.setStatus(PlayerStatus.ONLINE);
        p1.setToken("1");
        p2.setToken("2");
        p3.setToken("3");


        List<PlayerEntity> list= new ArrayList<>();
        list.add(p1);
        list.add(p2);
        list.add(p3);

        sb.initializeMap(list);

        sb.updateScore(p1,1);
        sb.updateScore(p2, 2);
        sb.updateScore(p3, 3);

        game.setScoreboard(sb);
        game.setPlayers(list);
    }

    @Test
    public void updateLeaderBoardWorks(){
        activeGameService.updateLeaderBoard(game);
        assertEquals(1,p1.getLeaderBoardScore());
        assertEquals(1, p1.getGamesPlayed());
        assertEquals(2,p2.getLeaderBoardScore());
        assertEquals(1,p2.getGamesPlayed());
        assertEquals(3, p3.getLeaderBoardScore());
        assertEquals(1, p3.getGamesPlayed());
    }
}
