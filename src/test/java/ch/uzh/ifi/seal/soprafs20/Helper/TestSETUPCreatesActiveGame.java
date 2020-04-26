package ch.uzh.ifi.seal.soprafs20.Helper;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.service.ActiveGameService;
import ch.uzh.ifi.seal.soprafs20.service.GameSetUpService;
import ch.uzh.ifi.seal.soprafs20.service.LogicService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.BeforeTransaction;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;

public class TestSETUPCreatesActiveGame {
    @Qualifier("playerRepository")
    @Autowired
    protected PlayerRepository playerRepository;
    protected GameEntity createdActiveGame;
    protected PlayerEntity p2;
    @Autowired
    protected ActiveGameService gameService;
    @Autowired
    protected GameSetUpService gameSetUpService;
    protected GameSetUpEntity game = new GameSetUpEntity();
    protected GameSetUpEntity createdGame;
    protected PlayerEntity p1;
    protected PlayerEntity p3;

    @Autowired
    protected GameSetUpRepository gameSetUpRepository;

    @Qualifier("gameRepository")
    @Autowired
    protected GameRepository gameRepository;

    @Autowired
    protected LogicService logicService;

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

        createdGame =gameSetUpService.createGame(game);

        createdActiveGame =gameService.getGameById(gameService.createActiveGame(createdGame.getId(), "One").getId());
        createdActiveGame.setActiveMysteryWord("RandomMysteryWord");
        createdActiveGame.setTimeStart(123L);
    }
}
