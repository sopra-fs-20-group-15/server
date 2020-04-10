package ch.uzh.ifi.seal.soprafs20.GameLogic;


import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumber;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumberbetweenOneAndFive;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    //These tests are not needed anymore since only Longs can be given now
/**
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
*/
    @Test
    public void ZeroShouldThrowError() {
        CardService cardService = new CardService(cardRepository);
        CardEntity card = new CardEntity();
        Long wordId = 0L;

        NotANumberbetweenOneAndFive thrown = assertThrows(NotANumberbetweenOneAndFive.class, () -> {
            cardService.chooseWordOnCard(wordId, card);
        });

        assertTrue(thrown.getMessage().contains("The input should be between 1 and 5!"));
    }

    /*
    @Test
    public void cardRepositoryGetsFilled() throws IOException {
        CardService cardService = new CardService(cardRepository);
        cardService.addAllCards();
        //CardEntity card = cardService.getCardById(0L);
        List<String> words= testCard.getWords();
        String word = words.get(0);
        Long id = testCard.getId();
        Long exptected = 0L;

    }*/

    @Test
    public void SixShouldThrowError() {
        CardService cardService = new CardService(cardRepository);
        CardEntity card = new CardEntity();
        Long wordId = 6L;

        NotANumberbetweenOneAndFive thrown = assertThrows(NotANumberbetweenOneAndFive.class, () -> {
            cardService.chooseWordOnCard(wordId, card);
        });

        assertTrue(thrown.getMessage().contains("The input should be between 1 and 5!"));
    }
    @Test
    public void OneShouldWork() {
        CardService cardService = new CardService(cardRepository);
        CardEntity card = new CardEntity();
        Long wordId = 1L;
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
        Long wordId = 5L;
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
