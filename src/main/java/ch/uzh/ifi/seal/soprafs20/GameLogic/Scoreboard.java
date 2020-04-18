package ch.uzh.ifi.seal.soprafs20.GameLogic;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Embeddable
public class Scoreboard {
    @ElementCollection
    private Map<String, Integer> scoreboard = new HashMap<>();

    public void initializeMap(List<PlayerEntity> playerEntities) {
        for (PlayerEntity playerEntity : playerEntities) {
            scoreboard.put(playerEntity.getUsername(), 0);
        }
    }

    public Map<String, Integer> getScore() {
        return scoreboard;
    }

    public void updateScore(PlayerEntity playerEntity, int score) {
        scoreboard.put(playerEntity.getUsername(), scoreboard.get(playerEntity.getUsername()) + score);
    }

}



