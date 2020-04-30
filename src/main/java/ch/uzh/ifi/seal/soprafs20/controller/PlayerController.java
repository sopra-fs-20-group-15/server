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

    @GetMapping("/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerGetDTO getUser(@PathVariable String playerId) {
        PlayerEntity playerEntityInput = DTOMapper.INSTANCE.convertUserIdStringToEntity(playerId);
        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(playerService.getUser(playerEntityInput));
        return playerGetDTO;
    }

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

    @PutMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerTokenDTO loginUser(@RequestBody PlayerPutDTO userputDTO){
        PlayerEntity playerEntityInput =DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userputDTO);
        return DTOMapper.INSTANCE.convertEntityToUserTokenDTO(playerService.loginUser(playerEntityInput)) ;
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
        PlayerEntity playerEntityInput = DTOMapper.INSTANCE.convertUserTokenDTOToEntity(playerTokenDTO);
        playerService.logOutUser(playerEntityInput) ;
    }


}
