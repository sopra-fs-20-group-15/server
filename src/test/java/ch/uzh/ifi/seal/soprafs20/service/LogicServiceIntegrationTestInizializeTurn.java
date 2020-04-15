package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
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
import java.util.HashMap;
import java.util.List;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceIntegrationTestInizializeTurn {
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
        createdActiveGame.setHasEnded(true);
        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("Two");
        cluePostDTO.setClue("Clue");
        logicService.giveClue(p2.getToken(), createdActiveGame, cluePostDTO);
    }
 /**Initialize Turn works*/
 /**Initialize Turn does not work since the game has not ended yet*/
 /**Initialize Turn does not work since the game has already been initialized*/
    @Test
    public void InitializeTurnWorks() {


        GameEntity initializedGame = logicService.initializeTurn(createdActiveGame.getId());

        //ActiveCard, CardIds, ActivePlayerId and PassivePlayerIds get already tested in LogicServiceTestInitializeTurn by analysing the helper functions
        //Evaluation
        assertEquals(initializedGame.getActiveMysteryWord(), "");
        assertEquals(initializedGame.getHasEnded(), false);
        assertEquals(initializedGame.getRightGuess(), false);
        assertEquals(initializedGame.getRightGuess(), false);
        assertEquals(initializedGame.getValidClue(), false);
        assertEquals(initializedGame.getValidCluesAreSet(), false);
        assertEquals(initializedGame.getClueMap(), new HashMap<String, String>());
        assertEquals(initializedGame.getValidClues(), new ArrayList<String>());
        assertEquals(initializedGame.getGuess(), "");
        assertEquals(initializedGame.getIsValidGuess(), false);
        assertEquals(initializedGame.getHasBeenInitialized(), true);
    }

    @Test
    public void InitializeTurnErrorBecauseGameHasAlreadyBeenInitialized() {
        createdActiveGame.setHasBeenInitialized(true);

        assertThrows(NoContentException.class, () -> {logicService.initializeTurn(createdActiveGame.getId());});
    }

    @Test
    public void InitializeTurnErrorBecauseGameHasNotEndedYet() {
        createdActiveGame.setHasBeenInitialized(true);

        assertThrows(ConflictException.class, () -> {logicService.initializeTurn(createdActiveGame.getId());});
    }

}
