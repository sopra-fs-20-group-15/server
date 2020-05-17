package ch.uzh.ifi.seal.soprafs20.GameLogic;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.botCreator.BotCreator;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Embeddable
public class HandlerForLeavingPlayers {
    @ElementCollection
    Map<String,Long> deadMansSwitchMap= new HashMap<>();

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
        //remove players from game
        game.removePlayersFromGameByToken(tokens);
        //add bots to replace removed players
        BotCreator botCreator= new BotCreator();
        botCreator.addBots(game,0,tokens.size());
        //lastly remove players from Map
        for (String token:tokens) {
            deadMansSwitchMap.remove(token);
        }
    }
}
