package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;

import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static ch.uzh.ifi.seal.soprafs20.constant.GameType.PRIVATE;

/**
 * PlayerEntity Service

 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class GodService {

    private final Logger log = LoggerFactory.getLogger(GameSetUpService.class);

    private final GameSetUpService gameSetUpService;
    private final LogicService logicService;
    private final PlayerService playerService;
    private final CardService cardService;
    private final PlayerRepository playerRepository;
    private final ActiveGameService activeGameService;

    @Autowired
    public GodService(@Qualifier("cardService") CardService cardService, @Qualifier("gameSetUpService") GameSetUpService gameSetUpService, @Qualifier("logicService") LogicService logicService, @Qualifier("playerService") PlayerService playerService, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("activeGameService") ActiveGameService activeGameService) {
        this.cardService = cardService;
        this.logicService = logicService;
        this.gameSetUpService = gameSetUpService;
        this.playerService = playerService;
        this.playerRepository = playerRepository;
        this.activeGameService = activeGameService;
    }


    public GodDTO createPlayerAndGame(){
        GameSetUpEntity game = new GameSetUpEntity();

        GameSetUpEntity createdGame;
        GameEntity  createdActiveGame;

        PlayerEntity p1;
        PlayerEntity p2;
        PlayerEntity p3;

        game.setNumberOfPlayers(5L);
        game.setNumberOfAngles(1L);
        game.setNumberOfDevils(1L);
        game.setGameType(PRIVATE);
        game.setPassword("Cara");
        PlayerEntity playerOne= new PlayerEntity();
        PlayerEntity playerTwo= new PlayerEntity();
        PlayerEntity playerThree= new PlayerEntity();

        playerOne.setUsername("OneName");
        playerOne.setPassword("One");
        playerOne.setToken("One");
        playerOne.setStatus(PlayerStatus.OFFLINE);
        p1=playerRepository.save(playerOne);
        playerService.loginUser(p1);

        playerTwo.setUsername("TwoName");
        playerTwo.setPassword("Two");
        playerTwo.setToken("Two");
        playerTwo.setStatus(PlayerStatus.OFFLINE);
        p2=playerRepository.save(playerTwo);
        playerService.loginUser(p2);

        playerThree.setUsername("ThreeName");
        playerThree.setToken("Three");
        playerThree.setPassword("Three");
        playerThree.setStatus(PlayerStatus.OFFLINE);
        p3=playerRepository.save(playerThree);
        playerService.loginUser(p3);

        List<String> playerTokens=new ArrayList<>();
        playerTokens.add("One");
        playerTokens.add("Two");
        playerTokens.add("Three");


        game.setPlayerTokens(playerTokens);

        game.setHostName(p1.getUsername());
        game.setGameName("GameName");

        createdGame = gameSetUpService.createGame(game);
        createdActiveGame = activeGameService.getGameById(activeGameService.createActiveGame(createdGame.getId(), "One").getId());

        GodDTO godDTO = new GodDTO();
        godDTO.setGameId(createdActiveGame.getId());
        godDTO.setPlayerToken1(p1.getToken());
        godDTO.setPlayerToken2(p2.getToken());
        godDTO.setPlayerToken3(p3.getToken());
        return  godDTO;

    }
}
