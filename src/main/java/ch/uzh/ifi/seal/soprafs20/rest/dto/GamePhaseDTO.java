package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class GamePhaseDTO {

    String phase;

    int phaseNumber;

    long timeStart;

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public int getPhaseNumber() {
        return phaseNumber;
    }

    public void setPhaseNumber(int phaseNumber) {
        this.phaseNumber = phaseNumber;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }
}
