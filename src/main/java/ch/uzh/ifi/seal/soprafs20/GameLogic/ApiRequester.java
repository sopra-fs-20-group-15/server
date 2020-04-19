package ch.uzh.ifi.seal.soprafs20.GameLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
