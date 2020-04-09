package ch.uzh.ifi.seal.soprafs20.GameLogic;

<<<<<<<<< Temporary merge branch 1
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;

public class ScoreCalculator {

    public static int calculateScoreActivePlayer(PlayerEntity playerEntity, boolean validGuess, long milliseconds) {
=========
public class ScoreCalculator {

    public static int calculateScoreActivePlayer(Player player, boolean validGuess, long milliseconds) {
>>>>>>>>> Temporary merge branch 2
        if (validGuess) {
            if (milliseconds >= 20000) return  25;
            else if (milliseconds >= 10000) return  23;
            else if (milliseconds >= 0) return 21;
        }
        return 0;
    }

<<<<<<<<< Temporary merge branch 1
    public static int calculateScorePassivePlayer(PlayerEntity playerEntity, boolean rightGuess, boolean validClue, long milliseconds, int numOfDuplicateGuesses) {
=========
    public static int calculateScorePassivePlayer(Player player, boolean rightGuess, boolean validClue, long milliseconds, int numOfDuplicateGuesses) {
>>>>>>>>> Temporary merge branch 2
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
