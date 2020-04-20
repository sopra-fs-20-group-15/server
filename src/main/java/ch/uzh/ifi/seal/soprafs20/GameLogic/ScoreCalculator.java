package ch.uzh.ifi.seal.soprafs20.GameLogic;


import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;

import java.util.Map;

public class ScoreCalculator {

    public static int calculateScoreActivePlayer(PlayerEntity playerEntity, boolean validGuess) {
        if (validGuess) {
            Long milliseconds=33000-playerEntity.getTimePassed();
            if (milliseconds >= 20000) return  25;
            else if (milliseconds >= 10000) return  23;
            else return 21;
        }
        return 0;
    }

    public static int calculateScorePassivePlayer(PlayerEntity playerEntity, boolean validGuess, boolean validClue, int numOfDuplicateGuesses) {
        if (validClue && validGuess) {
            Long milliseconds=28000-playerEntity.getTimePassed();
            if (milliseconds >= 20000) return 20;
            else if (milliseconds >= 15000) return 19;
            else if (milliseconds >= 10000) return 18;
            else if (milliseconds >= 5000) return 17;
            else return 16;
        }
        else if (validGuess){
            return 15 - numOfDuplicateGuesses*5;
        }
        return -numOfDuplicateGuesses*5;
    }
}
