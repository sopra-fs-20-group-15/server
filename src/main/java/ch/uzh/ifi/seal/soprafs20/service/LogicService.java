package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.*;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
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

    private HashMap<State, LogicServiceState> possibleStates = new HashMap<>();
    private LogicServiceState state;

    @Autowired
    public LogicService(@Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameRepository") GameRepository gameRepository, CardService cardService, ActiveGameService gameService, PlayerService playerService, LSStateChooseMysteryWord lsStateChooseMysteryWord, LSSGiveClues lssGiveClues, LSSGiveGuess lssGiveGuess, LSSWordReveal lssWordReveal, LSSGameHasEnded lssGameHasEnded) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.wordComparer = new WordComparer();
        this.cardService = cardService;
        this.gameService= gameService;
        this.playerService = playerService;
        this.possibleStates.put(State.ChooseMysteryWord, lsStateChooseMysteryWord);
        this.possibleStates.put(State.GiveClues, lssGiveClues);
        this.possibleStates.put(State.GiveGuess, lssGiveGuess);
        this.possibleStates.put(State.WordReveal, lssWordReveal);
        this.possibleStates.put(State.hasEnded, lssGameHasEnded);
    }

    protected GameEntity getGame(Long gameId){
        GameEntity game = gameService.getGameById(gameId);
        state = possibleStates.get(game.getStateForLogicService());
        return game;
    }


    /**Puts the active player at the end of the passive Players List, takes the passive Player at 0 and make him the active player*/
    protected GameEntity goOnePlayerFurther(GameEntity game){
        List<Long> passivePlayerIds = game.getPassivePlayerIds();
        passivePlayerIds.add(game.getActivePlayerId());
        Long playerId = passivePlayerIds.remove(0);
        game.setActivePlayerId(playerId);
        game.setPassivePlayerIds(passivePlayerIds);
        return game;
    }

    /**Initializes Turn
     *@Param: Long
     *@Returns: GameEntity
     *@Throws: 404: No game with specified id found
     *@Throws: 409: Game has already ended
     *@Throws: 204: Game has already been initialized
     *     */
    public GameEntity initializeTurn(Long gameId){
        GameEntity game = getGame(gameId);
        return this.state.initializeTurn(game);
    }

    /**Get's active card
     *@Param: Long
     *@Returns: CardEntity
     *@Throws: 404: No game with specified id found
     *@Throws: 404: No active card set and therefore not found.
     * */
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

    /**Set Mystery Word
     *@Param: Long, Long
     *@Returns: String
     *@Throws: 404: No game with specified id found
     *@Throws: 204: Mystery Word has already been set
     *     */
    public String setMysteryWord(Long gameId, Long wordId){
        GameEntity game = getGame(gameId);
            //set mystery word
            String word = state.setMysteryWord(game, wordId);
            //Update State
            game = getGame(gameId);
            //check if active player is playing a game exclusively with bots, if so set validClues
            if (game.getPassivePlayerIds().isEmpty()){
                state.giveClue(game.getId(), new CluePostDTO());
            }
            return word;
    }

    /**Get Mystery Word
     *@Param: Long
     *@Returns: String
     *@Throws: 404: No game with specified id found
     *@Throws: 404: Mystery Word not found, because it has not been set yet
     *     */
    public String getMysteryWord(Long gameId){
        GameEntity game = getGame(gameId);
        return state.getMysteryWord(game);
    }
    /**Let's players give clues and save them into a list
     *@Param: Long gameId; CluePostDTO: String playerToken, String clue
     *@Returns: void
     *@Throws: 401: Not authorized to give clue anymore.
     *      * */
    public void giveClue(Long gameId, CluePostDTO cluePostDTO){
        GameEntity game = getGame(gameId);
        state.giveClue(game.getId(), cluePostDTO);
    }

    /**Let's players get the valid clues
     *@Param: GameEntity
     *@Returns: void
     *@Throws: 204: No content, since clues are not ready yet.
     *     */
    public List<ClueGetDTO> getClues(Long gameId) {
        GameEntity game = getGame(gameId);
        return state.getClues(game);
    }

    /**Set the Guess
     *@Param: GameEntity, String
     *@Returns: void
     * */
    public void setGuess(Long gameId, String guess){
        GameEntity game = getGame(gameId);
       state.setGuess(game, guess);
    }

    public String getGuess(Long gameId){
        GameEntity game = getGame(gameId);
        return state.getGuess(game);
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

    /**Get the scores of the players in a game
     *@Param: GameId
     *@Returns: void
     *@Throws: 404: No game with specified id found.
     *     */
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

    protected void updateLeaderBoard (GameEntity game){
        Map<String, Integer> sb= game.getScoreboard().getScore();
        //update the leader board scores for every human player in game
        for (PlayerEntity player: game.getPlayers()) {
            int score=sb.get(player.getUsername());
            player.setLeaderBoardScore(player.getLeaderBoardScore()+score);
            player.setGamesPlayed(player.getGamesPlayed()+1);
            playerRepository.saveAndFlush(player);
        }
    }

    /**Returns whether the game has already ended or not
     *@Param: GameId
     *@Returns: boolean
     *@Throws: 404: No game with specified id found.
     *     */
    public boolean hasGameEnded(Long gameId){
        //Get the game
        GameEntity game = getGame(gameId);

        if (game.getCardIds().size() == 0){
            game.setStateForLogicService(State.hasEnded);
            updateLeaderBoard(game);
            return true;
        }
        else {
            return false;}
    }

    /**Get amount of remaining cards
     *@Param: Long
     *@Returns: CardsRemainingDTO: int
     *     */
    public CardsRemainingDTO getCardAmount(Long gameId){
        CardsRemainingDTO cardsRemainingDTO=new CardsRemainingDTO();
        cardsRemainingDTO.setCardsOnStack(gameService.getGameById(gameId).getCardIds().size());
        return cardsRemainingDTO;
    }

    /**Returns the names of all the players that have already submitted a clue
     *@Param: GameEntity
     *@Returns: void
     *     */
    public List<PlayerNameDTO> getCluePlayers(Long gameId) {
        GameEntity game = getGame(gameId);
        List<PlayerNameDTO> list = new ArrayList<>();
        for (String token: game.getClueMap().keySet()) {
            //add players that have given clues to list
            if (playerRepository.findByToken(token)!=null){
                list.add(DTOMapper.INSTANCE.convertPlayerEntityToPlayerNameDTO(playerRepository.findByToken(token)));
            }
            //add bots that have given clues to list
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

}
