package ch.uzh.ifi.seal.soprafs20.GameLogic;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WordComparer {

    /*
        @param: List of clues
        @Return: List of validClued
     */
    public Map<String, Integer> compareClues(ArrayList<String> clues, String mysteryWord) {
        Map<String, Integer> returnMap = new HashMap<>();
        for (String clue: clues) {
            returnMap.put(clue, 0);
        }
        String mysteryStem;
        try {
            mysteryStem = this.getWordStem(mysteryWord.toLowerCase());
        } catch(IOException ex) {
            mysteryStem = mysteryWord.toLowerCase();
        }
        ArrayList<String> wordStems = new ArrayList<>();
        for (String word : clues) {
            String stem;
            try {
                stem = this.getWordStem(word.toLowerCase());
            } catch(IOException ex) {
                stem = word.toLowerCase();
            }//get the word stem from API
            wordStems.add(stem);    //add stem to stemList
        }
        for (int i = 0; i < clues.size(); i++) {
            int count = -1;
            //check that it doesn't contain mysteryWord
            if (clues.get(i).toLowerCase().contains(mysteryStem) || wordStems.get(i).equals(mysteryStem)){
                count++;
            }
            for (int j = 0; j < clues.size(); j++) {
                if (this.closeWords(clues.get(i), clues.get(j)) || wordStems.get(i).equals(wordStems.get(j))) {
                    count++;
                }
            }
            returnMap.put(clues.get(i), count);
        }

        return returnMap;
    }


    public boolean compareMysteryWords(String guess, String mysteryWord){
        return closeWords(guess, mysteryWord);
    }

    /*
    gets two strings and returns boolean if they are closely the same
     */
    protected boolean closeWords(String word1, String word2){
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();
        if (word1.length() != word2.length()){
            return false;
        }
        int count = 0;
        if (word1.length() < 2){
            return word1.equals(word2);
        }
        if (word1.charAt(0) != word2.charAt(0)) {
            return false;
        }
        for (int i = 1; i < word1.length(); i++) {
            if (word1.charAt(i) == word2.charAt(i)){
                count++;
            }
        }

        return count >= word1.length()-2;
    }

    protected String getWordStem(String s) throws IOException {
        String apiAnswer;

        String urlParameters = "text=" + s;
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        String request = "http://text-processing.com/api/stem/";
        URL url = new URL( request );
        HttpURLConnection conn= (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
        conn.setUseCaches(false);
        try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
            //int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            apiAnswer = response.toString();
        }
        return convertApiAnswer(apiAnswer);
    }

    /**
     * @param s the string StemApiForm
     * @return  word Stem
     */
    private String convertApiAnswer(String s){
        int middle = s.indexOf(":");
        return s.substring(middle+3, s.length()-2);

    }

}
