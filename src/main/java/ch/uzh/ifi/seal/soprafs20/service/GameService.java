package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Bot;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.PlayerNotAvailable;

import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ActiveGamePostDTO;
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
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final GameSetUpRepository gameSetUpRepository;

    @Autowired
    public GameService(@Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository,@Qualifier("gameRepository") GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.gameSetUpRepository = gameSetUpRepository;
    }

    public GameEntity getGameById(Long id){
        Optional<GameEntity> gameOp = gameRepository.findById(id);
        if (gameOp.isEmpty()) throw new NotFoundException("No game with this id exists");
        return gameOp.get();
    }

    public GameSetUpEntity getGameSetupById(Long id){
        Optional<GameSetUpEntity> gameOp = gameSetUpRepository.findById(id);
        if (gameOp.isEmpty()) throw new NotFoundException("No Game Setup with this id exists!");
        return gameOp.get();
    }

    /**Creates a game. GameToken should be checked beforehand so that player exists*/
    public GameSetUpEntity createGame(GameSetUpEntity game) {
        //Check, if parameters are acceptable
        if (game.getNumberOfPlayers() < 8 && game.getNumberOfPlayers() > 2) {
            if (game.getNumberOfBots() > -1 && game.getNumberOfBots() < game.getNumberOfPlayers()){
             if(game.getGameType().name().equals("PRIVATE")){
                if(game.getPassword() != null && ! game.getPassword().isEmpty()){
                    GameSetUpEntity newGame = gameSetUpRepository.save(game);
                    gameSetUpRepository.flush();
                    return newGame;
                }
                else{
                    throw new ConflictException("The Password should not be empty or null!");
                }
             }
             // If it is a public game
             else{
                 GameSetUpEntity newGame = gameSetUpRepository.save(game);
                 gameSetUpRepository.flush();
                 return newGame;
             }
            }
            else{
                throw new ConflictException("The number of Player should be smaller than the number of players and bigger than 0");
            }
        }
        else{
            throw new ConflictException("The number of Player should be smaller than 7 and bigger than 3");
        }
    }

    /**Puts a player into a gameSetUp if all the requirements for that are met
     * @Returns GameSetUpEntity; for testing reasons*/
    public GameSetUpEntity putPlayerIntoGame(Long gameId, PlayerEntity player, String password){
        //Check if gameSetUpId exists
        Optional<GameSetUpEntity> gameOp = gameSetUpRepository.findById(gameId);
        if (gameOp.isEmpty()) throw new NotFoundException("No gameEntity with specified ID exists.");
        GameSetUpEntity game = gameOp.get();
        //Check if player is already part of the game
        if (game.getPlayerTokens().contains(player.getToken())){
            throw new NoContentException("The player is already part of the game");
        }
        //Check that the game is not full yet
        if (game.getNumberOfBots()+game.getPlayerTokens().size() >= game.getNumberOfPlayers()){
            throw new UnauthorizedException("The game is already full!");
        }
        //Check if the game is private and if so, if the password is correct
        if (game.getGameType().name().equals("PRIVATE")){
            if (!game.getPassword().equals(password)){
                throw new UnauthorizedException("The Password for joining a private game is not correct");
            }
        }
        //Put player into game
        List<String> playerTokens = game.getPlayerTokens();
        playerTokens.add(player.getToken());
        game.setPlayerTokens(playerTokens);
        return game;
    }
    /**Removes player from game*/
    public GameSetUpEntity removePlayerFromGame(Long gameId, PlayerEntity player){
        //Check if gameSetUpId exists
        Optional<GameSetUpEntity> gameOp = gameSetUpRepository.findById(gameId);
        if (gameOp.isEmpty()) throw new NotFoundException("No gameEntity with specified ID exists.");
        GameSetUpEntity game = gameOp.get();
        //Check that player is actually part of the game
        List<String> playerTokensFromGame = game.getPlayerTokens();
        if (!playerTokensFromGame.contains(player.getToken())){
            throw new NotFoundException("This player is not part of the game");
        }
        //Remove Player from game
        List<String> playerTokens = game.getPlayerTokens();
        playerTokens.remove(player.getToken());
        game.setPlayerTokens(playerTokens);
        return game;
    }

    public ActiveGamePostDTO createActiveGame(Long gameSetupId) {
            GameSetUpEntity gameSetUpEntity =this.getGameSetupById(gameSetupId);
            if (gameSetUpEntity.getPlayerTokens().size()==gameSetUpEntity.getNumberOfPlayers()) {
                GameEntity game = new GameEntity();
                List<PlayerEntity> players = new ArrayList<>();
                List<Bot> bots = new ArrayList<>();
                for (String playerToken : gameSetUpEntity.getPlayerTokens()) {
                    players.add(playerRepository.findByToken(playerToken));
                }
                int numOfBots = gameSetUpEntity.getNumberOfBots().intValue();
                for (int i = 1; i <= numOfBots; i++) {
                    Bot bot = new Bot();
                    bot.setBotName("Bot_Nr_" + String.valueOf(i));
                    bot.setBotToken("Bot_" + String.valueOf(i));
                    bots.add(bot);
                }
                game.setBots(bots);
                game.setPlayers(players);
                game.setValidCluesAreSet(false);
                game.setClueList(new HashMap<>());
                game.setActivePlayerId(gameSetUpEntity.getHostId());
                List<Long> passivePlayerIds=new ArrayList<>();
                for (PlayerEntity player : game.getPlayers()){
                    if (!player.getId().equals(game.getActivePlayerId())) passivePlayerIds.add(player.getId());
                }
                game.setPassivePlayerIds(passivePlayerIds);

                GameEntity activeGame= gameRepository.saveAndFlush(game);
                ActiveGamePostDTO activeGamePostDTO=new ActiveGamePostDTO();
                activeGamePostDTO.setId(activeGame.getId());
                List<String> playerNames= new ArrayList<>();
                for (PlayerEntity player : activeGame.getPlayers()){
                    playerNames.add(player.getUsername());
                }
                for(Bot bot : activeGame.getBots()) playerNames.add(bot.getBotName());
                activeGamePostDTO.setPlayerNames(playerNames);
                return activeGamePostDTO;
		    }
            else throw new ConflictException("Number of ready players is lower than number of desired players");
    }


    public void updateLeaderBoard(GameEntity game){
        for (PlayerEntity player : game.getScoreboard().getEndScore().keySet()){
            Optional<PlayerEntity> playerToBeUpdated = playerRepository.findById(player.getId());
            playerToBeUpdated.ifPresent(value -> value.setScore(value.getScore() + game.getScoreboard().getEndScore().get(player)));
        }
    }


}
