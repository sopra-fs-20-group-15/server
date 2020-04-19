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

}