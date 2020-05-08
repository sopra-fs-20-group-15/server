package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class ChatPostDTO {
    Long gameId;
    String playerToken;
    String message;

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setPlayerToken(String playerToken) {
        this.playerToken = playerToken;
    }

    public String getPlayerToken() {
        return playerToken;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
