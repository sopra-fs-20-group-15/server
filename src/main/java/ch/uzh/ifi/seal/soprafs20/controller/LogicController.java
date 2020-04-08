package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.GameLogic.CardService;
import ch.uzh.ifi.seal.soprafs20.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
public class LogicController {
    private final PlayerService playerService;
    private final CardService cardService;

    LogicController(PlayerService playerService, CardService cardService) {
        this.playerService = playerService;this.cardService = cardService;
    }

    @PostMapping("/games/{gameId}/Cards/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getUser(@PathVariable String gameId, @RequestBody PostCardDTO postCardDTO) {
        return cardService.chooseWordOnCard(wordId);
    }
}
