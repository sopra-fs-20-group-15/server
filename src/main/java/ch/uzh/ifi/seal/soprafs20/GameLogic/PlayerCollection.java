package ch.uzh.ifi.seal.soprafs20.GameLogic;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class PlayerCollection {
    @ElementCollection
    private List<PlayerEntity> players;
    @ElementCollection
    private List<Long> passivePlayerIds;
    private Long activePlayerId;
    @ElementCollection
    private List<Angel> angels;
    @ElementCollection
    private List<Devil> devils;


    public void setPlayers(List<PlayerEntity> players) {
        this.players = players;
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public void setPassivePlayerIds(List<Long> passivePlayerIds) {
        this.passivePlayerIds = passivePlayerIds;
    }

    public List<Long> getPassivePlayerIds() {
        return passivePlayerIds;
    }

    public void setActivePlayerId(Long activePlayerId) {
        this.activePlayerId = activePlayerId;
    }

    public Long getActivePlayerId() {
        return activePlayerId;
    }

    public void setAngels(List<Angel> angels) {
        this.angels = angels;
    }

    public List<Angel> getAngels() {
        return angels;
    }

    public void setDevils(List<Devil> devils) {
        this.devils = devils;
    }

    public List<Devil> getDevils() {
        return devils;
    }

    public List<Bot> getBots(){
        List<Bot> bots = new ArrayList<>();
        bots.addAll(angels);
        bots.addAll(devils);
        return bots;
    }
}
