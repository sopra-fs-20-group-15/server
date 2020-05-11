package ch.uzh.ifi.seal.soprafs20.service.LogicServiceTests;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GuessPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Transactional
@WebAppConfiguration
@SpringBootTest
public class LogicServiceStatePatternTest {

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
    protected final LSSGiveClues lssGiveClues;

    //Repositories
    protected final PlayerRepository playerRepository;
    protected final GameSetUpRepository gameSetUpRepository;
    protected final GameRepository gameRepository;

    @Autowired
    public LogicServiceStatePatternTest(@Qualifier("cardService") CardService cardService, @Qualifier("logicService") LogicService logicService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService,  @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, ActiveGameService activeGameService, @Qualifier("gameRepository") GameRepository gameRepository ,@Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, @Qualifier("LSSGiveClues")LSSGiveClues lssGiveClues) {
        this.cardService = cardService;
        this.logicService = logicService;
        this.gameSetUpService = gameSetUpService;
        this.playerService = playerService;
        this.playerRepository = playerRepository;
        this.gameService = activeGameService;
        this.gameRepository = gameRepository;
        this.gameSetUpRepository = gameSetUpRepository;
        this.lssGiveClues =lssGiveClues;
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
        logicService.initializeTurn(createdActiveGame.getId());
    }

    @Test
    public void ChangesStateCorrectly() {

        logicService.setMysteryWord(createdActiveGame.getId(), 1L);
        assertEquals(createdActiveGame.getStateForLogicService(), State.GiveClues);


        CluePostDTO cluePostDTO = new CluePostDTO();
        cluePostDTO.setPlayerToken("One");
        cluePostDTO.setClue("Pancake");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);
        cluePostDTO.setPlayerToken("Three");
        cluePostDTO.setClue("Table");
        logicService.giveClue(createdActiveGame.getId(), cluePostDTO);
        assertEquals(createdActiveGame.getStateForLogicService(), State.GiveGuess);

        logicService.setGuess(createdActiveGame.getId(), "123");
        assertEquals(createdActiveGame.getStateForLogicService(), State.WordReveal);

        logicService.hasGameEnded(createdActiveGame.getId());
        assertEquals(createdActiveGame.getStateForLogicService(), State.ChooseMysteryWord);


    }
}