package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.Embeddable;
import java.io.IOException;
import java.util.*;

@Embeddable
public class WordComparer {

    /*
        @param: List of clues
        @Return: List of validClued
     */
    public Map<String, Integer> compareClues(ArrayList<String> clues, String mysteryWord) {
        ApiRequester apiRequester = new ApiRequester();

        Map<String, Integer> returnMap = new HashMap<>();
        for (String clue: clues) {
            returnMap.put(clue, 0);
        }
        String mysteryStem;
        try {
            mysteryStem = apiRequester.getWordStem(mysteryWord.toLowerCase());
        } catch(IOException ex) {
            mysteryStem = mysteryWord.toLowerCase();
        }
        ArrayList<String> wordStems = this.getClueStems(clues);

        for (int i = 0; i < clues.size(); i++) {
            int count = -1;
            //check that it doesn't contain mysteryWord
            if (this.toCloseToMysteryWord(mysteryWord, mysteryStem, clues.get(i), wordStems.get(i))){
                count++;
            }
            for (int j = 0; j < clues.size(); j++) {
                if (this.closeWords(clues.get(i), clues.get(j)) || wordStems.get(i).equals(wordStems.get(j)) ||
                        (clues.get(j).contains(clues.get(i)) && clues.get(i).length() > 3)|| (clues.get(i).contains(clues.get(j)) && clues.get(j).length() > 3)) {
                    count++;
                }
            }
            returnMap.put(clues.get(i), count);
        }

        return returnMap;
    }

    //for efficiency reason we give this method the stems, so we minimize api Requests
    protected boolean toCloseToMysteryWord(String mWord, String mStem, String clue, String clueStem){
        mWord = mWord.toLowerCase();
        clue = clue.toLowerCase();
        //Compare their stems
        if(clue.contains(mStem) || mWord.contains(clueStem)||clueStem.equals(mStem)){
            return true;
        }
        //make sure clue doesn't contain MysteryWord, neither in reversed
        if (this.containsMysteryWord(mWord, clue)){
            return true;
        }
        //Checks that it's not to close to mysteryWord
        if (this.closeWords(mWord, clue)) {
            return true;
        }
        return false;
    }

    protected boolean containsMysteryWord(String mWord, String clue){
        if (mWord.length() == 0 ){  //makes sure there is no out of bounds error
            return true;
        }
        int j = mWord.length()-1;
        int i = 0;
        for (char c : clue.toLowerCase().toCharArray()){
            if (c == mWord.toLowerCase().charAt(i)){
                i++;
            }
            if (c == mWord.toLowerCase().charAt(j)){
                j--;
            }
            if (i == mWord.length() || j == -1) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> getClueStems(List<String> clues) {
        ApiRequester apiRequester = new ApiRequester();
        ArrayList<String> wordStems = new ArrayList<>();
        for (String word : clues) {
            String stem;
            try {
                stem = apiRequester.getWordStem(word.toLowerCase());
            } catch(IOException ex) {
                stem = word.toLowerCase();
            }//get the word stem from API
            wordStems.add(stem);    //add stem to stemList
        }
        return wordStems;
    }

    public void notSuitableBotClue(List<String> words, String mysteryWord){
        ApiRequester apiRequester = new ApiRequester();

        String mysteryStem;
        try {
            mysteryStem = apiRequester.getWordStem(mysteryWord.toLowerCase());
        } catch(IOException ex) {
            mysteryStem = mysteryWord.toLowerCase();
        }

        Iterator<String> i = words.iterator();
        while (i.hasNext()) {
            String word = i.next();
            String stem;
            try {
                stem = apiRequester.getWordStem(word.toLowerCase());
            } catch(IOException ex) {
                stem = word.toLowerCase();
            }//get the word stem from API
            if ((word.toLowerCase().contains(mysteryStem) || mysteryWord.toLowerCase().contains(stem) || stem.equals(mysteryStem) ||word.contains(" "))) {
                i.remove();

            }
        }
    }

    public boolean compareMysteryWords(String guess, String mysteryWord){

        return closeWords(guess, mysteryWord);
    }

    /*
    gets two strings and returns boolean if they are closely the same
     */
    protected boolean closeWords(String word1, String word2){
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (word1.length() != word2.length()){
            return false;
        }
        int count = 0;
        if (word1.length() < 2){
            return word1.equals(word2);
        }
        if (word1.charAt(0) != word2.charAt(0)) {
            return false;
        }
        for (int i = 1; i < word1.length(); i++) {
            if (word1.charAt(i) == word2.charAt(i)){
                count++;
            }
        }

        return count >= word1.length()-2;
    }



}
