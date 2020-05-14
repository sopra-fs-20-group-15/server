package ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ClueGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.CluePostDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LSSGameHasEnded implements LogicServiceState{

    public GameEntity initializeTurn(GameEntity game) {
        throw new NoContentException("The game has ended! Cannot initialize");
    }

    public String setMysteryWord(GameEntity game, Long wordId){
        throw new NoContentException("The game has ended! Cannot Set MysteryWord");
    }

    public String getMysteryWord(GameEntity game){
        throw new NoContentException("The game has ended! Cannot get mysteryWord");
    }

    public void giveClue(Long GameId, CluePostDTO cluePostDTO){
        throw new NoContentException("The game has ended! Cannot give clue");
    }

    public List<ClueGetDTO> getClues(GameEntity game){
        throw new NoContentException("The game has ended! Cannot get clues");
    }

    public void setGuess(GameEntity game, String guess){
        throw new NoContentException("The game has ended! Cannot get guess");
    }

    public String getGuess(GameEntity game){
        throw new NoContentException("The game has ended! Cannot get guess");
    }

}
