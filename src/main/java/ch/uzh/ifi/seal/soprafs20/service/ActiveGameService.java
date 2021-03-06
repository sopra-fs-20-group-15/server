package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.*;
import ch.uzh.ifi.seal.soprafs20.botCreator.BotCreator;
import ch.uzh.ifi.seal.soprafs20.exceptions.*;

import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ActiveGamePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GameGetDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.*;


@Service
@Transactional
public class ActiveGameService {

    private final Logger log = LoggerFactory.getLogger(GameSetUpService.class);

    private final PlayerService playerService;
    private final GameRepository gameRepository;
    private final GameSetUpService gameSetUpService;
    private final CardService cardService;
    private final PlayerRepository playerRepository;

    @Autowired
    public ActiveGameService(@Qualifier("cardService") CardService cardService, @Qualifier("playerService") PlayerService playerService, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository, @Qualifier("gameRepository") GameRepository gameRepository, GameSetUpService gameSetUpService, @Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.cardService = cardService;
        this.playerService = playerService;
        this.gameRepository = gameRepository;
        this.gameSetUpService = gameSetUpService;
        this.playerRepository = playerRepository;
    }
    /**Get an active game by its Id or throw 404 in case the game is not found
     *  @Param: Long id; Id of the active game that should be retrieved
     *  @Returns: GameEntity; The active Game
     *  @Throws: 404: If no game with specified id exists
     * */
    public GameEntity getGameById(Long id){
        Optional<GameEntity> gameOp = gameRepository.findById(id);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        return gameOp.get();
    }

    /**Get the information about a game that is Important for the frontend
     * @Param: Long gameId (activeGame)
     * @Returns: GameGetDTO: Long id, String activePlayerName, List<String> playerNames, List<String> passivePlayerNames,
     * @Throws: 404: Game with specified Id cannot be found
     * */
    public GameGetDTO getGameInformationById(Long gameId){

        GameGetDTO gameGetDTO = new GameGetDTO();
        GameEntity game = getGameById(gameId);
        gameGetDTO.setId(game.getId());

        //Get the name of the active Player
        gameGetDTO.setActivePlayerName(playerService.getPlayerById(game.getActivePlayerId()).getUsername());

        //Get the name of the passive players, save them in a list (bots as well)
        List<String> playerNames = new ArrayList<String>();
        for (Long id: game.getPassivePlayerIds()){
            playerNames.add(playerService.getPlayerById(id).getUsername());
        }
        //Add bots
        for (Bot bot: game.getBots()){
            playerNames.add(bot.getName());
        }
        gameGetDTO.setPassivePlayerNames(playerNames);
        //Add the name of the active player to the list of the passive players and return list with all players
        List<String> playerNames2 = new ArrayList<String>();
        for (PlayerEntity player : game.getPlayers()){playerNames2.add(player.getUsername());}
        //Add bots
        for (Bot bot: game.getBots()){
            playerNames2.add(bot.getName());
        }
        gameGetDTO.setPlayerNames(playerNames2);
        return gameGetDTO;
    }

    /**Create active game with helpers*/

    /**Adds the human players to a game*/
    protected GameEntity addHumanPlayers(GameEntity game, List<String> playerTokens, String hostToken){
        //Add playerEntities
        List<PlayerEntity> players = new LinkedList<>();
        for (String playerToken : playerTokens) {
            players.add(playerService.getPlayerByToken(playerToken));
            game.setPlayers(players);
        }
        //Add Ids
        game.setActivePlayerId(playerService.getPlayerByToken(hostToken).getId());
        List<Long> passivePlayerIds=new LinkedList<>();
        for (PlayerEntity player : game.getPlayers()){
            if (!player.getId().equals(game.getActivePlayerId())) passivePlayerIds.add(player.getId());
        }
        game.setPassivePlayerIds(passivePlayerIds);
        return game;
    }

    /**Adds 13 cards to a game and initializes the card repository if this has not been done yet*/
    protected GameEntity addCards(GameEntity game){
        try {cardService.addAllCards();
        } catch (IOException ex) {
            throw new NoContentException("The CardDatabase couldn't be filled");
        }
        game.setCardIds(cardService.getFullStackOfCards());
        return game;
    }

    /**Initializes Scoreboard, Clues and Guesses*/
    protected GameEntity furtherInitialize(GameEntity game){
        //further initialization
        game.setStateForLogicService(State.WordReveal);
        game.setClueMap(new HashMap<String,String>());
        game.setScoreboard(new Scoreboard());
        game.getScoreboard().initializeMap(game.getPlayers());
        game.setAnalyzedClues(new HashMap<String, Integer>());
        game.setTimeStart(null);
        game.setActiveMysteryWord("");
        Map<String, String> validClues= new HashMap<>();
        game.setValidClues(validClues);
        return game;
    }

