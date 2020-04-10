package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class CardPostDTO {

    private Long wordId;
    private String playerToken;

    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public String getPlayerToken() {
        return playerToken;
    }

    public void setPlayerToken(String playerToken) {
        this.playerToken = playerToken;
    }
}
