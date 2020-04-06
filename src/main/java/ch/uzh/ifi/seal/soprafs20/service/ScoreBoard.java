package ch.uzh.ifi.seal.soprafs20.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreBoard {
    private Map<Player, Integer> scoreBoard = new HashMap<Player, Integer>();


    public ScoreBoard(List<Player> players) {
        for (Player player : players) {
            scoreBoard.put(player, 0);
        }
    }

    public Map<Player, Integer> getEndScore() {
        return scoreBoard;
    }

//  for active players
    public void updateScore(Player player, boolean rightGuess, long milliseconds) {
        calculateScoreActivePlayer(player, rightGuess, milliseconds);
    }

    public void updateScore(Player player, boolean rightGuess, boolean validClue, long milliseconds, int numOfDuplicateGuesses) {
        calculateScorePassivePlayer(player, rightGuess, validClue, milliseconds, numOfDuplicateGuesses);
    }

    private void calculateScoreActivePlayer(Player player, boolean validGuess, long milliseconds) {
        if (validGuess) {
            if (milliseconds >= 20000) scoreBoard.put(player, scoreBoard.get(player) + 25);
            else if (milliseconds >= 10000) scoreBoard.put(player, scoreBoard.get(player) + 23);
            else if (milliseconds >= 0) scoreBoard.put(player, scoreBoard.get(player) + 21);
        }
    }

    private void calculateScorePassivePlayer(Player player,boolean rightGuess, boolean validClue, long milliseconds, int numOfDuplicateGuesses) {
        if (validClue && rightGuess) {
            if (milliseconds >= 25000) scoreBoard.put(player, scoreBoard.get(player) + 21);
            else if (milliseconds >= 20000) scoreBoard.put(player, scoreBoard.get(player) + 20);
            else if (milliseconds >= 15000) scoreBoard.put(player, scoreBoard.get(player) + 19);
            else if (milliseconds >= 10000) scoreBoard.put(player, scoreBoard.get(player) + 18);
            else if (milliseconds >= 5000) scoreBoard.put(player, scoreBoard.get(player) + 17);
            else if (milliseconds >= 0) scoreBoard.put(player, scoreBoard.get(player) + 16);
        }
        else if (rightGuess){
            scoreBoard.put(player, scoreBoard.get(player) + 15 - numOfDuplicateGuesses*5);
        }
        else scoreBoard.put(player, scoreBoard.get(player) -numOfDuplicateGuesses*5);
    }
}



