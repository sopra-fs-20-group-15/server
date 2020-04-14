package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.GameType;

public class LobbyOverviewGetDTO {
    private String gameName;
    private GameType gameType;
    private Long numOfDesiredPlayers;
    private Long numOfAngels;
    private Long numOfDevils;
    private Long numOfActualPlayers;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public Long getNumOfActualPlayers() {
        return numOfActualPlayers;
    }

    public void setNumOfActualPlayers(Long numOfActualPlayers) {
        this.numOfActualPlayers = numOfActualPlayers;
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

}
