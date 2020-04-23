
package ch.uzh.ifi.seal.soprafs20.service.ActiveGameServiceTests;


import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ActiveGamePostDTO;
import ch.uzh.ifi.seal.soprafs20.service.ActiveGameService;
import ch.uzh.ifi.seal.soprafs20.service.GameSetUpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationCreateActiveGameTest {


    @Qualifier("gameSetUpEntityRepository")
    @Autowired
    private GameSetUpRepository gameSetUpRepository;

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ActiveGameService gameService;

    @Autowired
    private GameSetUpService gameSetUpService;

    private GameSetUpEntity game = new GameSetUpEntity();

    private GameSetUpEntity createdGame;

    private PlayerEntity player1;

    private PlayerEntity player2;

    @BeforeTransaction
    public void clean(){
        gameSetUpRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        gameSetUpRepository.deleteAll();
        gameRepository.deleteAll();
        playerRepository.deleteAll();


        game.setGameName("GAME1");
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
        player1=playerRepository.save(playerOne);

        playerTwo.setUsername("TwoName");
        playerTwo.setPassword("Two");
        playerTwo.setToken("Two");
        playerTwo.setStatus(PlayerStatus.ONLINE);
        player2=playerRepository.save(playerTwo);

        playerThree.setUsername("ThreeName");
        playerThree.setToken("Three");
        playerThree.setPassword("Three");
        playerThree.setStatus(PlayerStatus.ONLINE);
        playerRepository.save(playerThree);

        List<String> playerTokens=new ArrayList<>();
        playerTokens.add("One");
        playerTokens.add("Two");
        playerTokens.add("Three");


        game.setPlayerTokens(playerTokens);
        //Valid host gets already checked beforehand
        game.setHostName(player1.getUsername());
        createdGame = gameSetUpService.createGame(game);

    }

    /**Successfully create active Game*/
    @Test
    public void CreateActiveGameSuccessfullyWithoutBots() {
        ActiveGamePostDTO activeGamePostDTO =gameService.createActiveGame(createdGame.getId(), "One");
//        check if expected output matches actual output
        assertNotNull(activeGamePostDTO.getId());
        assertEquals(activeGamePostDTO.getPlayerNames().size(), 3);
        List<String> list=new ArrayList<>();
        list.add("OneName");
        list.add("TwoName");
        list.add("ThreeName");
        GameEntity createdActiveGame=gameService.getGameById(activeGamePostDTO.getId());
        assertTrue(activeGamePostDTO.getPlayerNames().containsAll(list));
        assertNotNull(createdActiveGame.getScoreboard());
//      Check if every human player is in ScoreBoard
        for (PlayerEntity player: createdActiveGame.getPlayers()) {
            assertTrue(createdActiveGame.getScoreboard().getScore().containsKey(player.getUsername()));
        }
        assertNull(createdActiveGame.getTimeStart());
        assertEquals(createdActiveGame.getAnalyzedClues(), new HashMap<String, Integer>());
        assertEquals(createdActiveGame.getValidClues(), new HashMap<String, String>());
        assertFalse(createdActiveGame.getHasBeenInitialized());
    }

    @Test
    public void CreateActiveGameSuccessfullyWithBots() {
        game.setNumberOfAngles(1L);
        game.setNumberOfDevils(1L);
        createdGame = gameSetUpService.createGame(game);

        ActiveGamePostDTO activeGamePostDTO =gameService.createActiveGame(createdGame.getId(), "One");
//        check if expected output matches actual output
        assertNotNull(activeGamePostDTO.getId());
        assertEquals(activeGamePostDTO.getPlayerNames().size(), 5);
        List<String> list=new ArrayList<>();
        list.add("OneName");
        list.add("TwoName");
        list.add("ThreeName");
        list.add("Angel_Nr_1");
        list.add("Devil_Nr_1");
        assertTrue(activeGamePostDTO.getPlayerNames().containsAll(list));
    }

    @Test
    public void NotAllPlayersAreReady() {
        List<String> playerTokens=new ArrayList<>();
        playerTokens.add("One");
        playerTokens.add("Two");

        game.setPlayerTokens(playerTokens);

        createdGame = gameSetUpService.createGame(game);

        assertThrows(ConflictException.class, ()-> gameService.createActiveGame(createdGame.getId(), "One"));
    }

    @Test
    public void createActiveGameFailsBecauseOtherPlayerThanHostTriesToStartIt() {
        assertThrows(UnauthorizedException.class, ()-> gameService.createActiveGame(createdGame.getId(), "Two"));
    }

    @Test
    public void createActiveGameFailsBecauseNoGameSetupWithSpecifiedId() {
        assertThrows(NotFoundException.class, ()-> gameService.createActiveGame(22000L, "Two"));
    }

}
