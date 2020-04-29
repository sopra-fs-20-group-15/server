package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.GameType;

public class LobbyOverviewGetDTO {
    private Long id;
    private String gameName;
    private GameType gameType;
    private Long numOfDesiredPlayers;
    private Long numOfAngels;
    private Long numOfDevils;
    private Long numOfHumanPlayers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getNumOfHumanPlayers() {
        return numOfHumanPlayers;
    }

    public void setNumOfHumanPlayers(Long numOfHumanPlayers) {
        this.numOfHumanPlayers = numOfHumanPlayers;
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
