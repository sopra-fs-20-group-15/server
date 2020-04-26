package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.service.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Long.parseLong;

/**
 * Is responsible for creating, deleting and updating a Game Setup.
 */
@RestController
public class GameSetUpController {

    private final PlayerService playerService;
    private final GameSetUpService gameService;


    GameSetUpController(PlayerService playerService, GameSetUpService gameService) {
        this.playerService = playerService;
        this.gameService = gameService;
    }

    protected boolean stringIsALong(String str) {
        try {
            parseLong(str);
        } catch(NumberFormatException e) {
            throw new ConflictException("The PathVariable should be a long!");
        } catch(NullPointerException e) {
            throw new ConflictException("The PathVariable should be a long!");
        }
        return true;
    }

    /**Creates a game setUp
     * @Param: GamePostDTO:
     * @Returns:
     * @Throws:
     *
     *
     *
     *
     *
     * */
    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CreatedGameSetUpDTO createGameSetUp(@RequestBody GamePostDTO gamePostDTO) {
        //Check that Player actually exists
        PlayerEntity player = playerService.getPlayerByToken(gamePostDTO.getPlayerToken());
        GameSetUpEntity game = DTOMapper.INSTANCE.convertGameSetUpPostDTOtoEntity(gamePostDTO);
        game.setHostName(player.getUsername());
        List<String> playerTokens = new ArrayList<String>();
        playerTokens.add(player.getToken());
        game.setPlayerTokens(playerTokens);
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

    /**Allows player to get an overview of the existing game lobbies*/
    @GetMapping("/games/lobbies")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LobbyOverviewGetDTO> getLobbies() {
        return gameService.getLobbies();
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


}
