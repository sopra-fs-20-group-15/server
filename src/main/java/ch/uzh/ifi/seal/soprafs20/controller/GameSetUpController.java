package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.Entities.ChatEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.service.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Long.parseLong;

/**
 * GameSetUpController is responsible for creating, deleting and updating a Game Setup. Lets also players join and leave a game setup.
 */
@RestController
public class GameSetUpController {

    private final PlayerService playerService;
    private final GameSetUpService gameService; 



    GameSetUpController(PlayerService playerService, GameSetUpService gameService ){
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
     * @Param: GamePostDTO: Long gameId, String gameName, Long numberOfPlayers, Long numberOfAngles, Long numberOfDevils, GameType gameType, String password, String playerToken,
     * @Returns: CreatedGameSetUpDTP:  Long gameId, String gameName, Long numberOfPlayers, Long numberOfAngles, Long numberOfDevils, GameType gameType, String hostName
     * @Throws: 404: playerToken does not exist
      * @Throws: 409: total player number not between 3 and 7
      * @Throws: 409: bot number below zero or bigger than total player number -1
      * @Throws: 409: no empty game names
      * @Throws: 409: If its a private game; the password should not be empty
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

    /**Deletes a gameSetUp (only Host)
     * @Param: TokenDTO: String playerToken
     * @Returns: void
     * @Throws: 404: gameId does not exist
     * @Throws: 404: playerToken does not exist
     * @Throws: 401: Only the game host is allowed to delete a game setup
     * */
    @DeleteMapping("/gameSetUps/{gameSetUpId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void deleteGameSetUp(@RequestBody TokenDTO TokenDTO, @PathVariable String gameSetUpId) {
        //Check that Player actually exists
        PlayerEntity player = playerService.getPlayerByToken(TokenDTO.getPlayerToken());
        stringIsALong(gameSetUpId);
        Long gameSetUpIdLong = parseLong(gameSetUpId);
        gameService.deleteGameSetUpEntity(gameSetUpIdLong, player);
    }


    /**Lets a player join a GameSetUp
     * @Param: PlayerIntoGameSetUpDTO: String playerToken, String password (empty if public, password if private),
     * @Returns: void
     * @Throws: 204: the player is already part of that game
     * @Throws: 401: Private game: password is wrong
     * @Throws: 404: gameId does not exist
     * @Throws: 404: playerToken does not exist
     * @Throws: 409: game is full
     * */

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

    /**Lets a player leave a GameSetUp
     * @Param: TokenDTO: String playerToken
     * @Returns: void
     * @Throws: 204: Host wants to leave -> delete game instead
     * @Throws: 404: No game with this gameId
     * @Throws: 404: The player has not joined this gameSetUp yet
     * @Throws: 409: gameId is not a long*/


    @PutMapping("/games/{gameId}/lobbies/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void putPlayerBackIntoLobbyFromGameSetUp(@PathVariable String gameId, @RequestBody TokenDTO tokenDTO) {
        //Check that Player actually exists
        PlayerEntity player = playerService.getPlayerByToken(tokenDTO.getPlayerToken());
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        gameService.removePlayerFromGame(gameIdLong, player);
    }

    /**Allows player to get an overview of the existing game lobbies
     * @Param: void
     * @Returns: List<LobbyOverviewGetDTO>: LobbyOverviewGetDTO: private String gameName, GameType gameType, Long numOfDesiredPlayers, Long numOfAngels, Long numOfDevils, Long numOfHumanPlayers,
     */
    @GetMapping("/games/lobbies")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LobbyOverviewGetDTO> getLobbies() {
        return gameService.getLobbies();
    }


    /**Allows player to refresh Lobby status while in one
     * @Param: String gameSetUpId (since PathVariable), String playerToken
     * @Returns: @Returns: LobbyGetDTO: private Long activeGameId, Long gameSetUpId, String gameName, String hostName, List<String> playerNames, Long numOfDesiredPlayers, Long numOfHumanPlayers, Long numOfAngels, Long numOfDevils;
     * @Throws: 401: The player is not part of the lobby
     * @Throws: 409: PathVariable gameSetUpId is not a long
     * */
    @GetMapping("/games/lobbies/{gameSetUpId}/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO getLobbyInfo(@PathVariable String gameSetUpId, @PathVariable String playerToken) {
        //Check that SetupEntity actually exists
        stringIsALong(gameSetUpId);
            //Try to create active game
            Long gsId = parseLong(gameSetUpId);
            //add cards to repository
            return gameService.getLobbyInfo(gsId, playerToken);
    }
}
