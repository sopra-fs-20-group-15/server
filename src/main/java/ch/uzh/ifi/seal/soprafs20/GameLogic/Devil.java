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
    private Random rand=new Random();


    @Override
    public String giveClue(String mysteryWord, int n) {
        ApiRequester apiRequester = new ApiRequester();
        WordComparer wordComparer = new WordComparer();
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
        relWords = wordComparer.notSuitableBotClue(relRelWords, mysteryWord);
        if (n+1 < relWords.size()) {
            return relWords.get(n+1);
        }
        return returnClue;
    }

    private String randomWord(){
        List<String> rndWords = new ArrayList<>(Arrays.asList("Paris", "blue", "green", "white", "yellow", "purple", "smelly", "unpopular", "cheap", "death", "Religion", "Africa", "Bread", "Money", "Garden", "House", "Tree", "Table", "Chair", "Police", "Weapon" ));
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