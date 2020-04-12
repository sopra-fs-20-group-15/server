
package ch.uzh.ifi.seal.soprafs20.service;


import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;
        import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PUBLIC;
        import static org.junit.jupiter.api.Assertions.*;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

        import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
        import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
        import ch.uzh.ifi.seal.soprafs20.constant.GameType;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
        import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
        import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
        import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
        import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;

        import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ActiveGamePostDTO;
import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;
        import org.mockito.MockitoAnnotations;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.beans.factory.annotation.Qualifier;
        import org.springframework.boot.test.context.SpringBootTest;
        import org.springframework.test.context.web.WebAppConfiguration;
        import org.springframework.test.web.servlet.MockMvc;
        import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
        import java.util.List;

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
    private GameService gameService;

    private GameSetUpEntity game = new GameSetUpEntity();

    private GameSetUpEntity createdGame;

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
        playerRepository.save(playerOne);

        playerTwo.setUsername("TwoName");
        playerTwo.setPassword("Two");
        playerTwo.setToken("Two");
        playerTwo.setStatus(PlayerStatus.ONLINE);
        playerRepository.save(playerTwo);

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
        game.setHostId(1L);
        createdGame = gameService.createGame(game);

    }

    /**Successfully create active Game*/
    @Test
    public void CreateActiveGameSuccessfullyWithoutBots() {
        ActiveGamePostDTO activeGamePostDTO =gameService.createActiveGame(createdGame.getId());
//        check if expected output matches actual output
        assertEquals(activeGamePostDTO.getId(),1L);
        assertEquals(activeGamePostDTO.getPlayerNames().size(), 3);
        List<String> list=new ArrayList<>();
        list.add("OneName");
        list.add("TwoName");
        list.add("ThreeName");
        assertTrue(activeGamePostDTO.getPlayerNames().containsAll(list));
    }

    @Test
    public void CreateActiveGameSuccessfullyWithBots() {
        game.setNumberOfAngles(1L);
        game.setNumberOfDevils(1L);
        gameService.createGame(game);

        ActiveGamePostDTO activeGamePostDTO =gameService.createActiveGame(createdGame.getId());
//        check if expected output matches actual output
        assertEquals(activeGamePostDTO.getId(),2L);
        assertEquals(activeGamePostDTO.getPlayerNames().size(), 5);
        List<String> list=new ArrayList<>();
        list.add("OneName");
        list.add("TwoName");
        list.add("ThreeName");
        assertTrue(activeGamePostDTO.getPlayerNames().containsAll(list));
    }

    @Test
    public void NotAllPlayersAreReady() {
        List<String> playerTokens=new ArrayList<>();
        playerTokens.add("One");
        playerTokens.add("Two");

        game.setPlayerTokens(playerTokens);

        gameService.createGame(game);

        assertThrows(ConflictException.class, ()-> gameService.createActiveGame(1L));
    }

}
