package ch.uzh.ifi.seal.soprafs20.GameLogic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AngelTest {

    @Test
    void testGiveClueGivesAnswerForNormalWord(){
        Angel angel = new Angel();

        String actual = angel.giveClue("Rose", 0);

        assertNotNull(actual);
        assertEquals("love", actual);
    }

    @Test
    void testGiveClueGivesAnswerForName(){
        Angel angel = new Angel();

        String actual = angel.giveClue("Obama", 0);

        assertNotNull(actual);
    }

    @Test
    void testGiveClueGivesAnswerForPlaces(){
        Angel angel = new Angel();

        String actual = angel.giveClue("Paris", 0);

        assertNotNull(actual);
    }

    /**Bots are not allowed to give two times the same clue, since otherwise there would be too many invalid clues in a round played with a lot of bots*/
    @Test
    void testGiveClueGivesTwoNonEqualClues(){
        Angel angel = new Angel();

        String actual1 = angel.giveClue("Smoke", 0);
        String actual2 = angel.giveClue("Smoke", 1);


        assertNotNull(actual1);
        assertNotNull(actual2);
        assertNotEquals(actual1, actual2);
    }

    @Test
    void testGiveClueWorksForBlendWords(){
        Angel angel = new Angel();

        String actual1 = angel.giveClue("emperor penguin", 0);
        String actual2 = angel.giveClue("emperor penguin", 1);

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertNotEquals(actual1, actual2);
    }

    @Test
    void testClueFromApi(){
        Angel angel = new Angel();

        String actual1 = angel.giveClueFromApi("emperor penguin", 0);
        String actual2 = angel.giveClueFromApi("emperor penguin", 1);

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertNotEquals(actual1, actual2);
    }

    @Test
    void testGiveClueNeglectsCase(){
        Angel angel = new Angel();

        String actual1 = angel.giveClue("Smoke", 0);
        String actual2 = angel.giveClue("smoke", 0);
        String actual3 = angel.giveClue("sMoKE", 0);


        assertNotNull(actual1);
        assertNotNull(actual2);
        assertNotNull(actual3);
        assertEquals(actual1, actual2);
        assertEquals(actual2, actual3);
        assertEquals(actual1, "inhalation");
    }

    @Test
    void testAngelReturnsCorrectClueFromLine(){
        Angel angel = new Angel();

        String line = "Australia: 0.english 1.kangaroo 2.perth 3.adelaide 4.papua ";

        assertEquals(angel.correctClueFromLine(line, 0), "english");
        assertEquals(angel.correctClueFromLine(line, 1), "kangaroo");
        assertEquals(angel.correctClueFromLine(line, 2), "perth");
        assertEquals(angel.correctClueFromLine(line, 3), "adelaide");
        assertEquals(angel.correctClueFromLine(line, 4), "papua");
    }

}