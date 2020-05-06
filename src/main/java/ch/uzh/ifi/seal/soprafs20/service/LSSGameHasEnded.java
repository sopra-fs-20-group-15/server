package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LSSGameHasEnded implements LogicServiceState{

    public GameEntity initializeTurn(GameEntity game) {
        throw new ConflictException("The game has ended!");
    }

    public String setMysteryWord(GameEntity game, Long wordId){
        throw new ConflictException("The game has ended!");
    }

    public String getMysteryWord(GameEntity game){
        throw new ConflictException("The game has ended!");
    }

    public void giveClue(GameEntity game, CluePostDTO cluePostDTO){
        throw new ConflictException("The game has ended!");
    }

    public List<ClueGetDTO> getClues(GameEntity game){
        throw new ConflictException("The game has ended!");
    }

    public void setGuess(GameEntity game, String guess){
        throw new ConflictException("The game has ended!");
    }

    public String getGuess(GameEntity game){
        throw new ConflictException("The game has ended!");
    }

}
