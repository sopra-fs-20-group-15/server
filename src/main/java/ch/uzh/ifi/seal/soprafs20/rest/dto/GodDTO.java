package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class GodDTO {
    Long gameId;
    String playerToken1;
    String playerToken2;
    String playerToken3;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getPlayerToken1() {
        return playerToken1;
    }

    public void setPlayerToken1(String playerToken1) {
        this.playerToken1 = playerToken1;
    }

    public String getPlayerToken2() {
        return playerToken2;
    }

    public void setPlayerToken2(String playerToken2) {
        this.playerToken2 = playerToken2;
    }

    public String getPlayerToken3() {
        return playerToken3;
    }

    public void setPlayerToken3(String playerToken3) {
        this.playerToken3 = playerToken3;
    }
}
