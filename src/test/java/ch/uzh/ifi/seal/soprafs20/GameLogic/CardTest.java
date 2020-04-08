package ch.uzh.ifi.seal.soprafs20.GameLogic;


import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumber;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumberbetweenOneAndFive;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

public class CardTest {

    @Test
    public void NonNumberShouldThrowError() {
        Card card = new Card();
        String wordId = "ajkdlsfjkldsaf";

        NotANumber thrown = assertThrows(NotANumber.class, () -> {
            card.chooseWordOnCard(wordId);
        });

        assertTrue(thrown.getMessage().contains("The input should be an integer!"));
    }

    @Test
    public void EmptyStringShouldThrowError() {
        Card card = new Card();
        String wordId = "";

        NotANumber thrown = assertThrows(NotANumber.class, () -> {
            card.chooseWordOnCard(wordId);
        });

        assertTrue(thrown.getMessage().contains("The input should be an integer!"));
    }

    @Test
    public void ZeroShouldThrowError() {
        Card card = new Card();
        String wordId = "0";

        NotANumberbetweenOneAndFive thrown = assertThrows(NotANumberbetweenOneAndFive.class, () -> {
            card.chooseWordOnCard(wordId);
        });

        assertTrue(thrown.getMessage().contains("The input should be between 1 and 5!"));
    }
    @Test
    public void SixShouldThrowError() {
        Card card = new Card();
        String wordId = "6";

        NotANumberbetweenOneAndFive thrown = assertThrows(NotANumberbetweenOneAndFive.class, () -> {
            card.chooseWordOnCard(wordId);
        });

        assertTrue(thrown.getMessage().contains("The input should be between 1 and 5!"));
    }
    @Test
    public void OneShouldWork() {
        Card card = new Card();
        String wordId = "1";
        List<String> words = new ArrayList<String>();
        words.add("Kuchen");
        words.add("Kaffeeeis");
        words.add("Karamell");
        words.add("Keks");
        words.add("Kirschtorte");
        card.setWords(words);


        assertEquals("Kuchen", card.chooseWordOnCard(wordId));
    }
    @Test
    public void FiveShouldWork() {
        Card card = new Card();
        String wordId = "5";
        List<String> words = new ArrayList<String>();
        words.add("Kuchen");
        words.add("Kaffeeeis");
        words.add("Karamell");
        words.add("Keks");
        words.add("Kirschtorte");
        card.setWords(words);


        assertEquals("Kirschtorte", card.chooseWordOnCard(wordId));
    }
}
