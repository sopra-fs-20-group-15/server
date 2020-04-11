package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.Embeddable;

@Embeddable
public class Bot{
    private String botName;
    private String botToken;

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public String getBotName() {
        return botName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getBotToken() {
        return botToken;
    }
}
