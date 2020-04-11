package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.CardService;
import ch.uzh.ifi.seal.soprafs20.GameLogic.GameService;
import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.exceptions.PlayerNotAvailable;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ActiveGamePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CardPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.WordPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import static java.lang.Long.parseLong;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the PlayerService and finally return the result.
 */
@RestController
public class GamesController {

    private final GameService gameService;
    private final PlayerService playerService;

    GamesController(GameService gameService, PlayerService playerService) {
        this.gameService = gameService;
        this.playerService = playerService;
    }

    protected boolean stringIsALong(String str) {
        try {
            parseLong(str);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    /**Creates a game*/
    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GamePostDTO createGame(@RequestBody GamePostDTO gamePostDTO) {
        //Check that Player actually exists
        PlayerEntity player = playerService.getPlayerByToken(gamePostDTO.getPlayerToken());
        GameSetUpEntity game = DTOMapper.INSTANCE.convertGameSetUpPostDTOtoEntity(gamePostDTO);
        game.setHostId(player.getId());
        //Try to create Game
        GameSetUpEntity newGame = gameService.createGame(game);
        GamePostDTO gamePostDTOReturn = DTOMapper.INSTANCE.convertEntityToGameSetUpPostDTO(newGame);
        return gamePostDTOReturn;
    }

    /**Creates an active game*/
    @PostMapping("/games/{gameSetUpId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ActiveGamePostDTO createActiveGame(@PathVariable String gameSetUpId) {
        //Check that SetupEntity actually exists
        if (stringIsALong(gameSetUpId)){
            //Try to create active game
            Long gsId = parseLong(gameSetUpId);
            return gameService.createActiveGame(gsId);
        }
        else throw new BadRequestException("Game-Setup-ID has wrong format!");
    }
}
