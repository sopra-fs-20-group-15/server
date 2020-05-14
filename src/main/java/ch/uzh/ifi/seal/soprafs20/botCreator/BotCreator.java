package ch.uzh.ifi.seal.soprafs20.botCreator;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;

import java.util.*;

public class BotCreator {
    private final String[] names = {"Jill","Albert","Chris","Leon", "Claire", "Ada","Rebecca", "Barry", "Sheva", "Carlos", "Sherry",
            "Ashley", "Jack", "Hunk", "Helena", "Marvin", "Anette"};
    private List<String> nameList= new LinkedList<>(Arrays.asList(names));
    private Random rand=new Random();

    public GameEntity addBots(GameEntity game, int numOfDevils, int numOfAngels){
        List<Angel> angels=new ArrayList<>();
        List<Devil> devils=new ArrayList<>();
        List<String> humanNames=new ArrayList<>();
        for (PlayerEntity player : game.getPlayers()) {
            nameList.remove(player.getUsername());
        }
        //Add angles
        for (int i = 1; i <= numOfAngels; i++) {
            Angel bot = new Angel();
            String name= nameList.get(rand.nextInt(nameList.size()));
            bot.setName(name);
            bot.setToken(name +"Token");
            angels.add(bot);
            nameList.remove(name);
        }
        //Add devils
        for (int i = 1; i <= numOfDevils; i++) {
            Devil bot = new Devil();
            String name= nameList.get(rand.nextInt(nameList.size()));
            bot.setName(name);
            bot.setToken(name +"Token");
            devils.add(bot);
            nameList.remove(name);
        }
        game.setAngels(angels);
        game.setDevils(devils);
        return game;
    }
}
