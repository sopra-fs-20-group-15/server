package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class GuessPostDTO {
    private String guess;
    private String playerToken;

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    public String getPlayerToken() {
        return playerToken;
    }

    public void setPlayerToken(String playerToken) {
        this.playerToken = playerToken;
    }
}
