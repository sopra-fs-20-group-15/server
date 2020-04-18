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
        playerNamesScoreHashMap.put("A", 100);
        playerNamesScoreHashMap.put("B", 200);
        playerNamesScoreHashMap.put("C", 300);
        playerNamesScoreHashMap.put("D", 400);
        playerNamesScoreHashMap.put("E", 500);
        playerNamesScoreHashMap.put("F", 600);

        List<Integer> scores = logicService.orderScores(playerNamesScoreHashMap);

        assertEquals(scores.get(0), 600);
        assertEquals(scores.get(1), 500);
        assertEquals(scores.get(2), 400);
        assertEquals(scores.get(3), 300);
        assertEquals(scores.get(4), 200);
        assertEquals(scores.get(5), 100);
    }

    /**Is the sorting algorithm working properly? Does it sort descending?*/
    @Test
    public void orderScoresEqualValues(){
        Map<String, Integer> playerNamesScoreHashMap = new HashMap<String, Integer>();
        playerNamesScoreHashMap.put("A", 100);
        playerNamesScoreHashMap.put("B", 200);
        playerNamesScoreHashMap.put("C", 200);
        playerNamesScoreHashMap.put("D", 200);
        playerNamesScoreHashMap.put("E", 500);
        playerNamesScoreHashMap.put("F", 600);

        List<Integer> scores = logicService.orderScores(playerNamesScoreHashMap);

        assertEquals(scores.get(0), 600);
        assertEquals(scores.get(1), 500);
        assertEquals(scores.get(2), 200);
        assertEquals(scores.get(3), 200);
        assertEquals(scores.get(4), 200);
        assertEquals(scores.get(5), 100);
    }
}
