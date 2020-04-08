package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.CardService;
import ch.uzh.ifi.seal.soprafs20.GameLogic.GameService;
import ch.uzh.ifi.seal.soprafs20.GameLogic.ValidationService;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CardPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;


@RestController
public class LogicController {
    private final PlayerService playerService;
    private final CardService cardService;
    private final GameService gameService;
    private final ValidationService validationService;

    LogicController(PlayerService playerService, CardService cardService, ValidationService validationService, GameService gameService) {
        this.playerService = playerService;
        this.cardService = cardService;
        this.validationService = validationService;
        this.gameService = gameService;
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

    @PostMapping("/games/{gameId}/Cards/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getUser(@PathVariable String gameId, @RequestBody CardPostDTO cardPostDTO) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsActivePlayerOfGame(cardPostDTO.getPlayerToken(), gameIdLong);
        GameEntity game = gameService.getGameById(gameIdLong);
        CardEntity card = cardService.getCardById(game.getActiveCardId());
        String word = cardService.chooseWordOnCard(cardPostDTO.getWordId(), card);
        return word;
    }

    @GetMapping("/games/{gameId}/Cards/{playerToken}/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String getUser(@PathVariable String gameId, @PathVariable String playerToken) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPassivePlayerOfGame(playerToken, gameIdLong);
        GameEntity game = gameService.getGameById(gameIdLong);
        String word = game.getActiveWord();
        return word;
    }
}
