package GameLogic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

class WordComparerTest {

    @Test
    void noDuplicateInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("Tree");
        words.add("live");
        words.add("house");
        ArrayList<String> actual = wordComparer.compareClues(words);
        assertEquals(words, actual);
    }

    @Test
    void OneDuplicateInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("live");
        words.add("living");
        words.add("Tree");
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Tree");
        ArrayList<String> actual = wordComparer.compareClues(words);

        assertEquals(expected, actual);
    }

    @Test
    void TwoDuplicateInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("live");
        words.add("living");
        words.add("Horse");
        words.add("horses");
        words.add("Tree");
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Tree");
        ArrayList<String> actual = wordComparer.compareClues(words);

        assertEquals(expected, actual);
    }

    @Test
    void TriplicateInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("live");
        words.add("life");
        words.add("living");
        words.add("house");
        ArrayList<String> expected = new ArrayList<>();
        expected.add("house");
        ArrayList<String> actual = wordComparer.compareClues(words);

        assertEquals(expected, actual);
    }

    @Test
    void OnlyDuplicatesInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("lives");
        words.add("lifes");
        words.add("life");
        ArrayList<String> expected = new ArrayList<>();
        ArrayList<String> actual = wordComparer.compareClues(words);

        assertEquals(expected, actual);
    }

    @Test
    void TestStemAPI() {
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
    void TestCloseWordsNoMistakes() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "Azkaban";
        String word2 = "Azkaban";

        assertTrue(wordComparer.closeWords(word1, word2));
    }

    @Test
    void TestCloseWordsDifferentCapitalization() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "ASKaBan";
        String word2 = "Askaban";

        assertTrue(wordComparer.closeWords(word1, word2));
    }

    @Test
    void TestCloseWordsNotCloseEnough() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "house";
        String word2 = "Mouse";

        assertFalse(wordComparer.closeWords(word1, word2));
    }

    @Test
    void TestCloseWordsOneMistake() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "Askaban";
        String word2 = "Azkaban";

        assertTrue(wordComparer.closeWords(word1, word2));
    }

    @Test
    void TestCloseWordsTwoMistakes() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "Askaban";
        String word2 = "Azcaban";

        assertFalse(wordComparer.closeWords(word1, word2));
    }

    @Test
    void TestCompareMysteryWordsCorrect() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "Askaban";
        String word2 = "Azkaban";

        assertTrue(wordComparer.compareMysteryWords(word1, word2));
    }

    @Test
    void TestCompareMysteryWordsWrong() {
        WordComparer wordComparer = new WordComparer();
        String word1 = "Dog";
        String word2 = "Azkaban";

        assertFalse(wordComparer.compareMysteryWords(word1, word2));
    }


}
