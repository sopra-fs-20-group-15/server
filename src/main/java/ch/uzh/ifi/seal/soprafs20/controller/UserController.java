package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.GameLogic.Player;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Player Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all players in the internal representation
        List<Player> players = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (Player player : players) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(player));
        }
        return userGetDTOs;
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser(@PathVariable String userId) {
        Player playerInput = DTOMapper.INSTANCE.convertUserIdStringToEntity(userId);
        UserGetDTO userGetDTO= DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.getUser(playerInput));
        return userGetDTO;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        Player playerInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        Player createdPlayer = userService.createUser(playerInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdPlayer);
    }

    @PutMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserTokenDTO loginUser(@RequestBody UserPutDTO userputDTO){
        Player playerInput =DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userputDTO);
        return DTOMapper.INSTANCE.convertEntityToUserTokenDTO(userService.loginUser(playerInput)) ;
    }

    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateUser(@RequestBody UserPutUserIdDTO userPutUserIdDTO, @PathVariable String userId){
        userService.updateUser(DTOMapper.INSTANCE.convertUserPutUserIdDTOToEntity(userPutUserIdDTO), userId);

    }

    @PutMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void logoutUser(@RequestBody UserTokenDTO userTokenDTO){
        Player playerInput = DTOMapper.INSTANCE.convertUserTokenDTOToEntity(userTokenDTO);
        userService.logOutUser(playerInput) ;
    }
}
