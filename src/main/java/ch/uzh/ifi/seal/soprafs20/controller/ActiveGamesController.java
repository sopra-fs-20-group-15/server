package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static java.lang.Long.parseLong;

/**ActiveGamesController
 * Is responsible for creating and deleting the active version of the Just One Game. Can also give information about an active game*/
@RestController
public class ActiveGamesController {

    private final LogicService logicService;
    private final ActiveGameService activeGameService;
    private final ValidationService validationService;

    ActiveGamesController( LogicService logicService, ActiveGameService activeGameService, ValidationService validationService) {
        this.logicService = logicService;
        this.activeGameService = activeGameService;
        this.validationService = validationService;
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


    /**Creates an active game
     * @Param: playerTokenDTO: String playerToken
     * @Returns: activeGamePostDTO: Long id, List<String> playerNames
     * @Throws: 409: The PathVariable is not a Long
     * @Throws: 409: Not enough players are part of the game in order to start it
     * @Throws: 401: playerToken is not the Token of the Host
     * */
    @PostMapping("/games/{gameSetUpId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ActiveGamePostDTO createActiveGame(@PathVariable String gameSetUpId, @RequestBody PlayerTokenDTO playerTokenDTO) {
        //Check that SetupEntity actually exists
        if (stringIsALong(gameSetUpId)){
            //Try to create active game
            Long gsId = parseLong(gameSetUpId);
            ActiveGamePostDTO activeGamePostDTO = activeGameService.createActiveGame(gsId, playerTokenDTO.getToken());
            logicService.initializeTurn(activeGamePostDTO.getId());
            return activeGamePostDTO;
        }
        else throw new BadRequestException("Game-Setup-ID has wrong format!");
    }


    /**Allows a player to leave the request that he or she does not wish to continue a round of Just One
     * Takes the player out of the game at the end of that round. Deletes the game if he or she is the only human
     * player in that game
     * @Param: TokenDTO: String playerToken
     * @Returns: void
     * @Throws: 404: player not found
     * @Throws: 404: game not found
     * */
    @PutMapping("/activeGames/{gameId}/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void removePlayerFromGame(@PathVariable String gameId, @RequestBody TokenDTO tokenDTO) {
        //Check that SetupEntity actually exists
        if (stringIsALong(gameId)) {
            //Try to create active game
            Long Id = parseLong(gameId);
            validationService.checkPlayerIsPartOfGame(tokenDTO.getPlayerToken(), Id);
            activeGameService.removePlayerFromGame(Id, tokenDTO.getPlayerToken());

        }
    }


    @GetMapping("/activeGames/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getActiveGame(@PathVariable String gameId) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        GameGetDTO gameGetDTO = activeGameService.getGameInformationById(gameIdLong);
        return gameGetDTO;
    }

    /**Deletes an active game once it has ended. Can be executed by anyone
     * @Param: String gameId
     * @Returns: void
     * @Throws: 409:The PathVariable is not a Long
     * @Throws: 409: Game has not ended yet
     * */
    @DeleteMapping("/activeGames/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void deleteActiveGame(@PathVariable String gameId) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
       activeGameService.deleteActiveGame(gameIdLong);
    }


}

