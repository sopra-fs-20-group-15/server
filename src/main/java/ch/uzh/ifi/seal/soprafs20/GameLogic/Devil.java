package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.Embeddable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Embeddable
public class Devil implements Bot {
    private String botName;
    private String botToken;
    private ApiRequester apiRequester = new ApiRequester();
    private WordComparer wordComparer = new WordComparer();


    @Override
    public String giveClue(String mysteryWord, int n) {
        String returnClue = this.randomWord();
        List<String> relWords = new ArrayList<>();
        try {
            relWords = apiRequester.getFiveWordsFromDatamuseApi(mysteryWord, "ml");
        } catch (IOException ex) {
            return returnClue;
        }
        if (relWords.size() < 3) {return returnClue;}
        List<String> relRelWords = new ArrayList<>();
        try {
            relRelWords = apiRequester.getFiveWordsFromDatamuseApi(relWords.get(1), "ml");
            if (relRelWords.isEmpty()) {
                relRelWords = apiRequester.getFiveWordsFromDatamuseApi(relWords.get(2), "ml");
            }
        } catch (IOException ex) {
            return returnClue;
        }
        relWords = this.wordComparer.notSuitableBotClue(relRelWords, mysteryWord);
        if (n+1 < relWords.size()) {
            return relWords.get(n+1);
        }
        return returnClue;
    }

    private String randomWord(){
        List<String> rndWords = new ArrayList<>(Arrays.asList("Paris", "Africa", "Bread", "Money", "Garden", "House", "Tree", "Table", "Chair", "Police", "Weapon" ));
        Random rand = new Random();
        return rndWords.get(rand.nextInt(rndWords.size()));
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