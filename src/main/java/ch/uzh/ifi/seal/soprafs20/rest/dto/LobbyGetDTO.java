package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.List;

public class LobbyGetDTO {
    private Long activeGameId;
    private Long gameSetUpId;
    private String gameName;
    private String hostName;
    private List<String> playerNames;
    private Long numOfDesiredPlayers;
    private Long numOfActualPlayers;
    private Long numOfAngels;
    private Long numOfDevils;

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }

    public Long getNumOfActualPlayers() {
        return numOfActualPlayers;
    }

    public void setNumOfActualPlayers(Long numOfActualPlayers) {
        this.numOfActualPlayers = numOfActualPlayers;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Long getGameSetUpId() {
        return gameSetUpId;
    }

    public void setGameSetUpId(Long gameSetUpId) {
        this.gameSetUpId = gameSetUpId;
    }

    public Long getNumOfAngels() {
        return numOfAngels;
    }

    public void setNumOfAngels(Long numOfAngels) {
        this.numOfAngels = numOfAngels;
    }

    public Long getNumOfDevils() {
        return numOfDevils;
    }

    public void setNumOfDevils(Long numOfDevils) {
        this.numOfDevils = numOfDevils;
    }

    public Long getNumOfDesiredPlayers() {
        return numOfDesiredPlayers;
    }

    public void setNumOfDesiredPlayers(Long numOfDesiredPlayers) {
        this.numOfDesiredPlayers = numOfDesiredPlayers;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Long getActiveGameId() {
        return activeGameId;
    }

    public void setActiveGameId(Long activeGameId) {
        this.activeGameId = activeGameId;
    }
}
