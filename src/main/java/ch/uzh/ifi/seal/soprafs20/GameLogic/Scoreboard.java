package ch.uzh.ifi.seal.soprafs20.GameLogic;
<<<<<<< Updated upstream

public class Scoreboard {
=======
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Scoreboard {
    private Map<PlayerEntity, Integer> scoreBoard = new HashMap<PlayerEntity, Integer>();

    public Scoreboard(List<PlayerEntity> playerEntities) {
        for (PlayerEntity playerEntity : playerEntities) {
            scoreBoard.put(playerEntity, 0);
        }
    }

    public Map<PlayerEntity, Integer> getEndScore() {
        return scoreBoard;
    }

    //  for active players
    public void updateScore(PlayerEntity playerEntity, boolean rightGuess, long milliseconds) {
        scoreBoard.put(playerEntity, scoreBoard.get(playerEntity) + ScoreCalculator.calculateScoreActivePlayer(playerEntity, rightGuess, 33000-milliseconds));
    }

    // for passive players
    public void updateScore(PlayerEntity playerEntity, boolean rightGuess, boolean validClue, long milliseconds, int numOfDuplicateGuesses) {
        scoreBoard.put(playerEntity, scoreBoard.get(playerEntity) + ScoreCalculator.calculateScorePassivePlayer(playerEntity, rightGuess, validClue, 33000-milliseconds, numOfDuplicateGuesses));
    }

>>>>>>> Stashed changes
}
