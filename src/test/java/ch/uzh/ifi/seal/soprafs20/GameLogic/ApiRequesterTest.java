package ch.uzh.ifi.seal.soprafs20.GameLogic;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/**Checks, that the APIs used for the bots and the wordComparer work and return the correct answer
 * Datamuse API: gives back related words
 * Stem API: Gets the word stem*/
class ApiRequesterTest {



    @Test
    void getFiveWordsFromDatamuseApiDoesGetAnswer() {
        ApiRequester apiRequester = new ApiRequester();
        List<String> actual = new ArrayList<>();
        try {
            actual = apiRequester.getFiveWordsFromDatamuseApi("test", "ml"/*ml = means like*/);
        } catch (IOException ex)  {}

        assertTrue(!actual.isEmpty());
    }

    @Test
    void getFiveWordsFromDatamuseApiGetsRelatedWord() {
        ApiRequester apiRequester = new ApiRequester();
        List<String> actual = new ArrayList<>();
        try {
            actual = apiRequester.getFiveWordsFromDatamuseApi("Smoke", "rel_trg"/*rel_trg = related word triggers: "Triggers" (words that are statistically associated with the query word in the same piece of text.)cow → milking*/);
        } catch (IOException ex)  {}

        assertTrue(actual.contains("inhalation"));
    }

    /**Checks, if live is the word stem of "live" and "living"*/
    @Test
    void TestStemAPI() {
        ApiRequester apiRequester = new ApiRequester();
        String expected = "live";
        String actual1;
        String actual2;
        try {
            actual1 = apiRequester.getWordStem("live");
            actual2 = apiRequester.getWordStem("living");
        } catch(IOException ex) {
            actual1 = "";
            actual2 = "";
        }
        assertEquals(expected, actual1);
        assertEquals(expected, actual2);
    }

}