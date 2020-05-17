package ch.uzh.ifi.seal.soprafs20.GameLogic;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;


import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class HandlerForLeavingPlayersUnitTest {
    GameEntity game;
    PlayerEntity p1;
    PlayerEntity p2;
    PlayerEntity p3;
    @Mock
    Map<String, Long> map;

    @BeforeEach
    public void setup(){
        game=new GameEntity();

        p1=new PlayerEntity();
        p1.setUsername("One");
        p1.setToken("OneToken");
        p1.setId(1L);

        p2=new PlayerEntity();
        p2.setUsername("Two");
        p2.setToken("TwoToken");
        p2.setId(2L);

        p3=new PlayerEntity();
        p3.setUsername("Three");
        p3.setToken("ThreeToken");
        p3.setId(3L);

        List<PlayerEntity> players=new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        game.setPlayers(players);

        game.setScoreboard(new Scoreboard());
        game.getScoreboard().initializeMap(game.getPlayers());

        List<Long> passiveIds=new LinkedList<>();
        passiveIds.add(2L);
        passiveIds.add(3L);
        game.setPassivePlayerIds(passiveIds);

        game.setActivePlayerId(1L);
    }

    @Test
    public void loadingPlayersIntoHandlerWorks() throws NoSuchFieldException, IllegalAccessException {
        game.getHandlerForLeavingPlayers().loadPlayersIntoHandler(game.getPlayers());
        Field map = HandlerForLeavingPlayers.class.getDeclaredField("deadMansSwitchMap");
        map.setAccessible(true);
        Map deadManSwitchMap = (Map) map.get(game.getHandlerForLeavingPlayers());
        //make sure that every player is correctly loaded into the map
        for (PlayerEntity player:game.getPlayers()) {
            assertTrue(deadManSwitchMap.containsKey(player.getToken()));
        }
    }

    @Test
    public void updatingDeadMansSwitchMapWorks() throws NoSuchFieldException, IllegalAccessException {
        //setup
        map=new HashMap<>();
        map.put("OneToken",0L);
        FieldSetter.setField(game.getHandlerForLeavingPlayers(), HandlerForLeavingPlayers.class.getDeclaredField("deadMansSwitchMap"),map);

        game.getHandlerForLeavingPlayers().updateDeadMansSwitch("OneToken");

        Field map = HandlerForLeavingPlayers.class.getDeclaredField("deadMansSwitchMap");
        map.setAccessible(true);
        Map deadManSwitchMap = (Map) map.get(game.getHandlerForLeavingPlayers());

        //make sure that update actually set a new value
        assertNotNull(deadManSwitchMap.get("OneToken"));
        assertNotEquals(0L,deadManSwitchMap.get("OneToken"));
    }

    @Test
    public void passivePlayerGetsRemoved() throws NoSuchFieldException {
        //setup
        map=new HashMap<>();
        map.put("OneToken",System.currentTimeMillis());
        map.put("TwoToken",System.currentTimeMillis());
        map.put("ThreeToken", 0L);

        FieldSetter.setField(game.getHandlerForLeavingPlayers(), HandlerForLeavingPlayers.class.getDeclaredField("deadMansSwitchMap"),map);

        game.getHandlerForLeavingPlayers().removeInactivePlayers(game);
        //check that leaving players were removed and replaced by bots
        assertFalse(game.getPlayers().contains(p3));
        assertEquals(2,game.getPlayers().size());
        assertFalse(game.getPassivePlayerIds().contains(p3.getId()));
        assertEquals(1,game.getPassivePlayerIds().size());
        assertEquals(1,game.getAngels().size());
    }

    @Test
    public void allPassivePlayersGetsRemoved() throws NoSuchFieldException {
        //setup
        map=new HashMap<>();
        map.put("OneToken",System.currentTimeMillis());
        map.put("TwoToken",0L);
        map.put("ThreeToken", 0L);

        FieldSetter.setField(game.getHandlerForLeavingPlayers(), HandlerForLeavingPlayers.class.getDeclaredField("deadMansSwitchMap"),map);

        game.getHandlerForLeavingPlayers().removeInactivePlayers(game);

        //check that leaving players were removed and replaced by bots
        assertFalse(game.getPlayers().contains(p2));
        assertFalse(game.getPlayers().contains(p3));
        assertEquals(1,game.getPlayers().size());
        assertTrue(game.getPassivePlayerIds().isEmpty());
        assertEquals(p1.getId(),game.getActivePlayerId());
        assertEquals(2,game.getAngels().size());
    }

    @Test
    public void activePlayerGetsRemoved() throws NoSuchFieldException {
        //setup
        map=new HashMap<>();
        map.put("OneToken",0L);
        map.put("TwoToken",System.currentTimeMillis());
        map.put("ThreeToken", System.currentTimeMillis());

        FieldSetter.setField(game.getHandlerForLeavingPlayers(), HandlerForLeavingPlayers.class.getDeclaredField("deadMansSwitchMap"),map);

        game.getHandlerForLeavingPlayers().removeInactivePlayers(game);

        //check that leaving players were removed and replaced by bots
        assertFalse(game.getPlayers().contains(p1));
        assertEquals(2,game.getPlayers().size());
        assertNull(game.getActivePlayerId());
        assertEquals(1,game.getAngels().size());
    }

    @Test
    public void passivePlayerAndActivePlayerGetsRemoved() throws NoSuchFieldException {
        //setup
        map=new HashMap<>();
        map.put("OneToken",0L);
        map.put("TwoToken",System.currentTimeMillis());
        map.put("ThreeToken", 0L);

        FieldSetter.setField(game.getHandlerForLeavingPlayers(), HandlerForLeavingPlayers.class.getDeclaredField("deadMansSwitchMap"),map);

        game.getHandlerForLeavingPlayers().removeInactivePlayers(game);

        //check that leaving players were removed and replaced by bots
        assertFalse(game.getPlayers().contains(p1));
        assertFalse(game.getPlayers().contains(p3));
        assertEquals(1,game.getPlayers().size());
        assertTrue(game.getPassivePlayerIds().contains(p2.getId()));
        assertEquals(1,game.getPassivePlayerIds().size());
        assertEquals(2,game.getAngels().size());
    }
}
