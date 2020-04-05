package ch.uzh.ifi.seal.soprafs20.controller;

import GameLogic.Card;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class GameController {

    private final UserService userService;
    private final Card card;

    GameController(UserService userService, Card card) {
        this.userService = userService;this.card = card;
    }

    @PostMapping("/games/{gameId}/cards/words /{wordId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser( @PathVariable String wordId, @PathVariable String gameId) {
        return Card.card
    }
}