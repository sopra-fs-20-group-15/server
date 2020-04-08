package ch.uzh.ifi.seal.soprafs20.GameLogic;


import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumber;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumberbetweenOneAndFive;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    protected String chooseWordOnCardByNumber(int number, CardEntity card){
        return card.get(number-1);
    }

    public String chooseWordOnCard(String wordId){
        if (stringIsAnInteger(wordId))
        {
            int wordIdAsInt = Integer.parseInt(wordId);
            // Is the number between one end five
            if (wordIdAsInt > 0 && wordIdAsInt < 6){
                //In card I think
                return chooseWordOnCardByNumber(wordIdAsInt);
            }
            else{
                throw new NotANumberbetweenOneAndFive("The input should be between 1 and 5!");
            }
        }
        else{
            throw new NotANumber("The input should be an integer!");
        }
    }
}
