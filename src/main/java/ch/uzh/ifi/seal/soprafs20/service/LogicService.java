package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.*;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * PlayerEntity Service

 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class LogicService {

    private final Logger log = LoggerFactory.getLogger(GameSetUpService.class);
    private final WordComparer wordComparer;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final CardService cardService;
    private final ActiveGameService gameService;
    private final PlayerService playerService;

    @Autowired
    public LogicService(@Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameRepository") GameRepository gameRepository, CardService cardService, ActiveGameService gameService, PlayerService playerService) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.wordComparer = new WordComparer();
        this.cardService = cardService;
        this.gameService= gameService;
        this.playerService = playerService;
    }

    /**Puts the active player at the end of the passive Players List, takes the passive Player at 0 and make him the active player*/
    public GameEntity goOnePlayerFurther(GameEntity game){
        List<Long> passivePlayerIds = game.getPassivePlayerIds();
        passivePlayerIds.add(game.getActivePlayerId());
        Long playerId = passivePlayerIds.remove(0);
        game.setActivePlayerId(playerId);
        game.setPassivePlayerIds(passivePlayerIds);
        return game;
    }

    /***/
    public GameEntity drawCardFromStack(GameEntity game){
        if (game.getCardIds().size() > 0){
            List<Long> cardIds = game.getCardIds();
            game.setActiveCardId(cardIds.remove(cardIds.size()-1));
            return game;
        }
        else{
            throw new ConflictException("The CardStack is empty!The game should have ended already!");
        }
    }

    /**get amount of remaining cards*/
    public CardsRemainingDTO getCardAmount(Long gameId){
        CardsRemainingDTO cardsRemainingDTO=new CardsRemainingDTO();
        cardsRemainingDTO.setCardsOnStack(gameService.getGameById(gameId).getCardIds().size());
        return cardsRemainingDTO;
    }

    /**Initializes a turn*/
    public GameEntity initializeTurn(Long gameId){
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        GameEntity game = gameOp.get();
        if (!game.getHasBeenInitialized()){
            if (!game.getHasEnded()){
                game.setHasBeenInitialized(true);
                game.setActiveMysteryWord("");
                game.setHasEnded(false);
                //Update the players
                goOnePlayerFurther(game);
                //Update Cards
                drawCardFromStack(game);
                for (PlayerEntity player: game.getPlayers()) {
                    player.setTimePassed(null);
                }
                game.setRightGuess(false);
                game.setValidClue(false);
                game.setValidCluesAreSet(false);
                game.setClueMap(new HashMap<String, String>());
                game.setValidClues(new HashMap<String, String>());
                game.setAnalyzedClues(new HashMap<String, Integer>());
                game.setTimeStart(null);
                game.setGuess("");
                game.setIsValidGuess(false);
                return game;
        }
            else {throw new ConflictException("The game has already ended!");}}
        else {throw new NoContentException("The Game has already been initialized!");}
    }

    /**Gets a gameId and gives back the active Card of that game*/
    public CardEntity getCardFromGameById(Long gameId){
        //Get Game
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        GameEntity game = gameOp.get();
        //Check if activeCard has already been set and if so, get the Card
        Long cardId = game.getActiveCardId();
        CardEntity card;
        if (cardId != null){
            card = cardService.getCardById(cardId);
            return card;
        }
        else {throw new NotFoundException("The active Card of this game has not been set yet!");}
    }


    /**Set the MysteryWord*/

    public String setMysteryWord(Long gameId, Long wordId){
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        GameEntity game = gameOp.get();
        if (game.getActiveMysteryWord().isBlank()){
            CardEntity card = cardService.getCardById(game.getActiveCardId());
            String word = cardService.chooseWordOnCard(wordId, card);
            game.setActiveMysteryWord(word);
	    game.setTimeStart(System.currentTimeMillis());

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
            return word;
        }
        else {throw new NoContentException("The MysteryWord has already been set");}
    }

    /**Get the MysteryWord*/

    public String getMysteryWord(Long gameId){
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        GameEntity game = gameOp.get();
        if (!game.getActiveMysteryWord().isBlank()){
            return game.getActiveMysteryWord();
        }
        else {throw new NotFoundException("The MysteryWord not been set yet!");}
    }

    /**Set the Guess*/
    public void setGuess(GameEntity game, String guess){
        boolean isValidGuess = wordComparer.compareMysteryWords(game.getActiveMysteryWord(), guess);
        game.setGuess(guess);
        game.setIsValidGuess(isValidGuess);
        for (PlayerEntity player:game.getPlayers()) {
            if (player.getId().equals(game.getActivePlayerId())) player.setTimePassed(System.currentTimeMillis()-game.getTimeStart());
        }
        //draw an extra card if the guess was wrong
        if (!isValidGuess){
            drawCardFromStack(game);
        }
        //At this point it should be possible again to initialize an new turn
        game.setHasBeenInitialized(false);
//        Time to dish out some points fam!
        game.updateScoreboard();
    }

    /**Lets players give clues and saves them into a list*/
    public void giveClue(GameEntity game, CluePostDTO cluePostDTO){
        if (game.getClueMap().get(cluePostDTO.getPlayerToken())==null) {
            Map<String, String> clueMap =game.getClueMap();
            clueMap.put(cluePostDTO.getPlayerToken(),cluePostDTO.getClue());
            game.setClueMap(clueMap);
            System.currentTimeMillis();
            playerService.getPlayerByToken(cluePostDTO.getPlayerToken()).setTimePassed(System.currentTimeMillis()-game.getTimeStart());
        }
        else throw new UnauthorizedException("You have already submitted a clue for this round!");
        if (game.getClueMap().size()==game.getPlayers().size()+game.getNumOfBots()-1){
            ArrayList<String> clues = new ArrayList<String>(game.getClueMap().values());
            String mystery=game.getActiveMysteryWord();
            Map<String,Integer> analyzedClues=wordComparer.compareClues(clues, mystery);
            game.setAnalyzedClues(analyzedClues);
            Map<String,String> validClues = new HashMap<>();
            for ( Map.Entry<String,String> entry: game.getClueMap().entrySet()){
                if (playerRepository.findByToken(entry.getKey())!=null) {
                    if (analyzedClues.get(entry.getValue())==0) validClues.put(playerRepository.findByToken(entry.getKey()).getUsername(),
                        entry.getValue());
                }
                else {
                    for (Angel angel : game.getAngels()){
                        if (angel.getToken().equals(entry.getKey())) validClues.put(angel.getName(), entry.getValue());
                    }
                    for (Devil devil : game.getDevils()){
                        if (devil.getToken().equals(entry.getKey())) validClues.put(devil.getName(), entry.getValue());
                    }
                }
            }
            game.setValidClues(validClues);
            game.setValidCluesAreSet(true);
            game.setTimeStart(System.currentTimeMillis());
        }
    }

    /**Get full clue list*/
    public List<ClueGetDTO> getClues(GameEntity game) {
//        check if clues have already been set for this round
        if (game.getValidCluesAreSet()) {
            List<ClueGetDTO> list = new ArrayList<>();
            for (String playerName: game.getValidClues().keySet()) {
                ClueGetDTO clueGetDTO =new ClueGetDTO();
                clueGetDTO.setClue(game.getValidClues().get(playerName));
                clueGetDTO.setPlayerName(playerName);
                list.add(clueGetDTO);
            }
            return list;
        }
//        if clues have not been set throw NoContentException
        else throw new NoContentException("Clues are not ready yet!");
    }

    /**Returns the names of all the players that have already submitted a clue*/
    public List<PlayerNameDTO> getCluePlayers(GameEntity game) {
        List<PlayerNameDTO> list = new ArrayList<>();
        for (String token: game.getClueMap().keySet()) {
            if (playerRepository.findByToken(token)!=null){
                list.add(DTOMapper.INSTANCE.convertPlayerEntityToPlayerNameDTO(playerRepository.findByToken(token)));
            }
            else {
                for (Angel angel: game.getAngels()) {
                    if (angel.getToken().equals(token)) {
                        PlayerNameDTO playerNameDTO = new PlayerNameDTO();
                        playerNameDTO.setPlayerName(angel.getName());
                        list.add(playerNameDTO);
                    }
                }
                for (Devil devil: game.getDevils()) {
                    if (devil.getToken().equals(token)) {
                        PlayerNameDTO playerNameDTO = new PlayerNameDTO();
                        playerNameDTO.setPlayerName(devil.getName());
                        list.add(playerNameDTO);
                    }
                }
            }
        }
        return  list;
    }

    /**Returns whether the game has already ended or not*/
    public boolean hasGameEnded(Long gameId){
        //Get the game
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        GameEntity game = gameOp.get();
        //Check if the card stack is empty. If so, set gameHasEnded to true
        if (game.getHasEnded()) return true;
        else if (game.getCardIds().size() == 0){
            game.setHasEnded(true);
            Map<String, Integer> sb= game.getScoreboard().getScore();
            for (PlayerEntity player: game.getPlayers()) {
                int score=sb.get(player.getUsername());
                player.setLeaderBoardScore(player.getLeaderBoardScore()+score);
                player.setGamesPlayed(player.getGamesPlayed()+1);
                playerRepository.saveAndFlush(player);
            }
            return true;
        }
        return false;
    }    
/**Orders the items of a list*/
    public List<StatisticsGetDTO> orderStatisticsGetDTOList(List<StatisticsGetDTO> rankScorePlayerNameList){
        //Sort with insertion sort (since max. 7 human players, time complexity is not that important(if more players, quick sort would have been used))
        for(int i = 1; i < rankScorePlayerNameList.size(); i++){
            StatisticsGetDTO current = rankScorePlayerNameList.get(i);
            int j = i -1;
            while(j >= 0 && current.getScore() > rankScorePlayerNameList.get(j).getScore()){
                rankScorePlayerNameList.set(j+1, rankScorePlayerNameList.get(j));
                j--;
            }
            rankScorePlayerNameList.set(j+1, current);
        }
        return  rankScorePlayerNameList;
    }



    /**Get the scores of the players in a game*/
    public List<StatisticsGetDTO> getStatistics(Long gameId){
        //Get a game by Id;
        Optional<GameEntity> gameOp = gameRepository.findById(gameId);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        GameEntity game = gameOp.get();
        Scoreboard scoreboard = game.getScoreboard();
        //Convert into a List of StatisticsGetDto which consists of the Rank, the Score and the playerName
        List<StatisticsGetDTO> rankScorePlayerNameList = new ArrayList<StatisticsGetDTO>();
        rankScorePlayerNameList = scoreboard.transformIntoList();
        //Order descending based on score
        rankScorePlayerNameList = orderStatisticsGetDTOList(rankScorePlayerNameList);
        //Set the placement
        int i = 1;
        for(StatisticsGetDTO elements : rankScorePlayerNameList){
            elements.setPlacement(i);
            i++;
        }
        return rankScorePlayerNameList;
    }
}
