package ch.uzh.ifi.seal.soprafs20.GameLogic;

public class EditDistanceCalculator {

    public static int calculateEditDistance(String w1, String w2){
        int[][] DPArray = new int[w2.length()+1][w1.length()+1];
        for (int i = 0; i < w1.length()+1; i++) {
            DPArray[0][i] = i;
        }
        for (int i = 0; i < w2.length()+1; i++) {
            DPArray[i][0] = i;
        }
        for (int j = 0; j < w2.length(); j++){
            for (int i = 0; i < w1.length(); i++) {

                if (w1.toLowerCase().charAt(i) == w2.toLowerCase().charAt(j)) {
                    DPArray[j+1][i+1] = DPArray[j][i];
                } else
                    DPArray[j+1][i+1] = 1 + Math.min(DPArray[j][i+1], Math.min(DPArray[j][i], DPArray[j+1][i]));
            }


        }

        return DPArray[w2.length()][w1.length()];
    }

}
