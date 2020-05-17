package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.Embeddable;
import java.io.*;
import java.util.*;

@Embeddable
public class Angel implements Bot {
    private String botName;
    private String botToken;
    private Random rand=new Random();


    @Override
    public String giveClue(String mysteryWord, int n) {
        ApiRequester apiRequester = new ApiRequester();
        WordComparer wordComparer = new WordComparer();
        String returnClue = this.apiFailureClue();
        List<String> clues = new ArrayList<>();
        try { clues = apiRequester.getFiveWordsFromDatamuseApi(mysteryWord, "ml");
        } catch (IOException ex) {
            returnClue = this.apiFailureClue();
        }
        this.stripWords(clues);
        wordComparer.notSuitableBotClue(clues, mysteryWord);
        //if api didn't give back useful words
        if (clues.size() < 2) {
            try { clues = apiRequester.getFiveWordsFromDatamuseApi(mysteryWord, "ml");
            } catch (IOException ex) {
                returnClue = this.apiFailureClue();
            }
        }
        this.stripWords(clues);
        wordComparer.notSuitableBotClue(clues, mysteryWord);

        for (String word : clues) {
            System.out.println(word);
        }
        if (n < clues.size()) {
            return clues.get(n);
        }
        return returnClue;
    }

    private String apiFailureClue() {
        List<String> emergencyWords = new ArrayList<>(Arrays.asList("Paris", "economy", "Africa", "Bread", "Money", "Garden", "House", "Tree", "Table", "Chair", "Police", "Weapon" ));
        return emergencyWords.get(rand.nextInt(emergencyWords.size()));
    }

    protected void stripWords(List<String> words){
        int size = words.size();
        for (int i = 0; i < size; i++) {
            if(words.get(i).contains(" ")) {
                String newWord = words.get(i).substring(words.get(i).indexOf(" ")+1);
                if (!words.contains(newWord)) {
                words.add(newWord);
                }
            }
        }
    }

    public void setName(String botName) {
        this.botName = botName;
    }

    public String getName() {
        return botName;
    }

    public void setToken(String botToken) {
        this.botToken = botToken;
    }

    public String getToken() {
        return botToken;
    }
}


