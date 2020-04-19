package ch.uzh.ifi.seal.soprafs20.GameLogic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class WordComparerTest {


    @Test
    void noDuplicateInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("Tree");
        words.add("live");
        words.add("house");
        String mysteryWord = "Alcatraz";
        Map<String, Integer> actual = wordComparer.compareClues(words, mysteryWord);
        assertEquals(0, actual.get("Tree"));
        assertEquals(0, actual.get("live"));
        assertEquals(0, actual.get("house"));
    }

    @Test
    void OneDuplicateInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("live");
        words.add("living");
        words.add("Tree");
        String mysteryWord = "Alcatraz";

        Map<String, Integer> actual = wordComparer.compareClues(words, mysteryWord);
        assertEquals(1, actual.get("living"));
        assertEquals(1, actual.get("live"));
        assertEquals(0, actual.get("Tree"));
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
        String mysteryWord = "Alcatraz";
        Map<String, Integer> actual = wordComparer.compareClues(words, mysteryWord);
        assertEquals(1, actual.get("living"));
        assertEquals(1, actual.get("live"));
        assertEquals(0, actual.get("Tree"));
        assertEquals(1, actual.get("Horse"));
        assertEquals(1, actual.get("horses"));
    }

    @Test
    void TriplicateInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("live");
        words.add("live");
        words.add("living");
        words.add("house");
        String mysteryWord = "Alcatraz";
        Map<String, Integer> actual = wordComparer.compareClues(words, mysteryWord);
        assertEquals(2, actual.get("living"));
        assertEquals(0, actual.get("house"));
        assertEquals(2, actual.get("live"));
    }


    @Test
    void OnlyDuplicatesInClues() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("live");
        words.add("living");
        words.add("lives");
        String mysteryWord = "Alcatraz";
        Map<String, Integer> actual = wordComparer.compareClues(words, mysteryWord);
        assertEquals(2, actual.get("lives"));
        assertEquals(2, actual.get("live"));
        assertEquals(2, actual.get("living"));
    }

    @Test
    void ClueToCloseToMysteryWord() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("living");
        String mysteryWord = "live";
        Map<String, Integer> actual = wordComparer.compareClues(words, mysteryWord);
        assertEquals(1, actual.get("living"));
    }

    @Test
    void notASuitableBotClueOkClue() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("house");
        words.add("Tree");
        String mysteryWord = "live";
        List<String> actual = wordComparer.notSuitableBotClue(words, mysteryWord);
        assertEquals(words.get(0), actual.get(0));
        assertEquals(words.get(1), actual.get(1));
    }

    @Test
    void notASuitableBotClueOneWordIsMW() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("live");
        words.add("house");
        words.add("Tree");
        String mysteryWord = "live";
        List<String> actual = wordComparer.notSuitableBotClue(words, mysteryWord);
        words.remove("live");
        assertEquals(words.get(0), actual.get(0));
        assertEquals(words.get(1), actual.get(1));
    }

    @Test
    void notASuitableBotClueOneWordTooClose() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("living");
        words.add("house");
        words.add("Tree");
        String mysteryWord = "live";
        List<String> actual = wordComparer.notSuitableBotClue(words, mysteryWord);
        words.remove("living");
        assertEquals(words.get(0), actual.get(0));
        assertEquals(words.get(1), actual.get(1));
    }

    @Test
    void notASuitableBotClueTwoWordsTooClose() {
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<>();
        words.add("living");
        words.add("live");
        words.add("Tree");
        String mysteryWord = "live";
        List<String> actual = wordComparer.notSuitableBotClue(words, mysteryWord);
        words.remove("living");
        words.remove("live");
        assertEquals(words.get(0), actual.get(0));
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
