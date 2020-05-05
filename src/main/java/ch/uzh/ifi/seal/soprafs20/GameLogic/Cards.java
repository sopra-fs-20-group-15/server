package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.List;

@Embeddable
public class Cards {
    @ElementCollection
    private List<Long> CardIds;
    private Long activeCardId;
    private String activeMysteryWord;

    public void setCardIds(List<Long> cardIds) {
        CardIds = cardIds;
    }

    public List<Long> getCardIds() {
        return CardIds;
    }

    public void setActiveCardId(Long activeCardId) {
        this.activeCardId = activeCardId;
    }

    public Long getActiveCardId() {
        return activeCardId;
    }

    public void setActiveMysteryWord(String activeMysteryWord) {
        this.activeMysteryWord = activeMysteryWord;
    }

    public String getActiveMysteryWord() {
        return activeMysteryWord;
    }
}
