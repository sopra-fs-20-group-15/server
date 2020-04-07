package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.GameLogic.Player;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Player Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the PlayerService and finally return the result.
 */
@RestController
public class PlayerController {

    private final PlayerService playerService;

    PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerGetDTO> getAllUsers() {
        // fetch all players in the internal representation
        List<Player> players = playerService.getUsers();
        List<PlayerGetDTO> playerGetDTOS = new ArrayList<>();

        // convert each user to the API representation
        for (Player player : players) {
            playerGetDTOS.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(player));
        }
        return playerGetDTOS;
    }

    @GetMapping("/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerGetDTO getUser(@PathVariable String playerId) {
        Player playerInput = DTOMapper.INSTANCE.convertUserIdStringToEntity(playerId);
        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(playerService.getUser(playerInput));
        return playerGetDTO;
    }

    @PostMapping("/players")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PlayerGetDTO createUser(@RequestBody PlayerPostDTO playerPostDTO) {
        // convert API user to internal representation
        Player playerInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(playerPostDTO);

        // create user
        Player createdPlayer = playerService.createUser(playerInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdPlayer);
    }

    @PutMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerTokenDTO loginUser(@RequestBody PlayerPutDTO userputDTO){
        Player playerInput =DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userputDTO);
        return DTOMapper.INSTANCE.convertEntityToUserTokenDTO(playerService.loginUser(playerInput)) ;
    }

    @PutMapping("/players/{playerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateUser(@RequestBody PlayerPutUserIdDTO playerPutUserIdDTO, @PathVariable String playerId){
        playerService.updateUser(DTOMapper.INSTANCE.convertUserPutUserIdDTOToEntity(playerPutUserIdDTO), playerId);

    }

    @PutMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void logoutUser(@RequestBody PlayerTokenDTO playerTokenDTO){
        Player playerInput = DTOMapper.INSTANCE.convertUserTokenDTOToEntity(playerTokenDTO);
        playerService.logOutUser(playerInput) ;
    }
}
