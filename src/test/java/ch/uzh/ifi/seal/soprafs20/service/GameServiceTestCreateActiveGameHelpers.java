package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ActiveGamePostDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@WebAppConfiguration
@SpringBootTest
public class GameServiceTestCreateActiveGameHelpers{

    @Autowired
    private ActiveGameService gameService;

    @MockBean
    private PlayerService playerService;

    GameEntity game = new GameEntity();


    /**Tests for addPlayers*/
    @Test
    public void GameServiceAddHumanPlayers() {
        //Create three players
        List<String> playerTokens = new ArrayList<>();
        playerTokens.add("p1Token");
        playerTokens.add("p2Token");
        playerTokens.add("p3Token");
        String hostToken = ("p3Token");
        PlayerEntity p1 = new PlayerEntity();
        p1.setId(1L);
        PlayerEntity p2 = new PlayerEntity();
        p2.setId(2L);
        PlayerEntity p3 = new PlayerEntity();
        p3.setId(3L);

        //Add them to the game
        given(playerService.getPlayerByToken(Mockito.any())).willReturn(p1, p2, p3, p3);
        gameService.addHumanPlayers(game, playerTokens, hostToken);

        //Are there all three players in Players?
        assertTrue(game.getPlayers().size() == 3);
        //Is the active player correct?
        assertTrue(game.getActivePlayerId() == 3L);
        //Are the passive players correct?
        assertTrue(game.getPassivePlayerIds().contains(1L));
        assertTrue(game.getPassivePlayerIds().contains(2L));
        assertFalse(game.getPassivePlayerIds().contains(3L));

    }

    /**Tests for addCards -> Are already tested in CardService*/

    /**Tests for furtherInitialize*/
    @Test
    public void GameServiceFurtherInitialize(){
        List<PlayerEntity> players = new ArrayList<PlayerEntity>();
        game.setPlayers(players);

        gameService.furtherInitialize(game);

        assertFalse(game.getHasBeenInitialized());
        assertFalse(game.getHasEnded());
        assertFalse(game.getValidCluesAreSet());
        assertNotNull(game.getClueMap());
        assertNotNull(game.getScoreboard());
        assertNotNull(game.getValidClues());
        assertNotNull(game.getAnalyzedClues());
    }

    /**Tests for createActiveGamePostDTO*/
    @Test
    public void GameServiceCreateActiveGamePostDTO(){
        //Set important game Information for frontend
        game.setId(1L);
        List<PlayerEntity> players = new ArrayList<PlayerEntity>();
        PlayerEntity p1 = new PlayerEntity();
        p1.setId(1L);
        p1.setUsername("1");
        PlayerEntity p2 = new PlayerEntity();
        p2.setId(2L);
        p2.setUsername("2");
        PlayerEntity p3 = new PlayerEntity();
        p3.setId(3L);
        p3.setUsername("3");
        players.add(p1);
        players.add(p2);
        players.add(p3);

        game.setPlayers(players);
        Angel angel1 = new Angel();
        angel1.setName("A1");
        List<Angel> angels = new ArrayList<Angel>();
        angels.add(angel1);
        game.setAngels(angels);
        game.setDevils(new ArrayList<Devil>());

        ActiveGamePostDTO activeGamePostDTO = gameService.createActiveGamePostDTO(game);

        assertEquals(activeGamePostDTO.getId(), game.getId());
        assertEquals(activeGamePostDTO.getPlayerNames().size(), 4);
        assertTrue(activeGamePostDTO.getPlayerNames().contains("1"));
        assertTrue(activeGamePostDTO.getPlayerNames().contains("2"));
        assertTrue(activeGamePostDTO.getPlayerNames().contains("3"));
        assertTrue(activeGamePostDTO.getPlayerNames().contains("A1"));

    }
}
