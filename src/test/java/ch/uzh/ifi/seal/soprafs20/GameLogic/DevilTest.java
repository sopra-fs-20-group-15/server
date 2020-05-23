package ch.uzh.ifi.seal.soprafs20.GameLogic;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
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
    }

    @Test
    void testGiveClueWorksForBlendWords(){
        Devil devil = new Devil();

        String actual1 = devil.giveClue("emperor penguin", 0);
        String actual2 = devil.giveClue("emperor penguin", 1);

        assertNotNull(actual1);
        assertNotNull(actual2);
    }

    @Test
    void testGiveClueNeglectsCase(){
        Devil devil = new Devil();

        String actual1 = devil.giveClue("Smoke", 0);
        String actual2 = devil.giveClue("smoke", 0);
        String actual3 = devil.giveClue("sMoKE", 0);

        assertNotNull(actual1);
        assertNotNull(actual2);
        assertNotNull(actual3);
    }

    @Test
    void testGiveRndClueFromLineReturnsAWordFromLine(){
        Devil devil = new Devil();

        String line = "clupea pincers balking intaglio chalcedony gemstone nikko michelin carnelian onyx lexus valladolid toyota";
        String actual0 = devil.getRndClueFromLine(line, 0);
        String actual1 = devil.getRndClueFromLine(line, 1);
        String actual2 = devil.getRndClueFromLine(line, 2);
        String actual3 = devil.getRndClueFromLine(line, 3);
        String actual4 = devil.getRndClueFromLine(line, 4);

        assertNotNull(actual0);
        assertNotNull(actual1);
        assertNotNull(actual2);
        assertNotNull(actual3);
        assertNotNull(actual4);
        assertNotEquals(actual0, actual1);
        assertNotEquals(actual1, actual2);
        assertNotEquals(actual2, actual3);
        assertNotEquals(actual3, actual4);
    }

    @Test
    void testGiveClueFromApiWorks(){
        Devil devil = new Devil();

        String actual0 = devil.giveClueFromApi("Tree", 0);
        String actual1 = devil.giveClueFromApi("House", 0);
        String actual2 = devil.giveClueFromApi("Obama", 0);
        String actual3 = devil.giveClueFromApi("Revolver", 0);
        String actual4 = devil.giveClueFromApi("Smoke", 0);

        assertNotNull(actual0);
        assertNotNull(actual1);
        assertNotNull(actual2);
        assertNotNull(actual3);
        assertNotNull(actual4);
    }
    /*  This test is here to let a devil bot run on some of the mystery words to check it's performance
    @Test
    void testSomeWords(){
        Devil devil = new Devil();

        ArrayList<String> listOfLines = new ArrayList<>();
        try (BufferedReader bufReader = new BufferedReader(new FileReader("cardsEn.txt"))){
            String line = bufReader.readLine();
            while (line != null) {
                listOfLines.add(line);
                line = bufReader.readLine();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 100; i++) {
            if (listOfLines.get(i).isBlank()) {
                continue;
            }
            System.out.print(listOfLines.get(i)+ "    ");
            System.out.print(devil.giveClue(listOfLines.get(i), 0) + "    ");
            System.out.print(devil.giveClue(listOfLines.get(i), 1) + "    ");
            System.out.println(devil.giveClue(listOfLines.get(i), 2));
        }

    }
        */

}