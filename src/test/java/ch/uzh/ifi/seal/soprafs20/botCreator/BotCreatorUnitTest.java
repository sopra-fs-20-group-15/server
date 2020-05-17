package ch.uzh.ifi.seal.soprafs20.botCreator;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BotCreatorUnitTest {
    GameEntity game;
    BotCreator botCreator;
    @BeforeEach
    public void setup(){
        botCreator=new BotCreator();
        game=new GameEntity();
        PlayerEntity p1=new PlayerEntity();
        p1.setUsername("Harold");
        List<PlayerEntity> players=new ArrayList<>();
        players.add(p1);
        game.setPlayers(players);
    }
    
    @Test
    public void addBotsWithOneAngel() {
        botCreator.addBots(game, 0, 1);

        assertEquals(1, game.getAngels().size());
        assertEquals(0, game.getDevils().size());
        assertFalse(game.getAngels().get(0).getName().isBlank());
    }

    @Test
    public void addBotsWithOneDevil() {
        botCreator.addBots(game, 1, 0);

        assertEquals(0, game.getAngels().size());
        assertEquals(1, game.getDevils().size());
        assertFalse(game.getDevils().get(0).getName().isBlank());
    }

    @Test
    public void addBotsNoBots() {
        botCreator.addBots(game, 0, 0);

        assertEquals(0, game.getAngels().size());
        assertEquals(0, game.getDevils().size());
    }

    @Test
    public void addBotsDoNotUseNamesFromListIfHumansOrOtherBotsAlreadyUsesSaidName(){
        List<PlayerEntity> players=game.getPlayers();
        PlayerEntity p2=new PlayerEntity();
        PlayerEntity p3=new PlayerEntity();
        p2.setUsername("Claire");
        p3.setUsername("Jill");
        players.add(p2);
        players.add(p3);
        game.setPlayers(players);
        Angel a1=new Angel();
        Devil d1=new Devil();
        a1.setName("Chris");
        d1.setName("Leon");
        List<Angel> angels=new ArrayList<>();
        angels.add(a1);
        List<Devil> devils=new ArrayList<>();
        devils.add(d1);
        game.setAngels(angels);
        game.setDevils(devils);

        // adds bots until there are no bot names left anymore
        while (true){
            try {botCreator.addBots(game,0,1);}
            catch (IllegalArgumentException e){break;}
        }
        //Check that even though all possible names where added, Claire, Jill and Leon did not get added because they are used by human players or devil bot
        for (Angel bot : game.getAngels()) {
            assertFalse(bot.getName().equals(p2.getUsername())||bot.getName().equals(p3.getUsername())||bot.getName().equals(d1.getName()));
        }
        //Check that there is a single angel bot with the name Chris
        int cnt=0;
        for (Angel angel:game.getAngels()) {
            if (angel.getName().equals("Chris")) cnt++;
        }
        assertEquals(1,cnt);
    }
}
