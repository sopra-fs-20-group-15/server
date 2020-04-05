package GameLogic;

import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumber;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumberbetweenOneAndFive;

import java.util.ArrayList;
import java.util.List;

public class Card {
    List<String> words = new ArrayList<String>();


    /**
     * functions about controlling and getting the Id of a word on a card
     */

    boolean stringIsAnInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    public String chooseWordOnCardByNumber(int number){
        return words.get(number);
    }

    String chooseWordOnCard(String wordId){
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
