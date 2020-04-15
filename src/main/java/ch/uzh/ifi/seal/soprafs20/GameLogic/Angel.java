package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.Embeddable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Angel implements Bot {
    private String botName;
    private String botToken;

    @Override
    public String giveClue(String mysteryWord) {
        return null;
    }

    public void printCluesToAllmysteryWords() throws IOException {
        BufferedReader bufReader = new BufferedReader(new FileReader("cardsEn.txt"));
        ArrayList<String> listOfLines = new ArrayList<>();
        String line = bufReader.readLine();
        while (line != null) {
            listOfLines.add(line);
            line = bufReader.readLine();
        }
        bufReader.close();
        for (String word : listOfLines){
            if (!word.isBlank()){
                System.out.print(word + ": ");
                List<String> clues = this.getFiveCluesFromApi(word);
                for (String clue : clues){
                    System.out.print(clue + ", ");
                }
                System.out.print("\n");
            }
        }
    }

    protected List<String> getFiveCluesFromApi(String mysteryWord) throws IOException {
        String transformedWord = this.transformWordForApi(mysteryWord);
        String url = "https://api.datamuse.com/words?rel_trg=" + transformedWord + "&max=5";
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

        List<String> clues = new ArrayList<>();
        while (response.indexOf("word") >= 0){
            int indexStartOfCLue = response.indexOf("word") + 7;
            response = response.delete(0, indexStartOfCLue);
            int indexEndOfClue = response.indexOf(",") -1;
            clues.add(response.substring(0, indexEndOfClue));
        }
        return clues;
    }

    protected String transformWordForApi(String word){
        return word.replace(" ", "+");
    }


    public void setName(String botName) {
        this.botName = botName;
    }

    public String getName() {
        return botName;
    }

    public void setToken(String botToken) {
        this.botToken = botToken;
    }

    public String getToken() {
        return botToken;
    }
}


