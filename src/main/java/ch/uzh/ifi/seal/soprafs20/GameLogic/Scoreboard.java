package ch.uzh.ifi.seal.soprafs20.GameLogic;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Scoreboard {
    private Map<Player, Integer> scoreBoard = new HashMap<Player, Integer>();

    public Scoreboard(List<Player> players) {
        for (Player player : players) {
            scoreBoard.put(player, 0);
        }
    }

    public Map<Player, Integer> getEndScore() {
        return scoreBoard;
    }

    //  for active players
    public void updateScore(Player player, boolean rightGuess, long milliseconds) {
        scoreBoard.put(player, scoreBoard.get(player) + ScoreCalculator.calculateScoreActivePlayer(player, rightGuess, 33000-milliseconds));
    }

    // for passive players
    public void updateScore(Player player, boolean rightGuess, boolean validClue, long milliseconds, int numOfDuplicateGuesses) {
        scoreBoard.put(player, scoreBoard.get(player) + ScoreCalculator.calculateScorePassivePlayer(player, rightGuess, validClue, 33000-milliseconds, numOfDuplicateGuesses));
    }

}
