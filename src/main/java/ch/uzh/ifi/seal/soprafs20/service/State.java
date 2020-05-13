package ch.uzh.ifi.seal.soprafs20.service;

public enum State {
    ChooseMysteryWord(1), GiveClues(2), GiveGuess(3), WordReveal(4), hasEnded(10);

    private int numVal;

    State(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
