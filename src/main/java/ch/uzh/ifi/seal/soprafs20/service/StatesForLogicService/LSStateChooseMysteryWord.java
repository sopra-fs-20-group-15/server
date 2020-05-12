package ch.uzh.ifi.seal.soprafs20.service.StatesForLogicService;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.service.CardService;
import ch.uzh.ifi.seal.soprafs20.service.State;
import org.springframework.beans.factory.annotation.Autowired;
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
        throw new NoContentException("You cannot initialize in this phase!");
    }

    protected void botsAddClues(GameEntity game, String word){
        Map<String, String> clueMap =game.getClueMap();
        List<Angel> angels = game.getAngels();
        for (int i = 0; i < angels.size(); i++){
            String clue = angels.get(i).giveClue(word, i);
            clueMap.put(angels.get(i).getToken(), clue);
        }
        List<Devil> devils = game.getDevils();
        for (int j = 0; j < devils.size(); j++){
            String clue = devils.get(j).giveClue(word, j);
            clueMap.put(devils.get(j).getToken(), clue);
        }
    }

    public String setMysteryWord(GameEntity game, Long wordId){
        //set mystery word
        CardEntity card = cardService.getCardById(game.getActiveCardId());
        String word = cardService.chooseWordOnCard(wordId, card);
        game.setActiveMysteryWord(word);
        //Switch over to the next state
        game.setStateForLogicService(State.GiveClues);
        game.setTimeStart(System.currentTimeMillis());
        // let bots give their clues
        botsAddClues(game, game.getActiveMysteryWord());
        return word;

    }

    public String getMysteryWord(GameEntity game){
        throw new NoContentException("The MysteryWord has not been set yet!");
    }

    public void giveClue(Long GameId, CluePostDTO cluePostDTO){
        throw new NoContentException("The MysteryWord has to be chosen first!");
    }

    public List<ClueGetDTO> getClues(GameEntity game)
    {throw new NoContentException("The MysteryWord has to be chosen first!");
    }

    public void setGuess(GameEntity game, String guess) {
        throw new NoContentException("The MysteryWord has to be chosen first!");
    }


    public String getGuess(GameEntity game) {
        throw new NoContentException("The MysteryWord has to be chosen first!");
    }


}
