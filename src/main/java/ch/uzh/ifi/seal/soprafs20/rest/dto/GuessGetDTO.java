package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class GuessGetDTO {
    private String guess;

    private boolean isValidGuess;

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    public boolean getIsValidGuess() {
        return isValidGuess;
    }

    public void setIsValidGuess(boolean validGuess) {
        isValidGuess = validGuess;
    }
}
