package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.rest.dto.LeaderBoardGetDTO;
import ch.uzh.ifi.seal.soprafs20.service.LeaderBoardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**LeaderBoardController: Shows statistics, for example how is the best Just One player. */

@RestController
public class LeaderBoardController {

    private final LeaderBoardService leaderboardService;

    LeaderBoardController(LeaderBoardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }
    /**Shows the LeaderBoard of all players. In there is how many rounds they played, how many points they got over all games etc.
     * @Returns: List<LeaderBoardGetDTO>: int rank, int score, int gamesPlayed, String playerName,
     * */
    @GetMapping("/leaderBoards")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LeaderBoardGetDTO> getLeaderBoard() {
        return leaderboardService.getLeaderBoard();
    }
}
