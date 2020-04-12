package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.Embeddable;

@Embeddable
public class Angel implements Bot {
    private String botName;
    private String botToken;

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


