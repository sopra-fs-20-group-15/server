package ch.uzh.ifi.seal.soprafs20.chat;

import javax.persistence.Embeddable;

@Embeddable
public class ChatMessage {
    private  String playerName;
    private  String message;
    private  Long time;

    public void initChatMessage(String playerName,String message, Long time){
        this.playerName=playerName;
        this.message=message;
        this.time=time;
    }


    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTime() {
        return time;
    }
}
