package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Guess {
    private String Guess;
    private boolean isValidGuess;

    public void setGuess(String guess) {
        Guess = guess;
    }

    public String getGuess() {
        return Guess;
    }

    public void setValidGuess(boolean validGuess) {
        isValidGuess = validGuess;
    }

    public boolean getIsValidGuess() {
        return isValidGuess;
    }
}