    /**Saves all the information about the newly created game into a DTO*/
    protected ActiveGamePostDTO createActiveGamePostDTO(GameEntity game){
        ActiveGamePostDTO activeGamePostDTO=new ActiveGamePostDTO();
        activeGamePostDTO.setId(game.getId());
        List<String> playerNames= new ArrayList<>();
        for (PlayerEntity player : game.getPlayers()){
            playerNames.add(player.getUsername());
        }
        for(Bot bot : game.getBots()) playerNames.add(bot.getName());
        activeGamePostDTO.setPlayerNames(playerNames);
        return activeGamePostDTO;
    }

    /**Create a an active game from a gameSetUpEntity
     *@Param: playerTokenDTO: String playerToken
     *@Returns: activeGamePostDTO: Long id, List<String> playerNames
     *@Throws: 409: Not enough players are part of the game in order to start it
     *@Throws: 401: playerToken is not the Token of the Host
     *      * */
    public ActiveGamePostDTO createActiveGame(Long gameSetupId, String pt) {
        //Checkers
        //Check that the player is the host
        if (!gameSetUpService.getGameSetupById(gameSetupId).getHostName().equals(playerService.getPlayerByToken(pt).getUsername()))
            throw new UnauthorizedException("Player is not host and therefore not allowed to start the game");
        GameSetUpEntity gameSetUpEntity =gameSetUpService.getGameSetupById(gameSetupId);
        //Check that enough players are in the lobby to start the game.
        if (gameSetUpEntity.getPlayerTokens().size()+gameSetUpEntity.getNumberOfDevils()+gameSetUpEntity.getNumberOfAngles()
                >2 && gameSetUpEntity.getPlayerTokens().size()+gameSetUpEntity.getNumberOfDevils()+gameSetUpEntity.getNumberOfAngles()<8) {
            //game initialization
            GameEntity game = new GameEntity();
            addHumanPlayers(game, gameSetUpEntity.getPlayerTokens(), pt);
            BotCreator botCreator=new BotCreator();
            botCreator.addBots(game, gameSetUpEntity.getNumberOfDevils().intValue(), gameSetUpEntity.getNumberOfAngles().intValue());
            addCards(game);
            furtherInitialize(game);
            game.getHandlerForLeavingPlayers().loadPlayersIntoHandler(game.getPlayers());
            //Save and flush
            GameEntity activeGame= gameRepository.saveAndFlush(game);
            gameSetUpService.getGameSetupById(gameSetupId).setActiveGameId(activeGame.getId());
            //Create the activeGamePostDTO
            return createActiveGamePostDTO(activeGame);
        }
        else throw new ConflictException("Not enough or too many players to start game!");
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

    /**
     *
     *
     *
     * */

    public void removePlayerFromGame(Long gameId, String playerToken){
        GameEntity game = getGameById(gameId);
        PlayerEntity player = playerService.getPlayerByToken(playerToken);
        if (game.getStateForLogicService() == State.WordReveal) {
            // Set the time for the player that wants to leave to a big number
            HandlerForLeavingPlayers handlerForLeavingPlayers = game.getHandlerForLeavingPlayers();
            Map<String, Long> deadMansSwitchMap = handlerForLeavingPlayers.getDeadMansSwitchMap();
            deadMansSwitchMap.put(playerToken, 10000L);
            handlerForLeavingPlayers.setDeadMansSwitchMap(deadMansSwitchMap);
            game.setHandlerForLeavingPlayers(handlerForLeavingPlayers);
            //remove the players
            handlerForLeavingPlayers.removeInactivePlayers(game);
            //delete the game if no human players are left
            if (handlerForLeavingPlayers.getDeadMansSwitchMap().size() == 0) {
                game.setStateForLogicService(State.hasEnded);
                deleteActiveGame(gameId);
            }
        }
        else {throw new ConflictException("A player can only leave a round during Word Reveal!");}
    }

    /**Deletes an active game once it has ended
     * @Param: String gameId
     * @Returns: void
     * @Throws: 409: Game has not ended yet
     * */
    public void deleteActiveGame(long gameId){
        GameEntity game = getGameById(gameId);
        if (game.getStateForLogicService() != State.hasEnded){throw new ConflictException("The game cannot be deleted before it has ended");}
        updateLeaderBoard(game);
        gameRepository.deleteById(game.getId());

    }

}
