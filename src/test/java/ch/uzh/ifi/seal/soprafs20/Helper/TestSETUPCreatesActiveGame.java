package ch.uzh.ifi.seal.soprafs20.Helper;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.service.*;
import ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.BeforeTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;

public class TestSETUPCreatesActiveGame {
    protected GameSetUpEntity game = new GameSetUpEntity();

    protected GameSetUpEntity createdGame;
    protected GameEntity  createdActiveGame;

    protected PlayerEntity p1;
    protected PlayerEntity p2;
    protected PlayerEntity p3;

    //Services
    protected final GameSetUpService gameSetUpService;
    protected final LogicService logicService;
    protected final PlayerService playerService;
    protected final CardService cardService;
    protected final ActiveGameService gameService;

    //Helpers for LogicService
    protected HashMap<State, LogicServiceState> possibleStates = new HashMap<>();

    //Repositories
    protected final PlayerRepository playerRepository;
    protected final GameSetUpRepository gameSetUpRepository;
    protected final GameRepository gameRepository;

    @Autowired
    public TestSETUPCreatesActiveGame(@Qualifier("cardService") CardService cardService, @Qualifier("logicService") LogicService logicService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService, @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, ActiveGameService activeGameService, @Qualifier("gameRepository") GameRepository gameRepository , @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, LSStateChooseMysteryWord lsStateChooseMysteryWord, LSSGiveClues lssGiveClues, LSSGiveGuess lssGiveGuess, LSSWordReveal lssWordReveal, LSSGameHasEnded lssGameHasEnded) {
        this.cardService = cardService;
        this.logicService = logicService;
        this.gameSetUpService = gameSetUpService;
        this.playerService = playerService;
        this.playerRepository = playerRepository;
        this.gameService = activeGameService;
        this.gameRepository = gameRepository;
        this.gameSetUpRepository = gameSetUpRepository;
        this.possibleStates.put(State.ChooseMysteryWord, lsStateChooseMysteryWord);
        this.possibleStates.put(State.GiveClues, lssGiveClues);
        this.possibleStates.put(State.GiveGuess, lssGiveGuess);
        this.possibleStates.put(State.WordReveal, lssWordReveal);
        this.possibleStates.put(State.hasEnded, lssGameHasEnded);
    }

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
        createdActiveGame.setStateForLogicService(State.GiveClues);
    }
}
