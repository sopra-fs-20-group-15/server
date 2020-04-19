package ch.uzh.ifi.seal.soprafs20.GameLogic;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiRequesterTest {



    @Test
    void getFiveWordsFromDatamuseApiDoesGetAnswer() {
        ApiRequester apiRequester = new ApiRequester();
        List<String> actual = new ArrayList<>();
        try {
            actual = apiRequester.getFiveWordsFromDatamuseApi("test", "ml");
        } catch (IOException ex)  {}

        assertTrue(!actual.isEmpty());
    }

    @Test
    void getFiveWordsFromDatamuseApiGetsRelatedWord() {
        ApiRequester apiRequester = new ApiRequester();
        List<String> actual = new ArrayList<>();
        try {
            actual = apiRequester.getFiveWordsFromDatamuseApi("Smoke", "rel_trg");
        } catch (IOException ex)  {}

        assertTrue(actual.contains("inhalation"));
    }

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