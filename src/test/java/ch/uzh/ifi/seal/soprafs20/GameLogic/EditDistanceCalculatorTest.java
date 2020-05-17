package ch.uzh.ifi.seal.soprafs20.GameLogic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EditDistanceCalculatorTest {

    @Test
    void leetTranslatorWorks() {
        EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();
        assertEquals(editDistanceCalculator.convertCharFromLeet('0'), 'o');
        assertEquals(editDistanceCalculator.convertCharFromLeet('4'), 'a');
        assertEquals(editDistanceCalculator.convertCharFromLeet('1'), 'l');
        assertEquals(editDistanceCalculator.convertCharFromLeet('7'), 't');
        assertEquals(editDistanceCalculator.convertCharFromLeet('8'), 'b');
        assertEquals(editDistanceCalculator.convertCharFromLeet('3'), 'e');
        assertEquals(editDistanceCalculator.convertCharFromLeet('5'), 's');
        assertEquals(editDistanceCalculator.convertCharFromLeet('2'), 'z');
    }

    @Test
    void worksForEmptyStrings() {
        EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();
        assertEquals(editDistanceCalculator.calculate1337EditDistance("", ""), 0);
    }

    @Test
    void sameWordsHasEditDistanceZero() {
        EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();
        assertEquals(editDistanceCalculator.calculate1337EditDistance("test", "test"), 0);
    }

    @Test
    void capitalizationDoesNotMatter() {
        EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();
        assertEquals(editDistanceCalculator.calculate1337EditDistance("tEst", "tesT"), 0);
    }

    @Test
    void oneDeletionHasEditDistanceOne() {
        EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();
        assertEquals(editDistanceCalculator.calculate1337EditDistance("testt", "test"), 1);
    }

    @Test
    void oneAdditionHasEditDistanceOne() {
        EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();
        assertEquals(editDistanceCalculator.calculate1337EditDistance("test", "testt"), 1);
    }

    @Test
    void oneChangeHasEditDistanceOne() {
        EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();
        assertEquals(editDistanceCalculator.calculate1337EditDistance("tesk", "test"), 1);
    }

    @Test
    void multipleChangesWorks() {
        EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();

        assertEquals(editDistanceCalculator.calculate1337EditDistance("tedk", "test"), 2);
    }

    @Test
    void multipleDeletesWorks()
    {
        EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();
        assertEquals(editDistanceCalculator.calculate1337EditDistance("test", "tester"), 2);
    }

    @Test
    void deleteAndChangeWorks() {
        EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();
        assertEquals(editDistanceCalculator.calculate1337EditDistance("test", "tisto"), 2);
    }

    @Test
    void multipleDeletionsAndChangesWorks() {
        EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();
        assertEquals(editDistanceCalculator.calculate1337EditDistance("Spaagethy", "Spaghetti"), 4);
    }

    @Test
    void editDistanceWorksFor1337() {
        EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();
        assertEquals(editDistanceCalculator.calculate1337EditDistance("Sp4gh377i", "Spaghetti"), 0);
    }

    @Test
    void multipleDeletionsAndChangesWorksFor1337() {
        EditDistanceCalculator editDistanceCalculator = new EditDistanceCalculator();
        assertEquals(editDistanceCalculator.calculate1337EditDistance("Sp44g37hy", "Spaghetti"), 4);
    }

}