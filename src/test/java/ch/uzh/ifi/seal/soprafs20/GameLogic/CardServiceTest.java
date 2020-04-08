package ch.uzh.ifi.seal.soprafs20.GameLogic;


import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumber;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumberbetweenOneAndFive;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private CardEntity testCard;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testCard = new CardEntity();
        List<String> words = new ArrayList<String>();
        words.add("Kuchen");
        words.add("Kaffeeeis");
        words.add("Karamell");
        words.add("Keks");
        words.add("Kirschtorte");
        testCard.setWords(words);


        // when -> any object is being save in the playerRepository -> return the dummy testPlayer
        Mockito.when(cardRepository.save(Mockito.any())).thenReturn(testCard);
    }

    @Test
    public void NonNumberShouldThrowError() {
        CardService cardService = new CardService(cardRepository);
        CardEntity card = new CardEntity();
        String wordId = "ajkdlsfjkldsaf";

        NotANumber thrown = assertThrows(NotANumber.class, () -> {
            cardService.chooseWordOnCard(wordId, card);
        });

        assertTrue(thrown.getMessage().contains("The input should be an integer!"));
    }

    @Test
    public void EmptyStringShouldThrowError() {
        CardService cardService = new CardService(cardRepository);
        CardEntity card = new CardEntity();
        String wordId = "";

        NotANumber thrown = assertThrows(NotANumber.class, () -> {
            cardService.chooseWordOnCard(wordId, card);
        });

        assertTrue(thrown.getMessage().contains("The input should be an integer!"));
    }

    @Test
    public void ZeroShouldThrowError() {
        CardService cardService = new CardService(cardRepository);
        CardEntity card = new CardEntity();
        String wordId = "0";

        NotANumberbetweenOneAndFive thrown = assertThrows(NotANumberbetweenOneAndFive.class, () -> {
            cardService.chooseWordOnCard(wordId, card);
        });

        assertTrue(thrown.getMessage().contains("The input should be between 1 and 5!"));
    }
    @Test
    public void SixShouldThrowError() {
        CardService cardService = new CardService(cardRepository);
        CardEntity card = new CardEntity();
        String wordId = "6";

        NotANumberbetweenOneAndFive thrown = assertThrows(NotANumberbetweenOneAndFive.class, () -> {
            cardService.chooseWordOnCard(wordId, card);
        });

        assertTrue(thrown.getMessage().contains("The input should be between 1 and 5!"));
    }
    @Test
    public void OneShouldWork() {
        CardService cardService = new CardService(cardRepository);
        CardEntity card = new CardEntity();
        String wordId = "1";
        List<String> words = new ArrayList<String>();
        words.add("Kuchen");
        words.add("Kaffeeeis");
        words.add("Karamell");
        words.add("Keks");
        words.add("Kirschtorte");
        card.setWords(words);



        assertEquals("Kuchen", cardService.chooseWordOnCard(wordId, card));
    }
    @Test
    public void FiveShouldWork() {
        CardEntity card = new CardEntity();
        String wordId = "5";
        List<String> words = new ArrayList<String>();
        words.add("Kuchen");
        words.add("Kaffeeeis");
        words.add("Karamell");
        words.add("Keks");
        words.add("Kirschtorte");
        card.setWords(words);


        assertEquals("Kirschtorte", cardService.chooseWordOnCard(wordId, card));
    }
}
