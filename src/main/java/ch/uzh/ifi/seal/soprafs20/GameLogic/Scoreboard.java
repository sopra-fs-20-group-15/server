package ch.uzh.ifi.seal.soprafs20.GameLogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Scoreboard {

    private Map<PlayerEntity, Integer> scoreboard = new HashMap<PlayerEntity, Integer>();

    public Scoreboard(List<PlayerEntity> playerEntities) {
        for (PlayerEntity playerEntity : playerEntities) {
            scoreboard.put(playerEntity, 0);
        }
    }

    public Map<PlayerEntity, Integer> getEndScore() {
        return scoreboard;
    }

    public void updateScore(PlayerEntity playerEntity, int score) {
        scoreboard.put(playerEntity, scoreboard.get(playerEntity) + score);
    }

}



