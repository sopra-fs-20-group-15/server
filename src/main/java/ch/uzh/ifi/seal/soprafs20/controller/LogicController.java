package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.GameLogic.WordComparer;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.CardService;
import ch.uzh.ifi.seal.soprafs20.GameLogic.GameService;
import ch.uzh.ifi.seal.soprafs20.GameLogic.ValidationService;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CardPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.WordPostDTO;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;


@RestController
public class LogicController {
    private final PlayerService playerService;
    private final CardService cardService;
    private final GameService gameService;
    private final ValidationService validationService;
    private final WordComparer wordComparer;

    LogicController(PlayerService playerService, CardService cardService, ValidationService validationService, GameService gameService) {
        this.playerService = playerService;
        this.cardService = cardService;
        this.validationService = validationService;
        this.gameService = gameService;
        this.wordComparer= new WordComparer();
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
    public WordPostDTO getUser(@PathVariable String gameId, @RequestBody CardPostDTO cardPostDTO) {
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

    @PostMapping("/games/{gameId}/clues/")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void giveClue(@PathVariable String gameId, @RequestBody CluePostDTO cluePostDTO) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPassivePlayerOfGame(cluePostDTO.getPlayerToken(), gameIdLong);
        GameEntity game = gameService.getGameById(gameIdLong);
        String playerName= playerService.getPlayerByToken(cluePostDTO.getPlayerToken()).getUsername();
        if (game.getClueList().get(playerName)==null) game.getClueList().put(playerName,cluePostDTO.getClue());
        else throw new UnauthorizedException("You have already submitted a clue for this round!");
        if (game.getClueList().size()==game.getPlayers().size()-1){
            ArrayList<String> clues=new ArrayList<String>();
            clues.addAll(game.getClueList().values());
            game.setValidClues(wordComparer.compareClues(clues));
        }

    }

    @GetMapping("/games/{gameId}/Cards/{playerToken}/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public WordPostDTO getUser(@PathVariable String gameId, @PathVariable String playerToken) {
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
