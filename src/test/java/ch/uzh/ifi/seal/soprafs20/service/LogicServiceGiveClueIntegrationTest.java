package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
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

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceGiveClueIntegrationTest {

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

        game.setHostId(p1.getId());
        game.setGameName("GameName");

        createdGame =gameService.createGame(game);

        createdActiveGame =gameService.getGameById(gameService.createActiveGame(createdGame.getId(), "One").getId());
    }

    @Test
    public void passivePlayerGivesClue() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        logicService.giveClue(p2.getToken(), createdActiveGame, cluePostDTO);
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Clue");
        assertTrue(createdActiveGame.getValidClues().isEmpty());
        assertFalse(createdActiveGame.getValidCluesAreSet());
    }

    @Test
    public void passivePlayerGivesClueTwice() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        logicService.giveClue(p2.getToken(), createdActiveGame, cluePostDTO);
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Clue");
        assertThrows(UnauthorizedException.class, () -> {logicService.giveClue(p2.getToken(), createdActiveGame, cluePostDTO);});
    }

    @Test
    public void allPassivePlayerGiveClues() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        logicService.giveClue(p2.getToken(), createdActiveGame, cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(p3.getToken(), createdActiveGame, cluePostDTO);

        assertTrue(createdActiveGame.getValidCluesAreSet());
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Clue");
        assertTrue(createdActiveGame.getClueMap().containsKey("Three"));
        assertEquals(createdActiveGame.getClueMap().get("Three"), "Table");
        assertFalse(createdActiveGame.getValidClues().isEmpty());
        assertTrue(createdActiveGame.getValidClues().contains("Clue") &&
                createdActiveGame.getValidClues().contains("Table"));
    }

    @Test
    public void cluesTooCloseToMysteryWordGetThrownOut() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Testing");
        logicService.giveClue(p2.getToken(), createdActiveGame, cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(p3.getToken(), createdActiveGame, cluePostDTO);

        assertTrue(createdActiveGame.getValidCluesAreSet());
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Testing");
        assertTrue(createdActiveGame.getClueMap().containsKey("Three"));
        assertEquals(createdActiveGame.getClueMap().get("Three"), "Table");
        assertFalse(createdActiveGame.getValidClues().isEmpty());
        assertTrue(!createdActiveGame.getValidClues().contains("Testing") &&
                createdActiveGame.getValidClues().contains("Table"));
    }

    @Test
    public void identicalCluesGetThrownOut() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Table");
        logicService.giveClue(p2.getToken(), createdActiveGame, cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("table");
        logicService.giveClue(p3.getToken(), createdActiveGame, cluePostDTO);

        assertTrue(createdActiveGame.getValidCluesAreSet());
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Table");
        assertTrue(createdActiveGame.getClueMap().containsKey("Three"));
        assertEquals(createdActiveGame.getClueMap().get("Three"), "table");
        assertTrue(createdActiveGame.getValidClues().isEmpty());
        assertTrue(!createdActiveGame.getValidClues().contains("Table")
                && !createdActiveGame.getValidClues().contains("table"));
    }

    @Test
    public void cluesWithSameStemGetThrownOut() {
        createdActiveGame.setActiveMysteryWord("Test");
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Tower");
        logicService.giveClue(p2.getToken(), createdActiveGame, cluePostDTO);

        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("towering");
        logicService.giveClue(p3.getToken(), createdActiveGame, cluePostDTO);

        assertTrue(createdActiveGame.getValidCluesAreSet());
        assertTrue(createdActiveGame.getClueMap().containsKey("Two"));
        assertEquals(createdActiveGame.getClueMap().get("Two"), "Tower");
        assertTrue(createdActiveGame.getClueMap().containsKey("Three"));
        assertEquals(createdActiveGame.getClueMap().get("Three"), "towering");
        assertTrue(createdActiveGame.getValidClues().isEmpty());
        assertTrue(!createdActiveGame.getValidClues().contains("Tower")
                && !createdActiveGame.getValidClues().contains("towering"));
    }
}
