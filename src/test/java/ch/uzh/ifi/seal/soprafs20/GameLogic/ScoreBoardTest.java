package ch.uzh.ifi.seal.soprafs20.GameLogic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;


class ScoreBoardTest {
    private Player testPlayer;
    private List<Player> players;
    private Scoreboard sb;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setUsername("testUsername");
        testPlayer.setPassword("testPassword");
        players.add(testPlayer);
        sb= new Scoreboard(players);
    }

    @Test
    public void rightPoints(){
        sb.updateScore(testPlayer,false, 2000);
        assertEquals(sb.getEndScore().get(testPlayer), 0);
    }
}