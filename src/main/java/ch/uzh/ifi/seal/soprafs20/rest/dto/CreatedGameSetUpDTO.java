package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.GameType;

public class CreatedGameSetUpDTO {
    private Long gameId;
    private Long numberOfPlayers;
    private Long numberOfBots;
    private GameType gameType;

    public Long getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(Long numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public Long getNumberOfBots() {
        return numberOfBots;
    }

    public void setNumberOfBots(Long numberOfBots) {
        this.numberOfBots = numberOfBots;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }


    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
