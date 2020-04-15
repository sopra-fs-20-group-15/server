package ch.uzh.ifi.seal.soprafs20.GameLogic;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class AngelTest {

    @Test
    void testApiPerformance(){
        Angel angel = new Angel();
        try {
            angel.printCluesToAllmysteryWords();
        } catch (IOException ex) {
            return;
        }
    }


}