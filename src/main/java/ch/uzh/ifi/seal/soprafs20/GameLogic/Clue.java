package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.Embeddable;

@Embeddable
public class Clue {
    private String clue;
    private String playerToken;

    public void setClue(String clue) {
        this.clue = clue;
    }

    public void setPlayerToken(String playerToken) {
        this.playerToken = playerToken;
    }

    public String getClue() {
        return clue;
    }

    public String getPlayerToken() {
        return playerToken;
    }
}
