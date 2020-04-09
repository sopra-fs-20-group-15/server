package ch.uzh.ifi.seal.soprafs20.GameLogic;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Scoreboard {
    private Map<PlayerEntity, Integer> scoreBoard = new HashMap<PlayerEntity, Integer>();

    public Scoreboard(List<PlayerEntity> players) {
        for (PlayerEntity player : players) {
            scoreBoard.put(player, 0);
        }
    }

    public Map<PlayerEntity, Integer> getEndScore() {
        return scoreBoard;
    }

    //  for active players
    public void updateScore(PlayerEntity player, boolean rightGuess, long milliseconds) {
        scoreBoard.put(player, scoreBoard.get(player) + ScoreCalculator.calculateScoreActivePlayer(player, rightGuess, 33000-milliseconds));
    }

    // for passive players
    public void updateScore(PlayerEntity player, boolean rightGuess, boolean validClue, long milliseconds, int numOfDuplicateGuesses) {
        scoreBoard.put(player, scoreBoard.get(player) + ScoreCalculator.calculateScorePassivePlayer(player, rightGuess, validClue, 33000-milliseconds, numOfDuplicateGuesses));
    }

}
