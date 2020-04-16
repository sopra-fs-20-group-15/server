package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.Embeddable;

@Embeddable
public class Devil implements Bot {
    private String botName;
    private String botToken;


    @Override
    public String giveClue(String mysteryWord) {
        return null;
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