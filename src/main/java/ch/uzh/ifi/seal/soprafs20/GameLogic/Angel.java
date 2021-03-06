package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.Embeddable;
import java.io.*;
import java.util.*;

@Embeddable
public class Angel implements Bot {
    private String botName;
    private String botToken;
    private Random rand=new Random();


    @Override
    public String giveClue(String mysteryWord, int n) {
        int length = mysteryWord.length();
        String line = "Error";
        if (n > 4) {
            return this.giveClueFromApi(mysteryWord, n);
        }

        try (BufferedReader bufReader = new BufferedReader(new FileReader("cardsEnAngel"))) {
            line = bufReader.readLine();
            while (line != null && !line.substring(0, length).equalsIgnoreCase(mysteryWord)) {
                line = bufReader.readLine();
            }
        } catch (IOException e) {
            return this.giveClueFromApi(mysteryWord, n);
        }
        if (line == null || line.isBlank()) {
            return this.giveClueFromApi(mysteryWord, n);
        }
        String returnClue = this.correctClueFromLine(line, n);

        return returnClue;
    }

    protected String correctClueFromLine(String line, int n) {
        if (0 <= n  && n <= 3){
            int start = line.indexOf(String.valueOf(n)) + 2;
            int end = line.indexOf(String.valueOf(n+1)) - 1;
            return line.substring(start, end);
        } else if (n == 4) {
            return line.substring(line.indexOf(String.valueOf(n))+2).trim();
        } else {
            return "Error";
        }
    }

    protected String giveClueFromApi(String mysteryWord, int n) {
        ApiRequester apiRequester = new ApiRequester();
        WordComparer wordComparer = new WordComparer();
        String returnClue = this.apiFailureClue();
        List<String> clues = new ArrayList<>();
        try {
            clues = apiRequester.getFiveWordsFromDatamuseApi(mysteryWord, "rel_trg");
        }
        catch (IOException ex) {}
        this.stripWords(clues);
        wordComparer.notSuitableBotClue(clues, mysteryWord);
        //if api didn't give back useful words
        if (clues.size() < 3) {
            try {
                clues = apiRequester.getFiveWordsFromDatamuseApi(mysteryWord, "ml");
                this.stripWords(clues);
                wordComparer.notSuitableBotClue(clues, mysteryWord);
            }
            catch (IOException ex) { }
        }

        if (n < clues.size()) {
            return clues.get(n);
        }
        return returnClue;
    }

    private String apiFailureClue() {
        List<String> emergencyWords = new ArrayList<>(Arrays.asList("Paris", "economy", "Africa", "Bread", "Money", "Garden", "House", "Tree", "Table", "Chair", "Police", "Weapon" ));
        return emergencyWords.get(rand.nextInt(emergencyWords.size()));
    }

    protected void stripWords(List<String> words){
        int size = words.size();
        for (int i = 0; i < size; i++) {
            if(words.get(i).contains(" ")) {
                String newWord = words.get(i).substring(words.get(i).indexOf(" ")+1);
                if (!words.contains(newWord)) {
                words.add(newWord);
                }
            }
        }
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


