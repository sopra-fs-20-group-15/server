package ch.uzh.ifi.seal.soprafs20.GameLogic;

import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.rest.dto.StatisticsGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardTest {

    private Scoreboard scoreboard = new Scoreboard();

    @BeforeEach
    public void setup() {
        Map<String, Integer> scores = new HashMap<String, Integer>();
        //players
        PlayerEntity player1 = new PlayerEntity();
        player1.setUsername("A");
        PlayerEntity player2 = new PlayerEntity();
        player1.setUsername("B");
        PlayerEntity player3 = new PlayerEntity();
        player1.setUsername("C");
        PlayerEntity player4 = new PlayerEntity();
        player1.setUsername("D");
        //put with scores into hash map
        scores.put(player1.getUsername(),100);
        scores.put(player2.getUsername(),200);
        scores.put(player3.getUsername(),300);
        scores.put(player4.getUsername(),400);
        scoreboard.setScoreboard(scores);
    }

    /**Can the scoreboard transform itself correctly into a List of StatisticsGetDTOs?*/
    @Test
    void ScoreBoardIntoList() {
        //Should be
        List<StatisticsGetDTO> intended = new ArrayList<StatisticsGetDTO>();
        StatisticsGetDTO statisticsGetDTO = new StatisticsGetDTO();
        statisticsGetDTO.setScore(100);
        statisticsGetDTO.setPlayerName("A");
        intended.add(statisticsGetDTO);
        statisticsGetDTO.setScore(200);
        statisticsGetDTO.setPlayerName("B");
        intended.add(statisticsGetDTO);
        statisticsGetDTO.setScore(300);
        statisticsGetDTO.setPlayerName("C");
        intended.add(statisticsGetDTO);
        statisticsGetDTO.setScore(400);
        statisticsGetDTO.setPlayerName("D");
        intended.add(statisticsGetDTO);

        List<StatisticsGetDTO> actualList = scoreboard.transformIntoList();
        List<Integer> scoresActualList = new ArrayList<Integer>();
        List<String>  namesActualList = new ArrayList<String>();
        for (StatisticsGetDTO element : actualList){
            scoresActualList.add(element.getScore());
            namesActualList.add(element.getPlayerName());
        }


        assertTrue(scoresActualList.contains(intended.get(0).getScore()));
        assertTrue(scoresActualList.contains(intended.get(1).getScore()));
        assertTrue(scoresActualList.contains(intended.get(2).getScore()));
        assertTrue(scoresActualList.contains(intended.get(3).getScore()));
        assertTrue(namesActualList.contains(intended.get(0).getPlayerName()));
        assertTrue(namesActualList.contains(intended.get(1).getPlayerName()));
        assertTrue(namesActualList.contains(intended.get(2).getPlayerName()));
        assertTrue(namesActualList.contains(intended.get(3).getPlayerName()));

    }
}
