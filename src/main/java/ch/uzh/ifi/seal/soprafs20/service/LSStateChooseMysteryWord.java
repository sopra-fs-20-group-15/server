package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.ScoreCalculator;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Scoreboard;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class LSStateChooseMysteryWord implements LogicServiceState {

    private final CardService cardService;

    @Autowired
    public LSStateChooseMysteryWord( CardService cardService) {
        this.cardService = cardService;
    }

    public GameEntity initializeTurn(GameEntity game){
        throw new ConflictException("You cannot initialize in this phase!");
    }

    public String setMysteryWord(GameEntity game, Long wordId){
        //set mystery word
        CardEntity card = cardService.getCardById(game.getActiveCardId());
        String word = cardService.chooseWordOnCard(wordId, card);
        game.setActiveMysteryWord(word);
        //Switch over to the next state
        game.setStateForLogicService(State.GiveClues);
        return word;

    }

    public String getMysteryWord(GameEntity game){
        throw new ConflictException("The MysteryWord has not been set yet!");
    }

    public void giveClue(GameEntity game, CluePostDTO cluePostDTO){
        throw new ConflictException("The MysteryWord has to be chosen first!");
    }

    public List<ClueGetDTO> getClues(GameEntity game)
    {throw new ConflictException("The MysteryWord has to be chosen first!");
    }

    public void setGuess(GameEntity game, String guess) {
        throw new ConflictException("The MysteryWord has to be chosen first!");
    }


    public String getGuess(GameEntity game) {
        throw new ConflictException("The MysteryWord has to be chosen first!");
    }


}
