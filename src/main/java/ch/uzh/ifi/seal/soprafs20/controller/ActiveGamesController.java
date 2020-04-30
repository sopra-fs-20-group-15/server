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


    ActiveGamesController( LogicService logicService, ActiveGameService activeGameService) {
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


    /**Allows player to get an overview of an existing active game
     * @Param: String gameId
     * @Returns: GameGetDTO: Long id, String activePlayerName, List<String> playerNames, List<String> passivePlayerNames,
     * @Throws: 409:The PathVariable is not a Long
     * @Throws: 404: Game with specified Id cannot be found
     * */
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

