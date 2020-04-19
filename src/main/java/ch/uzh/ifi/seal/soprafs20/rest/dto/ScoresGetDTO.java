package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.Map;

public class ScoresGetDTO {
    Map<String, Integer> playerNamesHashMap;

    public Map<String, Integer> getPlayerNamesHashMap() {
        return playerNamesHashMap;
    }

    public void setPlayerNamesHashMap(Map<String, Integer> playerNamesHashMap) {
        this.playerNamesHashMap = playerNamesHashMap;
    }
}
