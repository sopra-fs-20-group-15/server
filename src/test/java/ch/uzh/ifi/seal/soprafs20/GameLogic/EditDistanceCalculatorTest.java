package ch.uzh.ifi.seal.soprafs20.GameLogic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class EditDistanceCalculatorTest {

    @Test
    void worksForEmptyStrings() {
        assertEquals(EditDistanceCalculator.calculateEditDistance("", ""), 0);
    }

    @Test
    void sameWordsHasEditDistanceZero() {
        assertEquals(EditDistanceCalculator.calculateEditDistance("test", "test"), 0);
    }

    @Test
    void capitalizationDoesNotMatter() {
        assertEquals(EditDistanceCalculator.calculateEditDistance("tEst", "tesT"), 0);
    }

    @Test
    void oneDeletionHasEditDistanceOne() {
        assertEquals(EditDistanceCalculator.calculateEditDistance("testt", "test"), 1);
    }

    @Test
    void oneAdditionHasEditDistanceOne() {
        assertEquals(EditDistanceCalculator.calculateEditDistance("test", "testt"), 1);
    }

    @Test
    void oneChangeHasEditDistanceOne() {
        assertEquals(EditDistanceCalculator.calculateEditDistance("tesk", "test"), 1);
    }

    @Test
    void multipleChangesWorks() {
        assertEquals(EditDistanceCalculator.calculateEditDistance("tedk", "test"), 2);
    }

    @Test
    void multipleDeletesWorks() {
        assertEquals(EditDistanceCalculator.calculateEditDistance("test", "tester"), 2);
    }

    @Test
    void deleteAndChangeWorks() {
        assertEquals(EditDistanceCalculator.calculateEditDistance("test", "tisto"), 2);
    }

    @Test
    void multipleDeletionsAndChangesWorks() {
        assertEquals(EditDistanceCalculator.calculateEditDistance("Spaagethy", "Spaghetti"), 4);
    }

}