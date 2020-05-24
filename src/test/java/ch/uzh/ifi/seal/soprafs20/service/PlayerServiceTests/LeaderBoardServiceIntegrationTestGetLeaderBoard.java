package ch.uzh.ifi.seal.soprafs20.service.PlayerServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LeaderBoardGetDTO;
import ch.uzh.ifi.seal.soprafs20.service.LeaderBoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LeaderBoardServiceIntegrationTestGetLeaderBoard {

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private LeaderBoardService leaderBoardService;

    PlayerEntity p1;
    PlayerEntity p2;
    PlayerEntity p3;

    /**Set data for the leader board*/
    @BeforeTransaction
    public void setup() {
        playerRepository.deleteAll();

        p1= new PlayerEntity();
        p1.setUsername("1");
        p1.setPassword("1");
        p1.setToken("1");
        p1.setLeaderBoardScore(10);
        p1.setStatus(PlayerStatus.ONLINE);
        p1.setGamesPlayed(3);
        p1=playerRepository.save(p1);


        p2= new PlayerEntity();
        p2.setUsername("2");
        p2.setPassword("2");
        p2.setToken("2");
        p2.setLeaderBoardScore(5);
        p2.setStatus(PlayerStatus.ONLINE);
        p2.setGamesPlayed(4);
        p2=playerRepository.save(p2);

        p3= new PlayerEntity();
        p3.setUsername("3");
        p3.setPassword("3");
        p3.setToken("3");
        p3.setLeaderBoardScore(0);
        p3.setStatus(PlayerStatus.ONLINE);
        p3.setGamesPlayed(44);
        p3=playerRepository.save(p3);
    }

    @Test
    public void getLeaderBoardWorksWithNoDuplicateScores() {
        List<LeaderBoardGetDTO> list= leaderBoardService.getLeaderBoard();

        //check if list has right order and right values
        assertEquals(3,list.size());

        assertEquals(p1.getUsername(),list.get(0).getPlayerName());
        assertEquals(p1.getLeaderBoardScore(),list.get(0).getScore());
        assertEquals(1,list.get(0).getRank());
        assertEquals(p1.getGamesPlayed(), list.get(0).getGamesPlayed());

        assertEquals(p2.getUsername(),list.get(1).getPlayerName());
        assertEquals(p2.getLeaderBoardScore(),list.get(1).getScore());
        assertEquals(2,list.get(1).getRank());
        assertEquals(p2.getGamesPlayed(), list.get(1).getGamesPlayed());

        assertEquals(p3.getUsername(),list.get(2).getPlayerName());
        assertEquals(p3.getLeaderBoardScore(),list.get(2).getScore());
        assertEquals(3,list.get(2).getRank());
        assertEquals(p3.getGamesPlayed(), list.get(2).getGamesPlayed());
    }

    @Test
    public void getLeaderBoardWorksWithDuplicateScores() {
        p2.setLeaderBoardScore(10);
        p2=playerRepository.save(p2);

        PlayerEntity p4= new PlayerEntity();
        p4.setUsername("4");
        p4.setPassword("4");
        p4.setToken("4");
        p4.setLeaderBoardScore(0);
        p4.setStatus(PlayerStatus.ONLINE);
        p4=playerRepository.save(p4);

        PlayerEntity p5= new PlayerEntity();
        p5.setUsername("5");
        p5.setPassword("5");
        p5.setToken("5");
        p5.setLeaderBoardScore(-5);
        p5.setStatus(PlayerStatus.ONLINE);
        p5=playerRepository.save(p5);

        List<LeaderBoardGetDTO> list= leaderBoardService.getLeaderBoard();

        //check if list has right order and right values
        assertEquals(5,list.size());

        assertEquals(p1.getUsername(),list.get(0).getPlayerName());
        assertEquals(p1.getLeaderBoardScore(),list.get(0).getScore());
        assertEquals(1,list.get(0).getRank());
        assertEquals(p1.getGamesPlayed(), list.get(0).getGamesPlayed());

        assertEquals(p2.getUsername(),list.get(1).getPlayerName());
        assertEquals(p2.getLeaderBoardScore(),list.get(1).getScore());
        assertEquals(1,list.get(1).getRank());
        assertEquals(p2.getGamesPlayed(), list.get(1).getGamesPlayed());

        assertEquals(p3.getUsername(),list.get(2).getPlayerName());
        assertEquals(p3.getLeaderBoardScore(),list.get(2).getScore());
        assertEquals(3,list.get(2).getRank());
        assertEquals(p3.getGamesPlayed(), list.get(2).getGamesPlayed());


        assertEquals(p4.getUsername(),list.get(3).getPlayerName());
        assertEquals(p4.getLeaderBoardScore(),list.get(3).getScore());
        assertEquals(3,list.get(3).getRank());
        assertEquals(p4.getGamesPlayed(), list.get(3).getGamesPlayed());


        assertEquals(p5.getUsername(),list.get(4).getPlayerName());
        assertEquals(p5.getLeaderBoardScore(),list.get(4).getScore());
        assertEquals(5,list.get(4).getRank());
        assertEquals(p5.getGamesPlayed(), list.get(4).getGamesPlayed());

    }
}
