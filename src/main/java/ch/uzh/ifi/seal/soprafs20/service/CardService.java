package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
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
import java.util.Random;
/**CardService: Provides all services that have to do with cards or words on cards*/
@Service
@Transactional
public class CardService {

    private final CardRepository cardRepository;
    private final Random rand = new Random();

    @Autowired
    public CardService(@Qualifier("cardRepository") CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public CardEntity getCardById(Long id){
        Optional<CardEntity> cardOp = cardRepository.findById(id);
        if (cardOp.isEmpty()) throw new NotFoundException("No card with this id exists");
        return cardOp.get();
    }

    /**Reads all cards from the cardsEn.txt file and puts five words each into a card Entity. Then saves all entities into repository*/
    protected void addAllCards() throws IOException {
        if (!(cardRepository.findAll().isEmpty())) {
            return;
        }
        ArrayList<String> listOfLines;
        try (BufferedReader bufReader = new BufferedReader(new FileReader("cardsEn.txt"))){
            listOfLines = new ArrayList<>();
            String line = bufReader.readLine();
            while (line != null) {
                listOfLines.add(line);
                line = bufReader.readLine();
            }
        }
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

    /**
    Returns a List with 13 CardIds randomly chosen from the CardRepository. In case the card repository
     has not been initialized with the card from Just One yets, it calls addAllCards (to CardRepository)
     @Returns: List<Long>: with 13 cardIds
     */
    public List<Long> getFullStackOfCards(){
        try {this.addAllCards();
        } catch (IOException ex) {
            throw new NoContentException("The CardDatabase couldn't be filled");
        }
        List<Long> cardIds = new ArrayList<>();
        List<CardEntity> allCards = cardRepository.findAll();
        int stackSize = 13;
        for (int i = 0; i < stackSize; i++){
            int randomIndex = rand.nextInt(allCards.size());
            CardEntity randomCard = allCards.get(randomIndex);
            Long randomCardId = randomCard.getId();
            allCards.remove(randomIndex);
            cardIds.add(randomCardId);
        }
        return cardIds;
    }

    /**Helper for ChooseWordOnCard*/
    protected String chooseWordOnCardByNumber(Long number, CardEntity card){
        int i = number.intValue();
        return card.getWords().get(i-1);
    }

    /**Chooses a word on a card by its number (index 1 = first word on card, index 5 = last word on card))
     * @Param: Long wordId: number between one and five
     * @Param: CardEntity card: card from which a word shall be chosen
     * @Returns: Word on the card by index
     * @Throws: 409: If number is not between 1 and 5 (1 and 5 included)
     * */
    public String chooseWordOnCard(Long wordId, CardEntity card){
            // Is the number between one end five
            if (wordId> 0 && wordId < 6){
                //In card I think
                return chooseWordOnCardByNumber(wordId, card);
            }
            else{
                throw new ConflictException("The input should be between 1 and 5!");
            }
        }
}

