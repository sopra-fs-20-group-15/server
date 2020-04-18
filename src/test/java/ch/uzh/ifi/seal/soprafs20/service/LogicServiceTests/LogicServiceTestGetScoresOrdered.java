package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;

import ch.uzh.ifi.seal.soprafs20.rest.dto.StatisticsGetDTO;
import ch.uzh.ifi.seal.soprafs20.service.LogicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class LogicServiceTestGetScoresOrdered {

    @Autowired
    private LogicService logicService;

    /**Is the sorting algorithm working properly? Does it sort descending?*/
    @Test
    public void orderScores(){
        Map<String, Integer> playerNamesScoreHashMap = new HashMap<String, Integer>();
        List<StatisticsGetDTO> rankScorePlayerNameList = new ArrayList<StatisticsGetDTO>();
        for (int i = 0; i < 6; i++){
            StatisticsGetDTO statisticsGetDTO = new StatisticsGetDTO();
            int score = 100 + i*100;
            statisticsGetDTO.setScore(score);
            char userName = (char)(i +65);
            statisticsGetDTO.setPlayerName(String.valueOf(userName));
            rankScorePlayerNameList.add(statisticsGetDTO);
        }


        List<StatisticsGetDTO> scores = logicService.orderStatisticsGetDTOList(rankScorePlayerNameList);

        //Check scores
        assertEquals(scores.get(0).getScore(), 600);
        assertEquals(scores.get(1).getScore(), 500);
        assertEquals(scores.get(2).getScore(), 400);
        assertEquals(scores.get(3).getScore(), 300);
        assertEquals(scores.get(4).getScore(), 200);
        assertEquals(scores.get(5).getScore(), 100);

        //Check names
        assertEquals(scores.get(0).getPlayerName(), "F");
        assertEquals(scores.get(1).getPlayerName(), "E");
        assertEquals(scores.get(2).getPlayerName(), "D");
        assertEquals(scores.get(3).getPlayerName(), "C");
        assertEquals(scores.get(4).getPlayerName(), "B");
        assertEquals(scores.get(5).getPlayerName(), "A");
    }

    /**Is the sorting algorithm working properly? Does it sort descending even when there are dublicates?*/
    @Test
    public void orderScoresEqualValues(){
        Map<String, Integer> playerNamesScoreHashMap = new HashMap<String, Integer>();
        List<StatisticsGetDTO> rankScorePlayerNameList = new ArrayList<StatisticsGetDTO>();
        for (int i = 0; i < 6; i++){
            StatisticsGetDTO statisticsGetDTO = new StatisticsGetDTO();
            int score = 100 + i*100;
            statisticsGetDTO.setScore(score);
            char userName = (char)(i +65);
            statisticsGetDTO.setPlayerName(String.valueOf(userName));
            rankScorePlayerNameList.add(statisticsGetDTO);
        }
        StatisticsGetDTO statisticsGetDTO1 = new StatisticsGetDTO();
        statisticsGetDTO1.setScore(200);
        statisticsGetDTO1.setPlayerName("C");
        rankScorePlayerNameList.set(2, statisticsGetDTO1);
        StatisticsGetDTO statisticsGetDTO2 = new StatisticsGetDTO();
        statisticsGetDTO2.setScore(200);
        statisticsGetDTO1.setPlayerName("D");
        rankScorePlayerNameList.set(3, statisticsGetDTO1);

        List<StatisticsGetDTO> scores = logicService.orderStatisticsGetDTOList(rankScorePlayerNameList);

        //Check scores
        assertEquals(scores.get(0).getScore(), 600);
        assertEquals(scores.get(1).getScore(), 500);
        assertEquals(scores.get(2).getScore(), 200);
        assertEquals(scores.get(3).getScore(), 200);
        assertEquals(scores.get(4).getScore(), 200);
        assertEquals(scores.get(5).getScore(), 100);

    }

    /**Is the algorithm assigning the right ranks to the players of a list that was sorted in descending order? -> Get stats*/
    @Test
    public void HighestPointCountBestPlacement(){
        Map<String, Integer> playerNamesScoreHashMap = new HashMap<String, Integer>();
        List<StatisticsGetDTO> rankScorePlayerNameList = new ArrayList<StatisticsGetDTO>();
        for (int i = 0; i < 6; i++){
            StatisticsGetDTO statisticsGetDTO = new StatisticsGetDTO();
            int score = 1000 - i*100;
            statisticsGetDTO.setScore(score);
            char userName = (char)(i +65);
            statisticsGetDTO.setPlayerName(String.valueOf(userName));
            rankScorePlayerNameList.add(statisticsGetDTO);
        }


        List<StatisticsGetDTO> scores = logicService.orderStatisticsGetDTOList(rankScorePlayerNameList);

        //Check scores
        assertEquals(scores.get(0).getScore(), 600);
        assertEquals(scores.get(1).getScore(), 500);
        assertEquals(scores.get(2).getScore(), 400);
        assertEquals(scores.get(3).getScore(), 300);
        assertEquals(scores.get(4).getScore(), 200);
        assertEquals(scores.get(5).getScore(), 100);

        //Check names
        assertEquals(scores.get(0).getPlayerName(), "F");
        assertEquals(scores.get(1).getPlayerName(), "E");
        assertEquals(scores.get(2).getPlayerName(), "D");
        assertEquals(scores.get(3).getPlayerName(), "C");
        assertEquals(scores.get(4).getPlayerName(), "B");
        assertEquals(scores.get(5).getPlayerName(), "A");
    }
}
