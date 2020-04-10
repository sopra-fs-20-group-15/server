package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.CardService;
import ch.uzh.ifi.seal.soprafs20.GameLogic.GameService;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.exceptions.PlayerNotAvailable;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CardPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.WordPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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


}
