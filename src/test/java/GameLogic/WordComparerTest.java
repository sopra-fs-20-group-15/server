package GameLogic;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

class WordComparerTest {

    @Test
    public void noDuplicateInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<String>();
        words.add("Tree");
        words.add("live");
        words.add("house");
        ArrayList<String> actual = wordComparer.compareClues(words);
        assertEquals(words, actual);
    }

    @Test
    public void OneDuplicateInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<String>();
        words.add("live");
        words.add("living");
        words.add("Tree");
        ArrayList<String> expected = new ArrayList<String>();
        expected.add("Tree");
        ArrayList<String> actual = wordComparer.compareClues(words);

        assertEquals(expected, actual);
    }

    @Test
    public void TwoDuplicateInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<String>();
        words.add("live");
        words.add("living");
        words.add("Horse");
        words.add("horses");
        words.add("Tree");
        ArrayList<String> expected = new ArrayList<String>();
        expected.add("Tree");
        ArrayList<String> actual = wordComparer.compareClues(words);

        assertEquals(expected, actual);
    }

    @Test
    public void TriplicateInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<String>();
        words.add("live");
        words.add("life");
        words.add("living");
        words.add("house");
        ArrayList<String> expected = new ArrayList<String>();
        expected.add("house");
        ArrayList<String> actual = wordComparer.compareClues(words);

        assertEquals(expected, actual);
    }

    @Test
    public void OnlyDuplicatesInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<String>();
        words.add("lives");
        words.add("lifes");
        words.add("life");
        ArrayList<String> expected = new ArrayList<String>();
        ArrayList<String> actual = wordComparer.compareClues(words);

        assertEquals(expected, actual);
    }

    @Test
    public void TestStemAPI() {
        WordComparer wordComparer = new WordComparer();
        String expected = "live";
        String actual1;
        String actual2;
        try {
            actual1 = wordComparer.getWordStem("live");
            actual2 = wordComparer.getWordStem("living");
        } catch(IOException ex) {
            actual1 = "";
            actual2 = "";
        }
        assertEquals(expected, actual1);
        assertEquals(expected, actual2);
    }

    @Test
    public void TestCloseWordsNoMistakes() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "Azkaban";
        String word2 = "Azkaban";

        assertTrue(wordComparer.closeWords(word1, word2));
    }

    @Test
    public void TestCloseWordsDifferentCapitalization() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "ASKaBan";
        String word2 = "Askaban";

        assertTrue(wordComparer.closeWords(word1, word2));
    }

    @Test
    public void TestCloseWordsNotCloseEnough() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "house";
        String word2 = "Mouse";

        assertFalse(wordComparer.closeWords(word1, word2));
    }

    @Test
    public void TestCloseWordsOneMistake() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "Askaban";
        String word2 = "Azkaban";

        assertTrue(wordComparer.closeWords(word1, word2));
    }

    @Test
    public void TestCloseWordsTwoMistakes() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "Askaban";
        String word2 = "Azcaban";

        assertFalse(wordComparer.closeWords(word1, word2));
    }

    @Test
    public void TestCompareMysteryWordsCorrect() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "Askaban";
        String word2 = "Azkaban";

        assertTrue(wordComparer.compareMysteryWords(word1, word2));
    }

    @Test
    public void TestCompareMysteryWordsWrong() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "Dog";
        String word2 = "Azkaban";

        assertFalse(wordComparer.compareMysteryWords(word1, word2));
    }


}
