package ch.uzh.ifi.seal.soprafs20.GameLogic;


import javax.persistence.Embeddable;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        for (int i = 0; i < clues.size(); i++) {
            int count = -1;
            //check that it doesn't contain mysteryWord
            if (clues.get(i).toLowerCase().contains(mysteryStem) || wordStems.get(i).equals(mysteryStem)){
                count++;
            }
            for (int j = 0; j < clues.size(); j++) {
                if (this.closeWords(clues.get(i), clues.get(j)) || wordStems.get(i).equals(wordStems.get(j))) {
                    count++;
                }
            }
            returnMap.put(clues.get(i), count);
        }

        return returnMap;
    }

    public List<String> notSuitableBotClue(List<String> words, String mysteryWord){
        ApiRequester apiRequester = new ApiRequester();

        List<String> okWords = new ArrayList<>();
        String mysteryStem;
        try {
            mysteryStem = apiRequester.getWordStem(mysteryWord.toLowerCase());
        } catch(IOException ex) {
            mysteryStem = mysteryWord.toLowerCase();
        }
        for(String word: words){
            String stem;
            try {
                stem = apiRequester.getWordStem(word.toLowerCase());
            } catch(IOException ex) {
                stem = word.toLowerCase();
            }//get the word stem from API
            if (!(word.toLowerCase().contains(mysteryStem) || mysteryWord.toLowerCase().contains(stem) || stem.equals(mysteryStem) ||word.contains(" "))) {
                okWords.add(word);
            }
        }
        return okWords;
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
