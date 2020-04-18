package ch.uzh.ifi.seal.soprafs20.GameLogic;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class ScoreCalculatorTest {
    @Test
    public void correctValuesActivePlayer(){
        PlayerEntity player = new PlayerEntity();
        player.setUsername("peeeelaiiiir");
        player.setTimePassed(13000L);
        assertEquals(25, ScoreCalculator.calculateScoreActivePlayer(player,true));
        player.setTimePassed(23000L);
        assertEquals(23, ScoreCalculator.calculateScoreActivePlayer(player,true));
        player.setTimePassed(23500L);
        assertEquals(21, ScoreCalculator.calculateScoreActivePlayer(player,true));
        assertEquals(0,ScoreCalculator.calculateScoreActivePlayer(player,false));
    }

    @Test
    public void correctValuesPassivePlayer(){
        PlayerEntity player = new PlayerEntity();
        player.setUsername("peeeelaiiiir");
        player.setTimePassed(8000L);
        assertEquals(21, ScoreCalculator.calculateScorePassivePlayer(player,true,true, 0));
        player.setTimePassed(13000L);
        assertEquals(20, ScoreCalculator.calculateScorePassivePlayer(player,true,true, 0));
        player.setTimePassed(18000L);
        assertEquals(19, ScoreCalculator.calculateScorePassivePlayer(player,true,true, 0));
        player.setTimePassed(23000L);
        assertEquals(18, ScoreCalculator.calculateScorePassivePlayer(player,true,true, 0));
        player.setTimePassed(28000L);
        assertEquals(17, ScoreCalculator.calculateScorePassivePlayer(player,true,true, 0));
        player.setTimePassed(33000L);
        assertEquals(16, ScoreCalculator.calculateScorePassivePlayer(player,true,true, 0));

        player.setTimePassed(5000L);
        assertEquals(-10, ScoreCalculator.calculateScorePassivePlayer(player,true,false, 5));

        player.setTimePassed(5000L);
        assertEquals(-25, ScoreCalculator.calculateScorePassivePlayer(player,false,false, 5));
    }
}
