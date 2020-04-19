package ch.uzh.ifi.seal.soprafs20.GameLogic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ApiRequester {




    //"ml" = means like, "rel_trg" = triggerword,
    public List<String> getFiveWordsFromDatamuseApi(String mysteryWord, String requestType) throws IOException {
        String transformedWord = this.transformWordForApi(mysteryWord);
        String url = "https://api.datamuse.com/words?" + requestType + "=" + transformedWord + "&max=5";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        List<String> clues = this.transformApiAnswer(response);
        return clues;
    }

    public String getWordStem(String r) throws IOException {
        String s = r.toLowerCase();
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
        return convertStemApiAnswer(apiAnswer);
    }

    /**
     * @param s the string StemApiForm
     * @return  word Stem
     */
    private String convertStemApiAnswer(String s){
        int middle = s.indexOf(":");
        return s.substring(middle+3, s.length()-2);

    }

    private List<String> transformApiAnswer(StringBuffer response){
        List<String> clues = new ArrayList<>();
        while (response.indexOf("word") >= 0){
            int indexStartOfCLue = response.indexOf("word") + 7;
            response = response.delete(0, indexStartOfCLue);
            int indexEndOfClue = response.indexOf(",") -1;
            clues.add(response.substring(0, indexEndOfClue));
        }
        return clues;
    }


    private String transformWordForApi(String word){
        return word.replace(" ", "+");
    }

}
