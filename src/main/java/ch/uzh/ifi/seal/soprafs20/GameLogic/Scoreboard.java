package ch.uzh.ifi.seal.soprafs20.GameLogic;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.rest.dto.StatisticsGetDTO;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Embeddable
public class Scoreboard {
    @ElementCollection
    private Map<String, Integer> scoreboard = new HashMap<>();

    public Map<String, Integer> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Map<String, Integer> scoreboard) {
        this.scoreboard = scoreboard;
    }

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

    /**Transforms the internal statistics of the scoreboard into a format which is suitable for sending to the client*/
    public List<StatisticsGetDTO> transformIntoList(){
        List<StatisticsGetDTO> rankScorePlayerNameList = new ArrayList<StatisticsGetDTO>();
        //Convert into a List of StatisticsGetDto which consists of the Rank, the Score and the playerName
        for (Map.Entry<String, Integer> entry : this.scoreboard.entrySet()){
            StatisticsGetDTO rankScorePlayerName = new StatisticsGetDTO();
            rankScorePlayerName.setPlayerName(entry.getKey());
            rankScorePlayerName.setScore(entry.getValue());
            rankScorePlayerNameList.add(rankScorePlayerName);
        }
        return rankScorePlayerNameList;
    }

}



