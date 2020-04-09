package GameLogic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class WordComparer {

    /*
        @param: List of clues
        @Return: List of validClued
     */
    public ArrayList<String> compareClues(ArrayList<String> clues) {
        ArrayList<String> okClues = new ArrayList<>();
        ArrayList<String> tmpClues = new ArrayList<>();
        ArrayList<String> wordStems = new ArrayList<>();
        ArrayList<String> duplicates = new ArrayList<>();
        for (String word : clues) {
            String stem;
            try {
                stem = this.getWordStem(word.toLowerCase());
            } catch(IOException ex) {
                stem = word.toLowerCase();
            }//get the word stem from API
            if (wordStems.contains(stem)){duplicates.add(stem);} //if stem is already in wordStems, it's a duplicate
            wordStems.add(stem);    //add stem to stemList
        }
        for (int i = 0; i < clues.size(); i++) {

            if (!duplicates.contains(wordStems.get(i))){    //only add words that are not duplicates
                tmpClues.add(clues.get(i));
            }
        }
        //checks that none of the clues are to close
        for (String okClue : tmpClues){
            int count = 0;
            for (String word : clues){
                if (this.closeWords(okClue, word)){
                    count++;
                }
            }
            if (count < 2) {
                okClues.add(okClue);
            }
        }

        return okClues;
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
