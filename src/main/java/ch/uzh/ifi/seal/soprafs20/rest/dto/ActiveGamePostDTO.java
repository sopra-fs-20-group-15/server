package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class ActiveGamePostDTO {
    private Long id;

    private List<String> playerNames = new ArrayList<String>();

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }
}
