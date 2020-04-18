package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;


@RestController
public class LogicController {
    private final PlayerService playerService;
    private final CardService cardService;
    private final GameService gameService;
    private final ValidationService validationService;
    private final LogicService logicService;


    LogicController(PlayerService playerService, CardService cardService, ValidationService validationService, GameService gameService, LogicService logicService) {
        this.playerService = playerService;
        this.cardService = cardService;
        this.validationService = validationService;
        this.gameService = gameService;
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

    /**Initializes the turn of a game*/
    @PutMapping("/games/{gameId}/initializations")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void initializeTurn(@PathVariable String gameId, @RequestBody TokenDTO tokenDTO) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        PlayerEntity playerToken = DTOMapper.INSTANCE.convertTokenDTOToEntity(tokenDTO);
        validationService.checkPlayerIsPartOfGame(playerToken.getToken(), gameIdLong);
        logicService.initializeTurn(gameIdLong);
    }

    @GetMapping("/games/{gameId}/cards/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CardGetDTO getCard(@PathVariable String gameId, @PathVariable String playerToken) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPartOfGame(playerToken, gameIdLong);
        CardEntity cardEntity = logicService.getCardFromGameById(gameIdLong);
        CardGetDTO cardGetDTO = new CardGetDTO();
        cardGetDTO.setId(cardEntity.getId());
        cardGetDTO.setWords(cardEntity.getWords());
        return cardGetDTO;
    }

    /**sets the mysteryWord*/
    @PutMapping("/games/{gameId}/mysteryWord")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public WordPostDTO setMysteryWord(@PathVariable String gameId, @RequestBody CardPostDTO cardPostDTO) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsActivePlayerOfGame(cardPostDTO.getPlayerToken(), gameIdLong);
        String word = logicService.setMysteryWord(gameIdLong, cardPostDTO.getWordId());
        WordPostDTO wordPostDTO = new WordPostDTO();
        wordPostDTO.setWord(word);
        return wordPostDTO;
    }

    /**Gives back the chosen MysteryWord*/
    @GetMapping("/games/{gameId}/mysteryWord/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public WordPostDTO getMysteryWord(@PathVariable String gameId, @PathVariable String playerToken) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPartOfGame(playerToken, gameIdLong);
        String word = logicService.getMysteryWord(gameIdLong);
        WordPostDTO wordPostDTO = new WordPostDTO();
        wordPostDTO.setWord(word);
        return wordPostDTO;
    }

    /**Sets the clue for each player into a list. Once all players have given their clues, they will be evaluated*/
    @PostMapping("/games/{gameId}/clues")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void giveClue(@PathVariable String gameId, @RequestBody CluePostDTO cluePostDTO) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPassivePlayerOfGame(cluePostDTO.getPlayerToken(), gameIdLong);
        GameEntity game = gameService.getGameById(gameIdLong);
        if(game.getActiveMysteryWord().equals("")) throw new ConflictException("Turn order violated : Mystery Word has not been chosen yet!");
        logicService.giveClue(game,cluePostDTO);
    }

    /**get the valid clue list*/
    @GetMapping("/games/{gameId}/clues/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ClueGetDTO> getClues(@PathVariable String gameId, @PathVariable String playerToken) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPartOfGame(playerToken, gameIdLong);
        GameEntity game = gameService.getGameById(gameIdLong);
        return logicService.getClues(game);
    }

    /**get list with players that have already submitted clue*/
    @GetMapping("/games/{gameId}/clues/players/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerNameDTO> getCluePlayers(@PathVariable String gameId, @PathVariable String playerToken) {
        if(!stringIsALong(gameId)) throw new BadRequestException("Game-Id has wrong format.");
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPartOfGame(playerToken, gameIdLong);
        GameEntity game = gameService.getGameById(gameIdLong);
        return logicService.getCluePlayers(game);
    }

    /**sets the guess and checks, if the guess was correct (set points based on that)*/
    @PostMapping("/games/{gameId}/guesses")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void setGuess(@PathVariable String gameId, @RequestBody GuessPostDTO guessPostDTO) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsActivePlayerOfGame(guessPostDTO.getPlayerToken(), gameIdLong);
        GameEntity game = gameService.getGameById(gameIdLong);
        if (!game.getActiveMysteryWord().equals("")) {
            if (!game.getGuess().equals("")) {
                gameService.getPlayerByToken(guessPostDTO.getPlayerToken()).setTimePassed(System.currentTimeMillis()-game.getTimeStart());
                logicService.setGuess(game, guessPostDTO.getGuess());
            } else throw new NoContentException("The Guess has already been set");
        }
        else throw new NoContentException("The MysteryWord has already been set");
    }

    /**Get the clue and check, if it was valid*/
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

    /**Check if the game has ended*/
    @GetMapping("/games/{gameId}/ends/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameEndedDTO hasGameEnded(@PathVariable String gameId, @PathVariable String playerToken) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPartOfGame(playerToken, gameIdLong);
        Boolean hasGameEnded = logicService.hasGameEnded(gameIdLong);
        GameEndedDTO gameEndedDTO = new GameEndedDTO();
        gameEndedDTO.setHasGameEnded(hasGameEnded);
        return gameEndedDTO;
    }

}
