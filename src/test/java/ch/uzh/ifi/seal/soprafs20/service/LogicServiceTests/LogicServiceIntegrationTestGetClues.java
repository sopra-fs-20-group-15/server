package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
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
import java.util.List;
import java.util.Map;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceIntegrationTestGetClues {

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
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        createdActiveGame.setTimeStart(123L);
        logicService.giveClue(createdActiveGame, cluePostDTO);
    }

    @Test
    public void cluesAreSetAndGetClueWorksWithOnlyHumanPlayers() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        assertFalse(createdActiveGame.getValidClues().isEmpty());
        List<ClueGetDTO> listOfClues = logicService.getClues(createdActiveGame);

        assertEquals(2, listOfClues.size());

        assertEquals("Clue", listOfClues.get(0).getClue());
        assertEquals(p2.getUsername(), listOfClues.get(0).getPlayerName());

        assertEquals("Table", listOfClues.get(1).getClue());
        assertEquals(p3.getUsername(), listOfClues.get(1).getPlayerName());
    }

    @Test
    public void cluesAreSetAndGetClueWorksWithBots() {
        createdActiveGame.setActiveMysteryWord("Test");
//      add bots
        Angel angel=new Angel();
        angel.setName("Angelo_Merte");
        angel.setToken("Merte");

        List<Angel> angels=new ArrayList<>();
        angels.add(angel);

        createdActiveGame.setAngels(angels);

        Map<String, String> map=createdActiveGame.getClueMap();
        map.put(angel.getToken(),"Angel");
        createdActiveGame.setClueMap(map);

        Devil devil=new Devil();
        devil.setName("Lucius");
        devil.setToken("Malfoy");

        List<Devil> devils=new ArrayList<>();
        devils.add(devil);

        createdActiveGame.setDevils(devils);

        map=createdActiveGame.getClueMap();
        map.put(devil.getToken(),"Devil");
        createdActiveGame.setClueMap(map);

        CluePostDTO cluePostDTO = new CluePostDTO();

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(createdActiveGame, cluePostDTO);

        assertFalse(createdActiveGame.getValidClues().isEmpty());
        List<ClueGetDTO> listOfClues = logicService.getClues(createdActiveGame);

        assertEquals(4, listOfClues.size());

        assertEquals("Devil", listOfClues.get(0).getClue());
        assertEquals(devil.getName(), listOfClues.get(0).getPlayerName());

        assertEquals("Clue", listOfClues.get(1).getClue());
        assertEquals(p2.getUsername(), listOfClues.get(1).getPlayerName());

        assertEquals("Angel", listOfClues.get(3).getClue());
        assertEquals(angel.getName(), listOfClues.get(3).getPlayerName());

        assertEquals("Table", listOfClues.get(2).getClue());
        assertEquals(p3.getUsername(), listOfClues.get(2).getPlayerName());
    }

    @Test
    public void validCluesHaveNotBeenSetYet() {
        assertThrows(NoContentException.class, () -> {logicService.getClues(createdActiveGame); });
    }

}