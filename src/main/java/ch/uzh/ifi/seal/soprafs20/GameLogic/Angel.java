package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.Embeddable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Angel implements Bot {
    private String botName;
    private String botToken;


    @Override
    public String giveClue(String mysteryWord, int n) {
        ApiRequester apiRequester = new ApiRequester();
        WordComparer wordComparer = new WordComparer();
        String returnClue = "Earth";
        List<String> clues = new ArrayList<>();
        try { clues = apiRequester.getFiveWordsFromDatamuseApi(mysteryWord, "rel_trg");
        } catch (IOException ex) {
            returnClue = "USA";
        }
        //if api didn't give back usefull words
        if (clues.size() < 2) {
            try { clues = apiRequester.getFiveWordsFromDatamuseApi(mysteryWord, "ml");
            } catch (IOException ex) {
                returnClue = "USA";
            }
        }
        clues = wordComparer.notSuitableBotClue(clues, mysteryWord);
        if (n < clues.size()) {
            return clues.get(n);
        }
        return returnClue;
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


