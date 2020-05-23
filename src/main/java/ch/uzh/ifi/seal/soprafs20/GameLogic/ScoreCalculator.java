package ch.uzh.ifi.seal.soprafs20.GameLogic;


import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;

public class ScoreCalculator {

    public static int calculateScoreActivePlayer(PlayerEntity playerEntity, boolean validGuess) {
        if (validGuess) {
            Long milliseconds=66000-playerEntity.getTimePassed();
            if (milliseconds >= 40000) return  25;
            else if (milliseconds >= 20000) return  23;
            else return 21;
        }
        return 0;
    }

    public static int calculateScorePassivePlayer(PlayerEntity playerEntity, boolean validGuess, boolean validClue, int numOfDuplicateGuesses) {
        if (validClue && validGuess) {
            Long milliseconds=56000-playerEntity.getTimePassed();
            if (milliseconds >= 40000) return 20;
            else if (milliseconds >= 30000) return 19;
            else if (milliseconds >= 20000) return 18;
            else if (milliseconds >= 1000) return 17;
            else return 16;
        }
        else if (validGuess){
            return 15 - numOfDuplicateGuesses*5;
        }
        return -numOfDuplicateGuesses*5;
    }
}
