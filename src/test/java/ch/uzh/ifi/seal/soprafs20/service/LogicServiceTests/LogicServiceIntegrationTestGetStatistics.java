package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Scoreboard;
import ch.uzh.ifi.seal.soprafs20.rest.dto.StatisticsGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class LogicServiceIntegrationTestGetStatistics extends TestSETUPLogicService {

    @BeforeEach
    public void setup2() {

        Scoreboard scoreboard = new Scoreboard();
        Map<String, Integer> scores = new HashMap<String, Integer>();
        Map<String, Integer> mysteryWords = new HashMap<String, Integer>();
        //players
        PlayerEntity player1 = new PlayerEntity();
        player1.setUsername("A");
        PlayerEntity player2 = new PlayerEntity();
        player2.setUsername("B");
        PlayerEntity player3 = new PlayerEntity();
        player3.setUsername("C");
        PlayerEntity player4 = new PlayerEntity();
        player4.setUsername("D");
        //put with scores into hash map
        scores.put(player1.getUsername(),100);
        scores.put(player2.getUsername(),200);
        scores.put(player3.getUsername(),300);
        scores.put(player4.getUsername(),400);
        mysteryWords.put(player1.getUsername(), 4);
        mysteryWords.put(player2.getUsername(), 3);
        mysteryWords.put(player3.getUsername(), 2);
        mysteryWords.put(player4.getUsername(), 1);
        scoreboard.setScoreboard(scores);
        scoreboard.setCorrectlyGuessedMysteryWordsPerPlayer(mysteryWords);
        createdActiveGame.setScoreboard(scoreboard);

    }

    /**Is a correctly ordered list of the scoreboard returned?*/
    @Test
    public void getStatisticsWorks() {
        //Should be
        List<StatisticsGetDTO> intended = new ArrayList<StatisticsGetDTO>();
        StatisticsGetDTO statisticsGetDTO = new StatisticsGetDTO();
        statisticsGetDTO.setScore(400);
        statisticsGetDTO.setPlayerName("D");
        statisticsGetDTO.setPlacement(1);
        statisticsGetDTO.setNumberOfCorrectlyGuessedMysteryWords(1);
        intended.add(statisticsGetDTO);
        statisticsGetDTO.setScore(300);
        statisticsGetDTO.setPlayerName("C");
        statisticsGetDTO.setPlacement(2);
        statisticsGetDTO.setNumberOfCorrectlyGuessedMysteryWords(2);
        intended.add(statisticsGetDTO);
        statisticsGetDTO.setScore(200);
        statisticsGetDTO.setPlayerName("B");
        statisticsGetDTO.setPlacement(3);
        statisticsGetDTO.setNumberOfCorrectlyGuessedMysteryWords(3);
        intended.add(statisticsGetDTO);
        statisticsGetDTO.setScore(100);
        statisticsGetDTO.setPlayerName("A");
        statisticsGetDTO.setPlacement(4);
        statisticsGetDTO.setNumberOfCorrectlyGuessedMysteryWords(4);
        intended.add(statisticsGetDTO);

        List<StatisticsGetDTO> actualList = logicService.getStatistics(createdActiveGame.getId());

        //Check scores
        assertEquals(actualList.get(0).getScore(), 400);
        assertEquals(actualList.get(1).getScore(), 300);
        assertEquals(actualList.get(2).getScore(), 200);
        assertEquals(actualList.get(3).getScore(), 100);

        //Check names
        assertEquals(actualList.get(0).getPlayerName(), "D");
        assertEquals(actualList.get(1).getPlayerName(), "C");
        assertEquals(actualList.get(2).getPlayerName(), "B");
        assertEquals(actualList.get(3).getPlayerName(), "A");

        //Check ranks
        assertEquals(actualList.get(0).getPlacement(), 1);
        assertEquals(actualList.get(1).getPlacement(), 2);
        assertEquals(actualList.get(2).getPlacement(), 3);
        assertEquals(actualList.get(3).getPlacement(), 4);

        //Check number of correctly guessed cards
        assertEquals(actualList.get(0).getNumberOfCorrectlyGuessedMysteryWords(), 1);
        assertEquals(actualList.get(1).getNumberOfCorrectlyGuessedMysteryWords(), 2);
        assertEquals(actualList.get(2).getNumberOfCorrectlyGuessedMysteryWords(), 3);
        assertEquals(actualList.get(3).getNumberOfCorrectlyGuessedMysteryWords(), 4);
    }
}
