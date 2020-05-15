package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class CluePostDTO {
    private String clue;
    private String playerToken;


    public String getClue() {
        return clue;
    }

    public String getPlayerToken() {
        return playerToken;
    }

    public void setClue(String clue) {
        this.clue = clue;
    }

    public void setPlayerToken(String playerToken) {
        this.playerToken = playerToken;
    }
}
