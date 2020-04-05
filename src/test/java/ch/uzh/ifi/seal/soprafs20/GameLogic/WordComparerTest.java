package ch.uzh.ifi.seal.soprafs20.GameLogic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;



class WordComparerTest {

    @Test
    public void noDuplicateInClues(){
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<String>();
        words.add("test1");
        words.add("test2");
        words.add("test3");
        ArrayList<String> actual = wordComparer.compareClues(words);
        assertEquals(words, actual);
    }

    @Test
    public void OneDuplicateInClues(){
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<String>();
        words.add("test1");
        words.add("test1");
        words.add("test3");
        ArrayList<String> expected = new ArrayList<String>();
        expected.add("test3");
        ArrayList<String> actual = wordComparer.compareClues(words);
        assertEquals(expected, actual);
    }

    @Test
    public void TwoDuplicateInClues(){
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<String>();
        words.add("test1");
        words.add("test1");
        words.add("test2");
        words.add("test2");
        words.add("test3");
        ArrayList<String> expected = new ArrayList<String>();
        expected.add("test3");
        ArrayList<String> actual = wordComparer.compareClues(words);
        assertEquals(expected, actual);
    }

    @Test
    public void TriplicateInClues(){
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<String>();
        words.add("test1");
        words.add("test1");
        words.add("test1");
        words.add("test3");
        ArrayList<String> expected = new ArrayList<String>();
        expected.add("test3");
        ArrayList<String> actual = wordComparer.compareClues(words);
        assertEquals(expected, actual);
    }

    @Test
    public void OnlyDuplicatesInClues(){
        WordComparer wordComparer = new WordComparer();
        ArrayList<String> words = new ArrayList<String>();
        words.add("test1");
        words.add("test1");
        words.add("test1");
        ArrayList<String> expected = new ArrayList<String>();
        ArrayList<String> actual = wordComparer.compareClues(words);
        assertEquals(expected, actual);
    }

    @Test
    public void TestStemAPI(){
        WordComparer wordComparer = new WordComparer();
        String expected = "princ";
        String actual1 = wordComparer.simulateApi("prince");
        String actual2 = wordComparer.simulateApi("princess");
        assertEquals(expected, actual1);
        assertEquals(expected, actual2);
    }




}