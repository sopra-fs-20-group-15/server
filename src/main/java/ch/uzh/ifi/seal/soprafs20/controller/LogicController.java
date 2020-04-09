package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.CardService;
import ch.uzh.ifi.seal.soprafs20.GameLogic.GameService;
import ch.uzh.ifi.seal.soprafs20.GameLogic.ValidationService;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CardPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.WordPostDTO;
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
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public WordPostDTO setMysteryWord(@PathVariable String gameId, @RequestBody CardPostDTO cardPostDTO) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsActivePlayerOfGame(cardPostDTO.getPlayerToken(), gameIdLong);
        GameEntity game = gameService.getGameById(gameIdLong);
        CardEntity card = cardService.getCardById(game.getActiveCardId());
        if (game.getActiveWord() != ""){
        String word = cardService.chooseWordOnCard(cardPostDTO.getWordId(), card);
            WordPostDTO wordPostDTO = new WordPostDTO();
            wordPostDTO.setWord(word);
            return wordPostDTO;}
        else throw new NoContentException("The MysteryWord has already been set");
    }

    /**Gives back the chosen MysteryWord*/
    @GetMapping("/games/{gameId}/activeWord/{playerToken}/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public WordPostDTO getMysteryWord(@PathVariable String gameId, @PathVariable String playerToken) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPassivePlayerOfGame(playerToken, gameIdLong);
        GameEntity game = gameService.getGameById(gameIdLong);
        String word = game.getActiveWord();
        WordPostDTO wordPostDTO = new WordPostDTO();
        wordPostDTO.setWord(word);
        return wordPostDTO;
    }
}
