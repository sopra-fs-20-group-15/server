package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.PlayerNotAvailable;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import ch.uzh.ifi.seal.soprafs20.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationServiceTest {
    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private PlayerService playerService;
    private GameService gameService;
    private PlayerEntity testPlayer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testPlayer = new PlayerEntity();

        // when -> any object is being save in the playerRepository -> return the dummy testPlayer
        Mockito.when(playerRepository.save(Mockito.any())).thenReturn(testPlayer);
    }

    /**Tests for boolean checkPlayerIsActivePlayerOfGame()*/
    /**Successful: Player is Active Player of Game*/
    @Test
    public void playerIsActivePlayerOfGame() {
        ValidationService validationService = new ValidationService(gameRepository,playerRepository);
        // DummyObjects that will be given back by Mockito from the Repositories
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        GameEntity game = new GameEntity();
        game.setActivePlayerId(1L);
        Optional<GameEntity> gameOp = Optional.of(game);
        //Mocks the functions validationService needs
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(player);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(gameOp);

        //Test itself
        boolean evaluation = validationService.checkPlayerIsActivePlayerOfGame("ab", 0L);
        assertEquals(true, evaluation);

    }

    /**Error: Unauthorized; Player is not part of the game*/
    @Test
    public void playerIsNotActivePlayerOfGame() {
        ValidationService validationService = new ValidationService(gameRepository,playerRepository);
        // DummyObjects that will be given back by Mockito from the Repositories
        PlayerEntity player = new PlayerEntity();
        player.setId(2L);
        GameEntity game = new GameEntity();
        game.setActivePlayerId(1L);
        Optional<GameEntity> gameOp = Optional.of(game);
        //Mocks the functions validationService needs
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(player);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(gameOp);

        //Test itself
        assertThrows(UnauthorizedException.class, () -> validationService.checkPlayerIsActivePlayerOfGame("ab", 0L));
    }

    /**Error: NotFound; Player does not exist*/
    @Test
    public void playerDoesNotExistActivePlayer() {
        ValidationService validationService = new ValidationService(gameRepository,playerRepository);
        // DummyObjects that will be given back by Mockito from the Repositories
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        GameEntity game = new GameEntity();
        List<Long> passivePlayers = new ArrayList<Long>();
        game.setPassivePlayerIds(passivePlayers);
        Optional<GameEntity> gameOp = Optional.of(game);
        //Mocks the functions validationService needs
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenThrow(PlayerNotAvailable.class);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(gameOp);

        //Test itself
        assertThrows(PlayerNotAvailable.class, () -> validationService.checkPlayerIsActivePlayerOfGame("ab", 0L));
    }

    /**Error: NotFound; Player does not exist*/
    @Test
    public void gameDoesNotExistActivePlayer() {
        ValidationService validationService = new ValidationService(gameRepository,playerRepository);
        // DummyObjects that will be given back by Mockito from the Repositories
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        GameEntity game = null;
        Optional<GameEntity> gameOp = Optional.ofNullable(game);
        //Mocks the functions validationService needs
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(player);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(gameOp);

        //Test itself
        assertThrows(NotFoundException.class, () -> validationService.checkPlayerIsActivePlayerOfGame("ab", 0L));
    }

    /**Tests for boolean checkPlayerIsPassivePlayerOfGame(String playerToken, Long gameId)*/
    @Test
    public void playerIsPassivePlayerOfGame() {
        ValidationService validationService = new ValidationService(gameRepository,playerRepository);
        // DummyObjects that will be given back by Mockito from the Repositories
        PlayerEntity player = new PlayerEntity();
        GameEntity game = new GameEntity();
        List<Long> passivePlayers = new ArrayList<Long>();
        passivePlayers.add(player.getId());
        game.setPassivePlayerIds(passivePlayers);
        Optional<GameEntity> gameOp = Optional.of(game);
        //Mocks the functions validationService needs
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(player);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(gameOp);

        //Test itself
        boolean evaluation = validationService.checkPlayerIsPassivePlayerOfGame("ab", 0L);
        assertEquals(true, evaluation);

    }

    /**Error: Unauthorized; Player is not part of the game*/
    @Test
    public void playerIsNotPassivePlayerOfGame() {
        ValidationService validationService = new ValidationService(gameRepository,playerRepository);
        // DummyObjects that will be given back by Mockito from the Repositories
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        GameEntity game = new GameEntity();
        List<Long> passivePlayers = new ArrayList<Long>();
        PlayerEntity player2 = new PlayerEntity();
        player2.setId(5L);
        passivePlayers.add(5L);
        game.setPassivePlayerIds(passivePlayers);
        Optional<GameEntity> gameOp = Optional.of(game);
        //Mocks the functions validationService needs
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(player);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(gameOp);

        //Test itself
        assertThrows(UnauthorizedException.class, () -> validationService.checkPlayerIsPassivePlayerOfGame("ab", 0L));
    }

    /**Error: NotFound; Player does not exist*/
    @Test
    public void playerDoesNotExistPassive() {
        ValidationService validationService = new ValidationService(gameRepository,playerRepository);
        // DummyObjects that will be given back by Mockito from the Repositories
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        GameEntity game = new GameEntity();
        List<Long> passivePlayers = new ArrayList<Long>();
        game.setPassivePlayerIds(passivePlayers);
        Optional<GameEntity> gameOp = Optional.of(game);
        //Mocks the functions validationService needs
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenThrow(PlayerNotAvailable.class);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(gameOp);

        //Test itself
        assertThrows(PlayerNotAvailable.class, () -> validationService.checkPlayerIsPassivePlayerOfGame("ab", 0L));
    }

    /**Error: NotFound; Player does not exist*/
    @Test
    public void gameDoesNotExistPassive() {
        ValidationService validationService = new ValidationService(gameRepository,playerRepository);
        // DummyObjects that will be given back by Mockito from the Repositories
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        GameEntity game = null;
        Optional<GameEntity> gameOp = Optional.ofNullable(game);
        //Mocks the functions validationService needs
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(player);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(gameOp);

        //Test itself
        assertThrows(NotFoundException.class, () -> validationService.checkPlayerIsPassivePlayerOfGame("ab", 0L));
    }

    /**Tests for boolean checkPlayerIsPartOfGame(String playerToken, Long gameId)*/
    /**Successful: Player is Part of Game*/
    @Test
    public void playerIsPartOfGame() {
        ValidationService validationService = new ValidationService(gameRepository,playerRepository);
        // DummyObjects that will be given back by Mockito from the Repositories
        PlayerEntity player = new PlayerEntity();
        GameEntity game = new GameEntity();
        List<Long> passivePlayers = new ArrayList<Long>();
        passivePlayers.add(player.getId());
        game.setPassivePlayerIds(passivePlayers);
        Optional<GameEntity> gameOp = Optional.of(game);
        //Mocks the functions validationService needs
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(player);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(gameOp);

        //Test itself
        boolean evaluation = validationService.checkPlayerIsPartOfGame("ab", 0L);
        assertEquals(true, evaluation);

    }

    /**Error: Unauthorized; Player is not part of the game*/
    @Test
    public void playerIsNotPartOfGame() {
        ValidationService validationService = new ValidationService(gameRepository,playerRepository);
        // DummyObjects that will be given back by Mockito from the Repositories
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        GameEntity game = new GameEntity();
        List<Long> passivePlayers = new ArrayList<Long>();
        game.setPassivePlayerIds(passivePlayers);
        Optional<GameEntity> gameOp = Optional.of(game);
        //Mocks the functions validationService needs
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(player);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(gameOp);

        //Test itself
        assertThrows(UnauthorizedException.class, () -> validationService.checkPlayerIsPartOfGame("ab", 0L));
    }

    /**Error: NotFound; Player does not exist*/
    @Test
    public void playerDoesNotExist() {
        ValidationService validationService = new ValidationService(gameRepository,playerRepository);
        // DummyObjects that will be given back by Mockito from the Repositories
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        GameEntity game = new GameEntity();
        List<Long> passivePlayers = new ArrayList<Long>();
        game.setPassivePlayerIds(passivePlayers);
        Optional<GameEntity> gameOp = Optional.of(game);
        //Mocks the functions validationService needs
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenThrow(PlayerNotAvailable.class);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(gameOp);

        //Test itself
        assertThrows(PlayerNotAvailable.class, () -> validationService.checkPlayerIsPartOfGame("ab", 0L));
    }

    /**Error: NotFound; Player does not exist*/
    @Test
    public void gameDoesNotExist() {
        ValidationService validationService = new ValidationService(gameRepository,playerRepository);
        // DummyObjects that will be given back by Mockito from the Repositories
        PlayerEntity player = new PlayerEntity();
        player.setId(1L);
        GameEntity game = null;
        Optional<GameEntity> gameOp = Optional.ofNullable(game);
        //Mocks the functions validationService needs
        Mockito.when(playerRepository.findByToken(Mockito.any())).thenReturn(player);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(gameOp);

        //Test itself
        assertThrows(NotFoundException.class, () -> validationService.checkPlayerIsPartOfGame("ab", 0L));
    }

}
