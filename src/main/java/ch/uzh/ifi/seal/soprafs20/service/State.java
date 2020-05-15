package ch.uzh.ifi.seal.soprafs20.service;

public enum State {
    ChooseMysteryWord(1,30000),
    GiveClues(2,50000),
    GiveGuess(3, 60000),
    WordReveal(4, 10000),
    hasEnded(10, 0);

    private int numVal;
    private int phaseDuration;

    State(int numVal, int phaseDuration) {
        this.numVal = numVal;
        this.phaseDuration = phaseDuration;
    }

    public int getNumVal() {
        return numVal;
    }

    public int getPhaseDuration() {
        return phaseDuration;
    }
}
