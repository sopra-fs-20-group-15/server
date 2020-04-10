package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.GameLogic.*;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;
import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;


@RestController
public class LogicController {
    private final PlayerService playerService;
    private final CardService cardService;
    private final GameService gameService;
    private final ValidationService validationService;
    private final WordComparer wordComparer;
    private final LogicService logicService;


    LogicController(PlayerService playerService, CardService cardService, ValidationService validationService, GameService gameService, LogicService logicService) {
        this.playerService = playerService;
        this.cardService = cardService;
        this.validationService = validationService;
        this.gameService = gameService;
        this.wordComparer= new WordComparer();
        this.logicService = logicService;
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
        if (game.getActiveMysteryWord() != ""){
        String word = cardService.chooseWordOnCard(cardPostDTO.getWordId(), card);
            WordPostDTO wordPostDTO = new WordPostDTO();
            wordPostDTO.setWord(word);
            return wordPostDTO;}
        else throw new NoContentException("The MysteryWord has already been set");
    }

    @PostMapping("/games/{gameId}/guesses")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void setGuess(@PathVariable String gameId, @RequestBody GuessPostDTO guessPostDTO) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsActivePlayerOfGame(guessPostDTO.getPlayerToken(), gameIdLong);
        GameEntity game = gameService.getGameById(gameIdLong);
        if (game.getActiveMysteryWord() != "") {
            if (game.getGuess() != "") {
                logicService.setGuess(game, guessPostDTO.getGuess());
            } else throw new NoContentException("The Guess has already been set");
        }
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

    @GetMapping("/games/{gameId}/guesses/{playerToken}/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GuessGetDTO getGuess(@PathVariable String gameId, @PathVariable String playerToken) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPartOfGame(playerToken, gameIdLong);
        GameEntity game = gameService.getGameById(gameIdLong);
        String guess = game.getGuess();
        boolean isValidGuess = game.getIsValidGuess();
        GuessGetDTO guessGetDTO = new GuessGetDTO();
        guessGetDTO.setGuess(guess);
        guessGetDTO.setIsValidGuess(isValidGuess);
        return guessGetDTO;
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
        String word = game.getActiveMysteryWord();
        WordPostDTO wordPostDTO = new WordPostDTO();
        wordPostDTO.setWord(word);
        return wordPostDTO;
    }
}
