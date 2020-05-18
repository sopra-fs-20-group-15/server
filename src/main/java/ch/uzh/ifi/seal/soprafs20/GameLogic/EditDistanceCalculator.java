package ch.uzh.ifi.seal.soprafs20.GameLogic;


import javax.persistence.Embeddable;
import java.io.IOException;
import java.util.*;

@Embeddable
public class EditDistanceCalculator {

    public int calculate1337EditDistance(String w1, String w2){
        int[][] DPArray = new int[w2.length()+1][w1.length()+1];
        for (int i = 0; i < w1.length()+1; i++) {
            DPArray[0][i] = i;
        }
        for (int i = 0; i < w2.length()+1; i++) {
            DPArray[i][0] = i;
        }
        for (int j = 0; j < w2.length(); j++){
            for (int i = 0; i < w1.length(); i++) {

                if (convertCharFromLeet(w1.toLowerCase().charAt(i)) == convertCharFromLeet(w2.toLowerCase().charAt(j))) {
                    DPArray[j+1][i+1] = DPArray[j][i];
                } else
                    DPArray[j+1][i+1] = 1 + Math.min(DPArray[j][i+1], Math.min(DPArray[j][i], DPArray[j+1][i]));
            }
        }
        return DPArray[w2.length()][w1.length()];
    }

    public char convertCharFromLeet(char c) {
        String leet = "48310572";
        String alph = "abelostz";
        int i = leet.indexOf(c);
        if ( 0 <= i) {
            c = alph.charAt(i);
        };
        return c;
    }

}
