package ch.uzh.ifi.seal.soprafs20.service;


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
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
@SpringBootTest
public class GameServiceIntegrationGetLobbyTest {


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

    private PlayerEntity player1;

    private PlayerEntity player2;

    @BeforeTransaction
    public void clean(){
        gameRepository.deleteAll();
        gameSetUpRepository.deleteAll();
        playerRepository.deleteAll();
        game.setGameName("GAME1");
        game.setNumberOfPlayers(4L);
        game.setNumberOfAngles(1L);
        game.setNumberOfDevils(1L);
        game.setGameType(PRIVATE);
        game.setPassword("Cara");
        PlayerEntity playerOne= new PlayerEntity();
        PlayerEntity playerTwo= new PlayerEntity();
        PlayerEntity playerThree= new PlayerEntity();
        PlayerEntity playerFour=new PlayerEntity();

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

        playerFour.setUsername("FourName");
        playerFour.setToken("Four");
        playerFour.setPassword("Four");
        playerFour.setStatus(PlayerStatus.ONLINE);
        playerRepository.save(playerFour);

        List<String> playerTokens=new ArrayList<>();
        playerTokens.add("One");
        playerTokens.add("Two");
        playerTokens.add("Three");


        game.setPlayerTokens(playerTokens);
        //Valid host gets already checked beforehand
        game.setHostName(player1.getUsername());
        createdGame=gameSetUpRepository.save(game);
    }


    /**Successfully retrieves info from Lobby*/
    @Test
    public void getLobbyInfoWorks() {
        ActiveGamePostDTO activeGamePostDTO=gameService.createActiveGame(createdGame.getId(),"One");
        LobbyGetDTO lobbyGetDTO=gameService.getLobbyInfo(createdGame.getId(),"One");

        assertEquals(createdGame.getGameName(),lobbyGetDTO.getGameName());
        assertEquals(3,lobbyGetDTO.getNumOfActualPlayers());
        assertEquals(4,lobbyGetDTO.getNumOfDesiredPlayers());
        assertEquals(createdGame.getId(),lobbyGetDTO.getGameSetUpId());
        assertEquals(createdGame.getNumberOfAngles(),lobbyGetDTO.getNumOfAngels());
        assertEquals(createdGame.getNumberOfDevils(),lobbyGetDTO.getNumOfDevils());
        assertTrue(lobbyGetDTO.getPlayerNames().contains("Angel_Nr_1"));
        assertTrue(lobbyGetDTO.getPlayerNames().contains("Devil_Nr_1"));
        assertTrue(lobbyGetDTO.getPlayerNames().contains("OneName"));
        assertTrue(lobbyGetDTO.getPlayerNames().contains("TwoName"));
        assertTrue(lobbyGetDTO.getPlayerNames().contains("ThreeName"));
        assertEquals(lobbyGetDTO.getActiveGameId(), activeGamePostDTO.getId());
        assertEquals(5, lobbyGetDTO.getPlayerNames().size());
    }

    /**Unsuccessful getLobbyInfo calls*/
    @Test
    public void getLobbyFailsBecausePlayerNotInLobby() {
        assertThrows(UnauthorizedException.class, ()-> gameService.getLobbyInfo(game.getId(),"Four"));
    }

    @Test
    public void getLobbyFailsBecausePlayerDoesNotExist() {
        assertThrows(NotFoundException.class, ()-> gameService.getLobbyInfo(game.getId(),"Five"));
    }

    @Test
    public void getLobbyFailsBecauseGameSetUpIdDoesNotCorrespondToExistingLobby() {
        assertThrows(NotFoundException.class, ()-> gameService.getLobbyInfo(35454L,"Three"));
    }

}