package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Scoreboard;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.StatisticsGetDTO;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.LogicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceIntegrationTestGetStatistics {

    @Autowired
    private GameSetUpRepository gameSetUpRepository;

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private LogicService logicService;

    private GameSetUpEntity game = new GameSetUpEntity();

    private GameSetUpEntity createdGame;
    private GameEntity  createdActiveGame;

    private PlayerEntity p1;
    private PlayerEntity p2;
    private PlayerEntity p3;
    private PlayerEntity p4;


    @BeforeTransaction
    public void clean(){
        gameSetUpRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @BeforeEach
    public void setup() {
        game.setNumberOfPlayers(3L);
        game.setNumberOfAngles(0L);
        game.setNumberOfDevils(0L);
        game.setGameType(PRIVATE);
        game.setPassword("Cara");
        PlayerEntity playerOne= new PlayerEntity();
        PlayerEntity playerTwo= new PlayerEntity();
        PlayerEntity playerThree= new PlayerEntity();

        playerOne.setUsername("OneName");
        playerOne.setPassword("One");
        playerOne.setToken("One");
        playerOne.setStatus(PlayerStatus.ONLINE);
        p1=playerRepository.save(playerOne);

        playerTwo.setUsername("TwoName");
        playerTwo.setPassword("Two");
        playerTwo.setToken("Two");
        playerTwo.setStatus(PlayerStatus.ONLINE);
        p2=playerRepository.save(playerTwo);

        playerThree.setUsername("ThreeName");
        playerThree.setToken("Three");
        playerThree.setPassword("Three");
        playerThree.setStatus(PlayerStatus.ONLINE);
        p3=playerRepository.save(playerThree);

        List<String> playerTokens=new ArrayList<>();
        playerTokens.add("One");
        playerTokens.add("Two");
        playerTokens.add("Three");


        game.setPlayerTokens(playerTokens);

        game.setHostName(p1.getUsername());
        game.setGameName("GameName");

        createdGame =gameService.createGame(game);

        createdActiveGame =gameService.getGameById(gameService.createActiveGame(createdGame.getId(), "One").getId());
        Scoreboard scoreboard = new Scoreboard();
        Map<PlayerEntity, Integer> scores = new HashMap<PlayerEntity, Integer>();
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
        scores.put(player1,100);
        scores.put(player2,200);
        scores.put(player3,300);
        scores.put(player4,400);
        scoreboard.setScoreboard(scores);
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
        intended.add(statisticsGetDTO);
        statisticsGetDTO.setScore(300);
        statisticsGetDTO.setPlayerName("C");
        statisticsGetDTO.setPlacement(2);
        intended.add(statisticsGetDTO);
        statisticsGetDTO.setScore(200);
        statisticsGetDTO.setPlayerName("B");
        statisticsGetDTO.setPlacement(3);
        intended.add(statisticsGetDTO);
        statisticsGetDTO.setScore(100);
        statisticsGetDTO.setPlayerName("A");
        statisticsGetDTO.setPlacement(4);
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
    }
}
