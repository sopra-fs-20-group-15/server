package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.Embeddable;

@Embeddable
public class Turn {
    private Boolean hasBeenInitialized;
    private Boolean hasEnded;
    private Long timeStart;

    public void setHasBeenInitialized(Boolean hasBeenInitialized) {
        this.hasBeenInitialized = hasBeenInitialized;
    }

    public Boolean getHasBeenInitialized() {
        return hasBeenInitialized;
    }

    public void setHasEnded(Boolean hasEnded) {
        this.hasEnded = hasEnded;
    }

    public Boolean getHasEnded() {
        return hasEnded;
    }

    public void setTimeStart(Long timeStart) {
        this.timeStart = timeStart;
    }

    public Long getTimeStart() {
        return timeStart;
    }
}
