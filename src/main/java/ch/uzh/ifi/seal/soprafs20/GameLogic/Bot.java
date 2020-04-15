package ch.uzh.ifi.seal.soprafs20.GameLogic;

public interface Bot {
    public String giveClue(String mysteryWord);
    public void setName(String name);
    public void setToken(String token);
    public String getName();
    public String getToken();
}
