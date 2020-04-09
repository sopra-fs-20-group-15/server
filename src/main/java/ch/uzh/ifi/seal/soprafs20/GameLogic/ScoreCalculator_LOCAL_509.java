package ch.uzh.ifi.seal.soprafs20.GameLogic;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;

public class ScoreCalculator {

    public static int calculateScoreActivePlayer(PlayerEntity playerEntity, boolean validGuess, long milliseconds) {
        if (validGuess) {
            if (milliseconds >= 20000) return  25;
            else if (milliseconds >= 10000) return  23;
            else if (milliseconds >= 0) return 21;
        }
        return 0;
    }

    public static int calculateScorePassivePlayer(PlayerEntity playerEntity, boolean rightGuess, boolean validClue, long milliseconds, int numOfDuplicateGuesses) {
        if (validClue && rightGuess) {
            if (milliseconds >= 25000) return 21;
            else if (milliseconds >= 20000) return 20;
            else if (milliseconds >= 15000) return 19;
            else if (milliseconds >= 10000) return 18;
            else if (milliseconds >= 5000) return 17;
            else if (milliseconds >= 0) return 16;
        }
        else if (rightGuess){
            return 15 - numOfDuplicateGuesses*5;
        }
        return -numOfDuplicateGuesses*5;
    }
}
