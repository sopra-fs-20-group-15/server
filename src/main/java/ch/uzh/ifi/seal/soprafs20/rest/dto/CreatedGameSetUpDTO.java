package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.GameType;

public class CreatedGameSetUpDTO {
    private Long gameId;
    private String gameName;
    private Long numberOfPlayers;
    private Long numberOfAngles;
    private Long numberOfDevils;
    private GameType gameType;
    private String hostName;

    public Long getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(Long numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Long getNumberOfAngles() {
        return numberOfAngles;
    }

    public void setNumberOfAngles(Long numberOfAngles) {
        this.numberOfAngles = numberOfAngles;
    }

    public Long getNumberOfDevils() {
        return numberOfDevils;
    }

    public void setNumberOfDevils(Long numberOfDevils) {
        this.numberOfDevils = numberOfDevils;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
