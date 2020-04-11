package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class PlayerIntoGameSetUpDTO {
    String playerToken;
    String password;

    public String getPlayerToken() {
        return playerToken;
    }

    public void setPlayerToken(String playerToken) {
        this.playerToken = playerToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
