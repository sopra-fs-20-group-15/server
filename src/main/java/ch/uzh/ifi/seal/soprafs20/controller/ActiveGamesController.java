package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.exceptions.BadRequestException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static java.lang.Long.parseLong;

/**ActiveGamesController
 * Is responsible for creating the active version of the Just One Game*/
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
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    /**Creates an active game*/
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


    /**Allows player to get an overview of the existing active games*/
    @GetMapping("/activeGames/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getActiveGame(@PathVariable String gameId) {
        stringIsALong(gameId);
        Long gameIdLong = parseLong(gameId);
        GameGetDTO game = activeGameService.getGameInformationById(gameIdLong);
        return game;
    }


}

