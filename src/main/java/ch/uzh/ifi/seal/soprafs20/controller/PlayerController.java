package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * PlayerEntity Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the PlayerService and finally return the result.
 */
@RestController
public class PlayerController {

    private final PlayerService playerService;

    PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /** Creates a player
     * @Param: PlayerPostDTO: String username, String password,
     * @Returns: PlayerGetDTO: Long id, String username, PlayerStatus status,
     * @Throws: 409: The username exists already
     * @Throws: 422: Unprocessable Entity; username or password is empty
     * */
    @PostMapping("/players")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PlayerGetDTO createUser(@RequestBody PlayerPostDTO playerPostDTO) {
        // convert API user to internal representation
        PlayerEntity playerEntityInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(playerPostDTO);

        // create user
        PlayerEntity createdPlayerEntity = playerService.createUser(playerEntityInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdPlayerEntity);
    }

    /** Let a player login
     * @Param: PlayerPutDTO: String username, String password,
     * @Returns: PlayerGetDTO: Long id, String username, PlayerStatus status,
     * @Throws: 401: the password is not correct for this player
     * @Throws: 404: the player does not exist
     * */
    @PutMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerTokenDTO loginUser(@RequestBody PlayerPutDTO playerPutDTO){
        PlayerEntity playerEntityInput =DTOMapper.INSTANCE.convertUserPutDTOtoEntity(playerPutDTO);
        return DTOMapper.INSTANCE.convertEntityToUserTokenDTO(playerService.loginUser(playerEntityInput)) ;
    }

    /** Let a player logout
     * @Param: PlayerTokenDTO: String token, Long id;
     * @Throws: 404: the player does not exist
     * @Throws: 409: The player is currently playing a game and cannot logout
     * */
    @PutMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void logoutUser(@RequestBody PlayerTokenDTO playerTokenDTO){
        PlayerEntity playerEntityInput = DTOMapper.INSTANCE.convertUserTokenDTOToEntity(playerTokenDTO);
        playerService.logOutUser(playerEntityInput);
    }

    /** Gets a list of all players that have registered an account
     * @Returns: List<PlayerGetDTO>: Long id, String username, PlayerStatus status,
     * */
    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerGetDTO> getAllUsers() {
        // fetch all playerEntities in the internal representation
        List<PlayerEntity> playerEntities = playerService.getUsers();
        List<PlayerGetDTO> playerGetDTOS = new ArrayList<>();

        // convert each user to the API representation
        for (PlayerEntity playerEntity : playerEntities) {
            playerGetDTOS.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(playerEntity));
        }
        return playerGetDTOS;
    }

    /** Gets a list of all players that have registered an account
     * @Param: String playerId; pathVariable
     * @Returns: PlayerGetDTO: Long id, String username, PlayerStatus status,
     * @Throws: 404: no player with this id exists
     * */
    @GetMapping("/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerGetDTO getUser(@PathVariable String playerId) {
        PlayerEntity playerEntityInput = DTOMapper.INSTANCE.convertUserIdStringToEntity(playerId);
        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(playerService.getPlayerById(playerEntityInput.getId()));
        return playerGetDTO;
    }

}
