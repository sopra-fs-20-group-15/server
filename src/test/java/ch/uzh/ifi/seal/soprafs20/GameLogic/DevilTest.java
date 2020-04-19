package ch.uzh.ifi.seal.soprafs20.GameLogic;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DevilTest {

    @Test
    void testGiveClueGivesAnswerForNormalWord(){
        Devil devil = new Devil();

        String actual = devil.giveClue("Tree", 0);

        assertNotNull(actual);
    }

    @Test
    void testGiveClueGivesAnswerForName(){
        Devil devil = new Devil();

        String actual = devil.giveClue("Obama", 0);

        assertNotNull(actual);
    }

    @Test
    void testGiveClueGivesAnswerForPlaces(){
        Devil devil = new Devil();

        String actual = devil.giveClue("Paris", 0);

        assertNotNull(actual);
    }

    @Test
    void testGiveClueGivesTwoNonEqualClues(){
        Devil devil = new Devil();

        String actual1 = devil.giveClue("Smoke", 0);
        String actual2 = devil.giveClue("Smoke", 1);

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertNotEquals(actual1, actual2);
    }

    @Test
    void testGiveClueWorksForBlendWords(){
        Devil devil = new Devil();

        String actual1 = devil.giveClue("emperor penguin", 0);
        String actual2 = devil.giveClue("emperor penguin", 1);

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertNotEquals(actual1, actual2);
    }

    @Test
    void testGiveClueNeglectsCase(){Devil devil = new Devil();

        String actual1 = devil.giveClue("Smoke", 0);
        String actual2 = devil.giveClue("smoke", 0);
        String actual3 = devil.giveClue("sMoKE", 0);

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertNotNull(actual3);
        assertEquals(actual1, actual2);
        assertEquals(actual2, actual3);
    }

}