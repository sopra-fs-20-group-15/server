package ch.uzh.ifi.seal.soprafs20.GameLogic;

import java.util.ArrayList;

public class WordComparer {
    private ArrayList<String> helperWords;

    public ArrayList<String> compareClues(ArrayList<String> clues){
        ArrayList<String> okClues = new ArrayList<String>();
        ArrayList<String> wordStems = new ArrayList<String>();
        ArrayList<String> duplicates = new ArrayList<String>();
        for (String word : clues) {
            String stem = this.simulateApi(word);       //get the word stem from API
            if (wordStems.contains(stem)){duplicates.add(stem);} //if stem is already in wordStems, it's a duplicate
            wordStems.add(stem);    //add stem to stemlist
        }
        for (int i = 0; i < clues.size(); i++) {

            if (!duplicates.contains(wordStems.get(i))){    //only add words that are not duplicates
                okClues.add(clues.get(i));
            }
        }
        return okClues;
    }

    public String simulateApi(String s) {
        if (s == "test1") {return "test1";}
        if (s == "test2") {return "test2";}
        return "princ";
    }

}
