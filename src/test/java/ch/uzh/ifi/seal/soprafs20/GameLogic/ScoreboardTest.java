package ch.uzh.ifi.seal.soprafs20.GameLogic;

import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.rest.dto.StatisticsGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardTest {

    private Scoreboard scoreboard = new Scoreboard();

    @BeforeEach
    public void setup() {
        //Put some fake scores for giving clues and guessing mystery words correctly
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
        mysteryWords.put(player1.getUsername(), 1);
        mysteryWords.put(player2.getUsername(), 2);
        mysteryWords.put(player3.getUsername(), 3);
        mysteryWords.put(player4.getUsername(), 4);
        scoreboard.setScoreboard(scores);
        scoreboard.setCorrectlyGuessedMysteryWordsPerPlayer(mysteryWords);
    }

    /**Can the scoreboard transform itself correctly into a List of StatisticsGetDTOs?*/
    @Test
    void ScoreBoardIntoList() {
        //Should be
        List<StatisticsGetDTO> intended = new ArrayList<StatisticsGetDTO>();
        StatisticsGetDTO statisticsGetDTO = new StatisticsGetDTO();
        statisticsGetDTO.setScore(100);
        statisticsGetDTO.setPlayerName("A");
        statisticsGetDTO.setNumberOfCorrectlyGuessedMysteryWords(1);
        intended.add(statisticsGetDTO);
        statisticsGetDTO.setScore(200);
        statisticsGetDTO.setPlayerName("B");
        statisticsGetDTO.setNumberOfCorrectlyGuessedMysteryWords(2);
        intended.add(statisticsGetDTO);
        statisticsGetDTO.setScore(300);
        statisticsGetDTO.setPlayerName("C");
        statisticsGetDTO.setNumberOfCorrectlyGuessedMysteryWords(3);
        intended.add(statisticsGetDTO);
        statisticsGetDTO.setScore(400);
        statisticsGetDTO.setPlayerName("D");
        statisticsGetDTO.setNumberOfCorrectlyGuessedMysteryWords(4);
        intended.add(statisticsGetDTO);

        List<StatisticsGetDTO> actualList = scoreboard.transformIntoList();
        List<Integer> scoresActualList = new ArrayList<Integer>();
        List<String>  namesActualList = new ArrayList<String>();
        List<Integer> mysteryActualList = new ArrayList<Integer>();
        for (StatisticsGetDTO element : actualList){
            scoresActualList.add(element.getScore());
            namesActualList.add(element.getPlayerName());
            mysteryActualList.add(element.getNumberOfCorrectlyGuessedMysteryWords());
        }

        assertTrue(scoresActualList.contains(intended.get(0).getScore()));
        assertTrue(scoresActualList.contains(intended.get(1).getScore()));
        assertTrue(scoresActualList.contains(intended.get(2).getScore()));
        assertTrue(scoresActualList.contains(intended.get(3).getScore()));
        assertTrue(namesActualList.contains(intended.get(0).getPlayerName()));
        assertTrue(namesActualList.contains(intended.get(1).getPlayerName()));
        assertTrue(namesActualList.contains(intended.get(2).getPlayerName()));
        assertTrue(namesActualList.contains(intended.get(3).getPlayerName()));
        assertTrue(mysteryActualList.contains(intended.get(0).getNumberOfCorrectlyGuessedMysteryWords()));
        assertTrue(mysteryActualList.contains(intended.get(1).getNumberOfCorrectlyGuessedMysteryWords()));
        assertTrue(mysteryActualList.contains(intended.get(2).getNumberOfCorrectlyGuessedMysteryWords()));
        assertTrue(mysteryActualList.contains(intended.get(3).getNumberOfCorrectlyGuessedMysteryWords()));
    }

    /**When a player is removed due to inactivity before a game has ended, he or she should also be taken out of the scoreboard*/
    @Test
    void removePlayersFromScoreBoardWhenRemovingSinglePlayer(){
        //make sure map is not empty at beginning of game
        assertEquals(4, scoreboard.getScore().size());
        //add player to the list of players that should be removed
        List<String> usersToBeRemoved=new ArrayList<>();
        usersToBeRemoved.add("A");
        //remove players in list
        scoreboard.removePlayersFromScoreBoard(usersToBeRemoved);
        //check that players were removed
        assertEquals(3, scoreboard.getScore().size());
        assertEquals(3,scoreboard.getCorrectlyGuessedMysteryWordsPerPlayer().size());
        assertFalse(scoreboard.getScore().containsKey("A"));
        assertFalse(scoreboard.getCorrectlyGuessedMysteryWordsPerPlayer().containsKey("A"));

    }

    @Test
    void removePlayersFromScoreBoardWorksWhenAllPlayersAreRemovedAtOnce(){
        //make sure map is not empty at beginning of game
        assertEquals(4, scoreboard.getScore().size());
        //add all players to the list of players that should be removed
        List<String> usersToBeRemoved=new ArrayList<>();
        usersToBeRemoved.add("A");
        usersToBeRemoved.add("B");
        usersToBeRemoved.add("C");
        usersToBeRemoved.add("D");
        //remove players
        scoreboard.removePlayersFromScoreBoard(usersToBeRemoved);
        //check that players were removed
        assertTrue(scoreboard.getScore().isEmpty());
        assertTrue(scoreboard.getCorrectlyGuessedMysteryWordsPerPlayer().isEmpty());
    }


    @Test
    void removePlayersFromScoreBoardDoesNothingIfNoPlayerMarkedForRemoval(){
        //make sure map is not empty at beginning of game
        assertEquals(4, scoreboard.getScore().size());
        //do not add players to the list of players that should be removed
        List<String> usersToBeRemoved=new ArrayList<>();
        //remove players in list
        scoreboard.removePlayersFromScoreBoard(usersToBeRemoved);
        //check that players were removed
        assertEquals(4, scoreboard.getScore().size());
        assertEquals(4,scoreboard.getCorrectlyGuessedMysteryWordsPerPlayer().size());
    }

}
