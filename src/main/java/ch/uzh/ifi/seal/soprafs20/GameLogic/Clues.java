package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.Map;

@Embeddable
public class Clues {
    @ElementCollection
    private Map<String, String> clueMap;
    @ElementCollection
    private Map<String,String> validClues;
    @ElementCollection
    private Map<String,Integer> analyzedClues;

    private boolean validCluesAreSet;

    public void setClueMap(Map<String, String> clueMap) {
        this.clueMap = clueMap;
    }

    public Map<String, String> getClueMap() {
        return clueMap;
    }

    public void setValidClues(Map<String, String> validClues) {
        this.validClues = validClues;
    }

    public Map<String, String> getValidClues() {
        return validClues;
    }

    public void setAnalyzedClues(Map<String, Integer> analyzedClues) {
        this.analyzedClues = analyzedClues;
    }

    public Map<String, Integer> getAnalyzedClues() {
        return analyzedClues;
    }

    public void setValidCluesAreSet(boolean validCluesAreSet) {
        this.validCluesAreSet = validCluesAreSet;
    }

    public boolean getValidCluesAreSet() {
        return validCluesAreSet;
    }
}
