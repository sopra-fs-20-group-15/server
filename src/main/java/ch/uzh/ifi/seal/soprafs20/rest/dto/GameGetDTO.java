package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Scoreboard;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

public class GameGetDTO {

    private Long id;
    private String activePlayerName;
    private List<String> playerNames;
    private List<String> passivePlayerNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivePlayerName() {
        return activePlayerName;
    }

    public void setActivePlayerName(String activePlayerName) {
        this.activePlayerName = activePlayerName;
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }

    public List<String> getPassivePlayerNames() {
        return passivePlayerNames;
    }

    public void setPassivePlayerNames(List<String> passivePlayerNames) {
        this.passivePlayerNames = passivePlayerNames;
    }
}
