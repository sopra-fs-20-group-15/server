package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.GameLogic.Card;
import ch.uzh.ifi.seal.soprafs20.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the PlayerService and finally return the result.
 */
@RestController
public class GameController {

    private final PlayerService playerService;
    private final Card card;

    GameController(PlayerService playerService, Card card) {
        this.playerService = playerService;this.card = card;
    }

    @PostMapping("/games/{gameId}/cards/words /{wordId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getUser(@PathVariable String wordId, @PathVariable String gameId) {
        return card.chooseWordOnCard(wordId);
    }
}