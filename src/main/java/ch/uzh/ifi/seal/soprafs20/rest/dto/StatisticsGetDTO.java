package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class StatisticsGetDTO {
    int placement;
    String PlayerName;
    int score;
    int numberOfCorrectlyGuessedMysteryWords;

    public int getPlacement() {
        return placement;
    }

    public void setPlacement(int placement) {
        this.placement = placement;
    }

    public String getPlayerName() {
        return PlayerName;
    }

    public void setPlayerName(String playerName) {
        PlayerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getNumberOfCorrectlyGuessedMysteryWords() {
        return numberOfCorrectlyGuessedMysteryWords;
    }

    public void setNumberOfCorrectlyGuessedMysteryWords(int numberOfCorrectlyGuessedMysteryWords) {
        this.numberOfCorrectlyGuessedMysteryWords = numberOfCorrectlyGuessedMysteryWords;
    }
}
