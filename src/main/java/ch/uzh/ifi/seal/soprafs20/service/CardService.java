package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumberbetweenOneAndFive;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CardService {

    private final CardRepository cardRepository;

    @Autowired
    public CardService(@Qualifier("cardRepository") CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    protected boolean stringIsAnInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    public CardEntity getCardById(Long id){
        Optional<CardEntity> cardOp = cardRepository.findById(id);
        if (cardOp.isEmpty()) throw new NotFoundException("No card with this id exists");
        return cardOp.get();
    }

    protected String chooseWordOnCardByNumber(Long number, CardEntity card){
        int i = number.intValue();
        return card.getWords().get(i-1);
    }

    public void addAllCards() throws IOException {
        BufferedReader bufReader = new BufferedReader(new FileReader("cardsEn.txt"));
        ArrayList<String> listOfLines = new ArrayList<>();
        String line = bufReader.readLine();
        while (line != null) {
            listOfLines.add(line);
            line = bufReader.readLine();
        }
        bufReader.close();

        for (int i = 0; i < listOfLines.size(); i= i+6) {
            CardEntity card = new CardEntity();
            List<String> words = new ArrayList<>();
            for(int j = 0; j < 5; j++){
                words.add(listOfLines.get(i+j));
            }
            card.setWords(words);

            cardRepository.save(card);
            cardRepository.flush();
        }
    }

    public String chooseWordOnCard(Long wordId, CardEntity card){

            // Is the number between one end five
            if (wordId> 0 && wordId < 6){
                //In card I think
                return chooseWordOnCardByNumber(wordId, card);
            }
            else{
                throw new NotANumberbetweenOneAndFive("The input should be between 1 and 5!");
            }
        }
}
