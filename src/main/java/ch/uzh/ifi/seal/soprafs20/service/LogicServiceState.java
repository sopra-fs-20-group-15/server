package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;

import java.util.*;

public interface LogicServiceState {

    public GameEntity initializeTurn(GameEntity game);

    public String setMysteryWord(GameEntity game, Long wordId);

    public String getMysteryWord(GameEntity game);

    public void giveClue(Long gameId, CluePostDTO cluePostDTO);

    public List<ClueGetDTO> getClues(GameEntity game);

    public void setGuess(GameEntity game, String guess);

    public String getGuess(GameEntity game);

}




