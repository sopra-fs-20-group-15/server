package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.service.CardService;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static java.lang.Long.parseLong;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the PlayerService and finally return the result.
 */
@RestController
public class GamesController {

    private final GameService gameService;
    private final CardService cardService;
    private final PlayerService playerService;

    GamesController(GameService gameService, PlayerService playerService, CardService cardService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.cardService = cardService;
    }

    protected boolean stringIsALong(String str) {
        try {
            parseLong(str);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    /**Creates a game setUp*/
    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CreatedGameSetUpDTO createGameSetUp(@RequestBody GamePostDTO gamePostDTO) {
        //Check that Player actually exists
        PlayerEntity player = playerService.getPlayerByToken(gamePostDTO.getPlayerToken());
        GameSetUpEntity game = DTOMapper.INSTANCE.convertGameSetUpPostDTOtoEntity(gamePostDTO);
        game.setHostName(player.getUsername());
        //Try to create Game
        GameSetUpEntity newGame = gameService.createGame(game);
        CreatedGameSetUpDTO gamePostDTOReturn = DTOMapper.INSTANCE.convertEntityToGameSetUpPostDTO(newGame);
        return gamePostDTOReturn;
    }

    /**Deletes a gameSetUp (only Host)*/

    @DeleteMapping("/gameSetUps/{gameSetUpId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void deleteGameSetUp(@RequestBody String playerToken, @PathVariable String gameSetUpId) {
        //Check that Player actually exists
        PlayerEntity player = playerService.getPlayerByToken(playerToken);
        stringIsALong(gameSetUpId);
        Long gameSetUpIdLong = parseLong(gameSetUpId);
        gameService.deleteGameSetUpEntity(gameSetUpIdLong, player);
    }
    /**Lets a player join a GameSetUp*/

    @PutMapping("/games/{gameId}/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void putPlayerIntoGameSetUp(@PathVariable String gameId, @RequestBody PlayerIntoGameSetUpDTO playerIntoGameSetUpDTO) {
        //Check that Player actually exists
        PlayerEntity player = playerService.getPlayerByToken(playerIntoGameSetUpDTO.getPlayerToken());
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        gameService.putPlayerIntoGame(gameIdLong, player, playerIntoGameSetUpDTO.getPassword());
    }

    /**Lets a player leave a GameSetUp*/

    @DeleteMapping("/games/{gameId}/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void DeletePlayerFromGameSetUp(@PathVariable String gameId, @RequestBody PlayerTokenDTO playerTokenDTO) {
        //Check that Player actually exists
        PlayerEntity player = playerService.getPlayerByToken(playerTokenDTO.getToken());
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        gameService.removePlayerFromGame(gameIdLong, player);
    }

    /**Allows player to refresh Lobby status while in one*/
    @GetMapping("/games/lobbies/{gameSetUpId}/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO getLobbyInfo(@PathVariable String gameSetUpId, @PathVariable String playerToken) {
        //Check that SetupEntity actually exists
        if (stringIsALong(gameSetUpId)){
            //Try to create active game
            Long gsId = parseLong(gameSetUpId);
            //add cards to repository
            return gameService.getLobbyInfo(gsId, playerToken);
        }
        else throw new BadRequestException("Game-Setup-ID has wrong format!");
    }

    /**Creates an active game*/
    @PostMapping("/games/{gameSetUpId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ActiveGamePostDTO createActiveGame(@PathVariable String gameSetUpId, @RequestBody PlayerTokenDTO playerTokenDTO) {
        //Check that SetupEntity actually exists
        if (stringIsALong(gameSetUpId)){
            //Try to create active game
            Long gsId = parseLong(gameSetUpId);
            //add cards to repository
            try {cardService.addAllCards();
            } catch (IOException ex) {
                throw new NoContentException("The CardDatabase couldn't be filled");
            }
            return gameService.createActiveGame(gsId, playerTokenDTO.getToken());
        }
        else throw new BadRequestException("Game-Setup-ID has wrong format!");
    }


}
