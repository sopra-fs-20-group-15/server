package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.Embeddable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Embeddable
public class Devil implements Bot {
    private String botName;
    private String botToken;
    private Random rand=new Random();


    @Override
    public String giveClue(String mysteryWord, int n) {
        int length = mysteryWord.length();
        String line;
        try (BufferedReader bufReader = new BufferedReader(new FileReader("cardsEnDevil"))) {
            line = bufReader.readLine();
            while (line != null && !line.substring(0, length).equalsIgnoreCase(mysteryWord)) {
                line = bufReader.readLine();
            }
        } catch (IOException e) {
            return this.giveClueFromApi(mysteryWord, n);
        }
        if (line != null) {
            return this.getRndClueFromLine(line.substring(length+2), n);
        }
        return this.giveClueFromApi(mysteryWord, n);
    }

    protected String getRndClueFromLine(String line, int n) {
        if (n < 0 || n> 4) {
            return this.randomWord();
        }
        String[] words = line.split(" ");
        int r = words.length/5;
        if (words.length % 5 > n) {
            r++;
        }
        int rn = ThreadLocalRandom.current().nextInt(r) * 5+ n;
        return words[rn];
    }

    protected String giveClueFromApi(String mysteryWord, int n) {
        ApiRequester apiRequester = new ApiRequester();
        WordComparer wordComparer = new WordComparer();
        String returnClue = this.randomWord();
        List<String> relWords;
        try {
            relWords = apiRequester.getFiveWordsFromDatamuseApi(mysteryWord, "ml");
        } catch (IOException ex) {
            return returnClue;
        }
        if (relWords.size() < 3) {return returnClue;}
        List<String> moleClues;
        try {
            moleClues = apiRequester.getFiveWordsFromDatamuseApi(relWords.get(1), "ml");
            if (moleClues.isEmpty()) {        //safety feature if DataMuse doesn't contain answer for above word
                moleClues = apiRequester.getFiveWordsFromDatamuseApi(relWords.get(2), "ml");
            }
        } catch (IOException ex) {
            return returnClue;
        }

        this.removeToHelpfulClues(relWords, moleClues);

        wordComparer.notSuitableBotClue(moleClues, mysteryWord);
        if (n+1 < relWords.size()) {
            return relWords.get(n+1);
        }
        return returnClue;
    }

    protected void removeToHelpfulClues(List<String> tooCloseWords, List<String> moleClues) {
        Iterator<String> i = moleClues.iterator();
        while (i.hasNext()) {
            String s = i.next();
            if (tooCloseWords.contains(s)) {
                i.remove();
            }
        }

    }

    private String randomWord(){
        List<String> rndWords = new ArrayList<>(Arrays.asList("Paris", "blue", "green", "white", "yellow", "purple", "smelly", "unpopular", "cheap", "death", "Religion", "Africa", "Bread", "Money", "Garden", "House", "Tree", "Table", "Chair", "Police", "Weapon" ));
        return rndWords.get(rand.nextInt(rndWords.size()));
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