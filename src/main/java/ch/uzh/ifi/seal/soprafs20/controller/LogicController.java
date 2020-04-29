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

import java.util.List;

import static java.lang.Long.parseLong;


@RestController
public class LogicController {
    private final PlayerService playerService;
    private final CardService cardService;
    private final ValidationService validationService;
    private final LogicService logicService;
    private final ActiveGameService activeGameService;


    LogicController(PlayerService playerService, CardService cardService, ValidationService validationService, LogicService logicService, ActiveGameService activeGameService) {
        this.playerService = playerService;
        this.cardService = cardService;
        this.validationService = validationService;
        this.logicService = logicService;
        this.activeGameService = activeGameService;
    }

    protected boolean stringIsALong(String str) {
        try {
            parseLong(str);
        } catch(NumberFormatException e) {
            throw new ConflictException("The PathVariable should be a long!");
        } catch(NullPointerException e) {
            throw new ConflictException("The PathVariable should be a long!");
        }
        return true;
    }

    /**Initializes the turn of a game
     *@Param: String gameId, TokenDTO: String playerToken
     *@Returns: void
     *@Throws: 404 not found, if player or game does not exist
     *@Throws: 409: Game has already ended
     *@Throws: 204: Game has already been initialized
     **/
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

    /**gets the amount of remaining cards on the stack
     *@Param: String gameId, String playerToken
     *@Returns: CardGetDTO: Long id, List<String> words
     *@Throws: 404 not found, if player or game does not exist
     *@Throws: 404: No active card set and therefore not found.
     **/
    @GetMapping("/games/{gameId}/cards/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CardGetDTO getCard(@PathVariable String gameId, @PathVariable String playerToken) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPassivePlayerOfGame(playerToken, gameIdLong);
        CardEntity cardEntity = logicService.getCardFromGameById(gameIdLong);
        CardGetDTO cardGetDTO = new CardGetDTO();
        cardGetDTO.setId(cardEntity.getId());
        cardGetDTO.setWords(cardEntity.getWords());
        return cardGetDTO;
    }

    /**gets the amount of remaining cards on the stack
     *@Param: Long gameId, String playerToken
     *@Returns: CardsRemainingDTO: int cardsOnStack
     *@Throws: 404 not found, if player or game does not exist
     **/
    @GetMapping("/games/{gameId}/cards/remainder/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CardsRemainingDTO getCardAmount(@PathVariable Long gameId, @PathVariable String playerToken) {
        validationService.checkPlayerIsPartOfGame(playerToken, gameId);
        logicService.getCardFromGameById(gameId);
        return logicService.getCardAmount(gameId);
    }

