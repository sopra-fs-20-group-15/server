package ch.uzh.ifi.seal.soprafs20.GameLogic;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.botCreator.BotCreator;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.*;

@Embeddable
public class HandlerForLeavingPlayers {

    /**Token, millliseconds not having done a call*/
    @ElementCollection
    private Map<String,Long> deadMansSwitchMap= new HashMap<>();

    public Map<String, Long> getDeadMansSwitchMap() {
        return deadMansSwitchMap;
    }

    public void setDeadMansSwitchMap(Map<String, Long> deadMansSwitchMap) {
        this.deadMansSwitchMap = deadMansSwitchMap;
    }

    public void loadPlayersIntoHandler(List<PlayerEntity> players){
        for (PlayerEntity player: players) {
            deadMansSwitchMap.put(player.getToken(),System.currentTimeMillis());
        }
    }

    public void updateDeadMansSwitch(String token){
        deadMansSwitchMap.put(token, System.currentTimeMillis());
    }

    //Checks if any player has timed out. If they have, they are removed from the game and replaced by a bot.
    public void removeInactivePlayers(GameEntity game){
        List<String> tokens=new ArrayList<>();
        // find out which players need to be removed
        for(Map.Entry<String, Long> entry: deadMansSwitchMap.entrySet()){
            if (System.currentTimeMillis()-entry.getValue()>30000) {
                tokens.add(entry.getKey());
            }
        }
        if (!tokens.isEmpty()) {
            List<PlayerEntity> players = new ArrayList<>();
            List<Long> idsOfPlayersToRemove = new LinkedList<>();
            List<Long> passiveIds = new ArrayList<>();
            List<String> userNames = new ArrayList<>();

            // remove playerEntity from game
            for (PlayerEntity player: game.getPlayers()) {
                if (!tokens.contains(player.getToken())) players.add(player);
                else {
                    idsOfPlayersToRemove.add(player.getId());
                    userNames.add(player.getUsername());
                }
            }
            // remove from passiveIds
            for(Long id: game.getPassivePlayerIds()){
                if (!idsOfPlayersToRemove.contains(id)) passiveIds.add(id);
            }

            //remove players from the ScoreBoard
            game.getScoreboard().removePlayersFromScoreBoard(userNames);

            //set new values for the game without the removed players
            game.setPlayers(players);
            game.setPassivePlayerIds(passiveIds);
            if (idsOfPlayersToRemove.contains(game.getActivePlayerId())) game.setActivePlayerId(null);
            //add bots to replace removed players
            BotCreator botCreator = new BotCreator();
            botCreator.addBots(game, 0, tokens.size());
            //lastly remove players from Map
            for (String token : tokens) {
                deadMansSwitchMap.remove(token);
            }
        }
    }

}
