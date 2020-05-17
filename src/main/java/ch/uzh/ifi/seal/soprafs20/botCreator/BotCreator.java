package ch.uzh.ifi.seal.soprafs20.botCreator;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;

import javax.persistence.Embeddable;
import java.util.*;

public class BotCreator {
    private final String[] names = {"Jill","Albert","Chris","Leon", "Claire", "Ada","Rebecca", "Barry", "Sheva", "Carlos", "Sherry",
            "Ashley", "Jack", "Hunk", "Helena", "Marvin", "Anette"};
    private List<String> nameList= new LinkedList<>(Arrays.asList(names));
    private Random rand=new Random();

    public void addBots(GameEntity game, int numOfDevils, int numOfAngels){
        List<Angel> angels;
        List<Devil> devils;

        if (game.getAngels()!=null) angels=game.getAngels();
        else angels=new ArrayList<>();
        if (game.getDevils()!=null) devils=game.getDevils();
        else devils=new ArrayList<>();

        // remove names from the list of possible names, if bots or humans already use that username
        for (PlayerEntity player : game.getPlayers()) {
            nameList.remove(player.getUsername());
        }
        for (Angel angel : angels) {
            nameList.remove(angel.getName());
        }
        for (Devil devil : devils) {
            nameList.remove(devil.getName());
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
    }
}