    /**sets the mysteryWord
     * @Param: String gameId, CardPostDTo cardPostDTO: long wordId, String playerToken
     * @Returns: void
     * @throws: 404 not found, if player or game does not exist
     * @Throws: 204: Mystery Word has already been set*/
    @PutMapping("/games/{gameId}/mysteryWord")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void setMysteryWord(@PathVariable String gameId, @RequestBody CardPostDTO cardPostDTO) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsActivePlayerOfGame(cardPostDTO.getPlayerToken(), gameIdLong);
        String word = logicService.setMysteryWord(gameIdLong, cardPostDTO.getWordId());
        WordPostDTO wordPostDTO = new WordPostDTO();
        wordPostDTO.setWord(word);

    }

    /**Gives back the chosen MysteryWord
     * @Param: String gameId, String playerToken
     * @Returns: WordPostDTO: String word
     * @throws: 404 not found, if player or game does not exist
     * @Throws: 401: Not authorized to give clue anymore.
     * @Throws: 409: Turn order violated
     * */
    @GetMapping("/games/{gameId}/mysteryWord/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public WordPostDTO getMysteryWord(@PathVariable String gameId, @PathVariable String playerToken) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPassivePlayerOfGame(playerToken, gameIdLong);
        String word = logicService.getMysteryWord(gameIdLong);
        WordPostDTO wordPostDTO = new WordPostDTO();
        wordPostDTO.setWord(word);
        return wordPostDTO;
    }

    /**Sets the clue for each player into a list. Once all players have given their clues, they will be evaluated
     * @Param: String gameId, CluePostDTO: String clue, String playerToken
     * @Returns: void
     * @throws: 404 not found, if player or game does not exist
     * @Throws: 401: Not authorized to give clue anymore.
     * @Throws: 409: Turn order violated
     * */
    @PostMapping("/games/{gameId}/clues")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void giveClue(@PathVariable String gameId, @RequestBody CluePostDTO cluePostDTO) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPassivePlayerOfGame(cluePostDTO.getPlayerToken(), gameIdLong);
        GameEntity game = activeGameService.getGameById(gameIdLong);
        if(game.getActiveMysteryWord().isBlank()) throw new ConflictException("Turn order violated : Mystery Word has not been chosen yet!");
        logicService.giveClue(game,cluePostDTO);
    }

    /**Gets a list with the valid clues
     * @Param: String gameId, String playerToken
     * @Returns: List<ClueGetDTO>: ClueGetDTO: String playerName, String clue
     * @throws: 404 not found, if player or game does not exist
     * @Throws: 204: Guess has already been set
     * @Throws: 204: Mystery word has not been set
     * */
    @GetMapping("/games/{gameId}/clues/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ClueGetDTO> getClues(@PathVariable String gameId, @PathVariable String playerToken) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPartOfGame(playerToken, gameIdLong);
        GameEntity game = activeGameService.getGameById(gameIdLong);
        return logicService.getClues(game);
    }

    /**Gets names of the players, that have already submitted a clue
     * @Param: String gameId, String playerToken
     * @Returns: List<PlayerNameDTO>: PlayerNameDTO: String playerName
     * @Throws: 400: GameId has wrong format
     * @throws: 404 not found, if player or game does not exist
     * @Throws: 204: Guess has already been set
     * @Throws: 204: Mystery word has not been set
     * */
    @GetMapping("/games/{gameId}/clues/players/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerNameDTO> getCluePlayers(@PathVariable String gameId, @PathVariable String playerToken) {
        if(!stringIsALong(gameId)) throw new BadRequestException("GameId has wrong format.");
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPartOfGame(playerToken, gameIdLong);
        GameEntity game = activeGameService.getGameById(gameIdLong);
        return logicService.getCluePlayers(game);
    }

    /**sets the guess and checks, if the guess was correct (set points based on that)
     * @Param: String gameId, GuessPostDTO: String guess, String playerToken
     * @Returns: void
     * @throws: 404 not found, if player or game does not exist
     * @throws: 401 unauthorized, if player ist not the active player of the game
     * @Throws: 409: Turn order violated
     * @Throws: 204: Guess has already been set
     * @Throws: 204: Mystery word has not been set
     * */
    @PostMapping("/games/{gameId}/guesses")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void setGuess(@PathVariable String gameId, @RequestBody GuessPostDTO guessPostDTO) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsActivePlayerOfGame(guessPostDTO.getPlayerToken(), gameIdLong);
        GameEntity game = activeGameService.getGameById(gameIdLong);
        if (!game.getValidCluesAreSet()) throw new ConflictException("Turn order violated : No valid clues have been provided to you yet");
        if (!game.getActiveMysteryWord().isBlank()) {
            if (game.getGuess().isBlank()) {
                logicService.setGuess(game, guessPostDTO.getGuess());
            } else throw new NoContentException("The Guess has already been set");
        }
        else throw new NoContentException("The MysteryWord has not been set yet");
    }

    /**Get the guess and check, if it was valid
     *@Param: String gameId, String playerToken
     *@Returns: GuessGetDTO: String guess, boolean isValidGuess
     *@throws: 404 not found, if player or game does not exist
     *@throws: 401 unauthorized, if player ist not the active player of the game
     * */
    @GetMapping("/games/{gameId}/guesses/{playerToken}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GuessGetDTO getGuess(@PathVariable String gameId, @PathVariable String playerToken) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        validationService.checkPlayerIsPartOfGame(playerToken, gameIdLong);
        GameEntity game = activeGameService.getGameById(gameIdLong);
        String guess = game.getGuess();
        boolean isValidGuess = game.getIsValidGuess();
        GuessGetDTO guessGetDTO = new GuessGetDTO();
        guessGetDTO.setGuess(guess);
        guessGetDTO.setIsValidGuess(isValidGuess);
        return guessGetDTO;
    }

    /**Returns whether the game has already ended or not
     *@Param: String gameId, String playerToken
     *@Returns: GameEndedDTO: boolean hasGameEnded
     *@throws: 404 not found, if player or game does not exist
     *@throws: 401 unauthorized, if player ist not the active player of the game
     * */
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

	/**Get the current statistics
     * @Param: String gameId
     * @Returns: List<StatisticsGetDTO>: StatisticGetDTO: int placement, string playerName, int score, int numberOfCorrectlyGuessedMysteryWords
     * @Throws: 404: No game with specified id found.
     * */
        @GetMapping("/games/{gameId}/statistics")
        @ResponseStatus(HttpStatus.OK)
        @ResponseBody
        public List<StatisticsGetDTO> getScores(@PathVariable String gameId) {
            stringIsALong(gameId);
            Long gameIdLong = parseLong(gameId);
            return logicService.getStatistics(gameIdLong);
    }


}
