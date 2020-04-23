package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static java.lang.Long.parseLong;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the PlayerService and finally return the result.
 */
@RestController
public class HelperForPostManController {
    private final PlayerService playerService;
    private final CardService cardService;
    private final GameSetUpService gameService;
    private final ValidationService validationService;
    private final LogicService logicService;
    private final GodService godService;


    HelperForPostManController(PlayerService playerService, CardService cardService, ValidationService validationService, GameSetUpService gameService, LogicService logicService, GodService godService) {
        this.playerService = playerService;
        this.cardService = cardService;
        this.validationService = validationService;
        this.gameService = gameService;
        this.logicService = logicService;
        this.godService = godService;
    }

    /**Sets the clue for each player into a list. Once all players have given their clues, they will be evaluated*/
    @PostMapping("/magicalCreatesThreeUsersLogsThemInAndCreatesAnActiveGameWithThemAndAnAngelAndADevil")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GodDTO magic() {
        GodDTO godDTO = godService.createPlayerAndGame();
        return godDTO;
    }
}
