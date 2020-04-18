package ch.uzh.ifi.seal.soprafs20.service;


import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Bot;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Scoreboard;
import ch.uzh.ifi.seal.soprafs20.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NoContentException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;

import ch.uzh.ifi.seal.soprafs20.exceptions.UnauthorizedException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameSetUpRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ActiveGamePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyOverviewGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.LobbyGetDTOMapper;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.LobbyOverviewGetDTOMapper;
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
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final GameSetUpRepository gameSetUpRepository;
    private final CardService cardService;

    @Autowired
    public GameService(@Qualifier("cardService") CardService cardService, @Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository,@Qualifier("gameRepository") GameRepository gameRepository) {
        this.cardService = cardService;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.gameSetUpRepository = gameSetUpRepository;
    }
    /**Getters*/
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

    public PlayerEntity getPlayerById(long id){
        Optional<PlayerEntity> playerOp = playerRepository.findById(id);
        if (playerOp.isEmpty()) throw new NotFoundException("No Game Setup with this id exists!");
        return playerOp.get();

    }

    public PlayerEntity getPlayerByToken(String playerToken){
        PlayerEntity player = playerRepository.findByToken(playerToken);
        if (player==null) throw new NotFoundException("No player with your Token exists");
        return player;
    }

    /**Get the information about a game that is Important for the frontend*/
    public GameGetDTO getGameInformationById(Long gameId){
        GameGetDTO gameGetDTO = new GameGetDTO();
        GameEntity game = getGameById(gameId);
        gameGetDTO.setId(game.getId());
        //Get the name of the active Player
        gameGetDTO.setActivePlayerName(getPlayerById(game.getActivePlayerId()).getUsername());
        //Get the name of the passive players, save them in a list
        List<String> playerNames = new ArrayList<String>();
        for (Long id: game.getPassivePlayerIds()){
            playerNames.add(getPlayerById(id).getUsername());
        }
        gameGetDTO.setPassivePlayerNames(playerNames);
        //Add the name of the active player to the list of the passive players and return list with all players
        List<String> playerNames2 = new ArrayList<String>();
        for (Long id: game.getPassivePlayerIds()){
            playerNames2.add(getPlayerById(id).getUsername());
        }
        playerNames2.add(gameGetDTO.getActivePlayerName());
        gameGetDTO.setPlayerNames(playerNames2);
        return gameGetDTO;
    }

    /**Creates a game. GameToken should be checked beforehand so that player exists*/
    public GameSetUpEntity createGame(GameSetUpEntity game) {
        //Check, if parameters are acceptable
        if (game.getNumberOfPlayers() < 8 && game.getNumberOfPlayers() > 2) {
            //Check that number of Angels and Devils is bigger than one and does not exceed number of players
            if ((game.getNumberOfAngles() > -1 && game.getNumberOfDevils() > -1) && game.getNumberOfAngles() + game.getNumberOfDevils() < game.getNumberOfPlayers()) {
                if (game.getGameName() != null && !game.getGameName().isEmpty()) {
                    if (game.getGameType().name().equals("PRIVATE")) {
                        if (game.getPassword() != null && !game.getPassword().isEmpty()) {
                            GameSetUpEntity newGame = gameSetUpRepository.save(game);
                            gameSetUpRepository.flush();
                            return newGame;
                        }
                        else {
                            throw new ConflictException("The Password should not be empty or null!");
                        }
                    }
                    // If it is a public game
                    else {
                        GameSetUpEntity newGame = gameSetUpRepository.save(game);
                        gameSetUpRepository.flush();
                        return newGame;
                    }

                }

                else {
                    throw new ConflictException("The Name should not be empty or null!");
                }
            }
            else {
                throw new ConflictException("Something with the number of bots is wrong!");
            }

        }else {
            throw new ConflictException("The number of Player should be smaller than 7 and bigger than 3");
        }
    }

    /**Delete a gameSetUpEntity*/
    public boolean deleteGameSetUpEntity(Long gameId, PlayerEntity player){
        gameSetUpRepository.findById(gameId);
        Optional<GameSetUpEntity> gameOp = gameSetUpRepository.findById(gameId);
        if (gameOp.isEmpty()) throw new NotFoundException("No gameEntity with specified ID exists.");
        GameSetUpEntity game = gameOp.get();
        if (player.getUsername() != game.getHostName()){
            throw new UnauthorizedException("This player is not the Host of the Game!");
        }
        gameSetUpRepository.delete(game);
        return true;
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
        if (game.getNumberOfAngles()+game.getNumberOfDevils()+game.getPlayerTokens().size() >= game.getNumberOfPlayers()){
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
        //If the host wants to leave the game, delete the game
        if (game.getHostName().equals(player.getUsername())){
            deleteGameSetUpEntity(gameId, player);
            throw new NoContentException("Since the host wanted to leave the game, it was deleted!");
        }
        //Remove Player from game
        List<String> playerTokens = game.getPlayerTokens();
        playerTokens.remove(player.getToken());
        game.setPlayerTokens(playerTokens);
        return game;
    }

    /**Create a an active game from a gameSetUpEntity*/
    public ActiveGamePostDTO createActiveGame(Long gameSetupId, String pt) {
            if (!getGameSetupById(gameSetupId).getHostName().equals(getPlayerByToken(pt).getUsername()))
                throw new UnauthorizedException("Player is not host and therefore not allowed to start the game");
            GameSetUpEntity gameSetUpEntity =this.getGameSetupById(gameSetupId);
//            Check that enough players are in the lobby to start the game.
            if (gameSetUpEntity.getPlayerTokens().size()+gameSetUpEntity.getNumberOfDevils()+gameSetUpEntity.getNumberOfAngles()
                >2 && gameSetUpEntity.getPlayerTokens().size()+gameSetUpEntity.getNumberOfDevils()+gameSetUpEntity.getNumberOfAngles()<8) {
//                game initialization
                GameEntity game = new GameEntity();
//                adding human and not human players to the game
                List<PlayerEntity> players = new ArrayList<>();
                List<Angel> angels=new ArrayList<>();
                List<Devil> devils=new ArrayList<>();
                for (String playerToken : gameSetUpEntity.getPlayerTokens()) {
                    players.add(playerRepository.findByToken(playerToken));
                }
                int numOfAngels = gameSetUpEntity.getNumberOfAngles().intValue();
                int numOfDevils = gameSetUpEntity.getNumberOfDevils().intValue();
                for (int i = 1; i <= numOfAngels; i++) {
                    Angel bot = new Angel();
                    bot.setName("Angel_Nr_" + String.valueOf(i));
                    bot.setToken("Angel_" + String.valueOf(i));
                    angels.add(bot);
                }
                for (int i = 1; i <= numOfDevils; i++) {
                    Devil bot = new Devil();
                    bot.setName("Devil_Nr_" + String.valueOf(i));
                    bot.setToken("Devil_" + String.valueOf(i));
                    devils.add(bot);
                }
                game.setAngels(angels);
                game.setDevils(devils);
                game.setPlayers(players);
//                further initialization
                game.setHasBeenInitialized(false);
                game.setHasEnded(false);

//              Fill CardRepository and add 13 Cards to Game
                try {cardService.addAllCards();
                } catch (IOException ex) {
                    throw new NoContentException("The CardDatabase couldn't be filled");
                }
                game.setCardIds(cardService.getFullStackOfCards());
//              further initialization

                game.setValidCluesAreSet(false);
                game.setHasEnded(true);
                game.setHasBeenInitialized(false);
                game.setClueMap(new HashMap<String,String>());
                game.setActivePlayerId(getPlayerByToken(pt).getId());
                game.setScoreboard(new Scoreboard());
                game.getScoreboard().initializeMap(game.getPlayers());
                game.setAnalyzedClues(new HashMap<String, Integer>());
                game.setTimeStart(null);
                Map<String, String> validClues= new HashMap<>();
                game.setValidClues(validClues);
                List<Long> passivePlayerIds=new ArrayList<>();
                for (PlayerEntity player : game.getPlayers()){
                    if (!player.getId().equals(game.getActivePlayerId())) passivePlayerIds.add(player.getId());
                }
                game.setPassivePlayerIds(passivePlayerIds);
//              setup of the DTO that needs to be returned
                GameEntity activeGame= gameRepository.saveAndFlush(game);
                getGameSetupById(gameSetupId).setActiveGameId(activeGame.getId());
                ActiveGamePostDTO activeGamePostDTO=new ActiveGamePostDTO();
                activeGamePostDTO.setId(activeGame.getId());
                List<String> playerNames= new ArrayList<>();
                for (PlayerEntity player : activeGame.getPlayers()){
                    playerNames.add(player.getUsername());
                }
                for(Bot bot : activeGame.getNamesOfBots()) playerNames.add(bot.getName());
                activeGamePostDTO.setPlayerNames(playerNames);
                return activeGamePostDTO;
		    }
            else throw new ConflictException("Not enough or too many players to start game!");
    }

    /**Get Information about a GameSetUp*/
    public LobbyGetDTO getLobbyInfo (Long gameSetupId, String playerToken){
        getPlayerByToken(playerToken);
        if (!getGameSetupById(gameSetupId).getPlayerTokens().contains(playerToken))
            throw new UnauthorizedException("Player has not joined the lobby and therefore can't access lobby information!");
        return LobbyGetDTOMapper.convertGameSetUpEntityToLobbyGetDTO(getGameSetupById(gameSetupId), playerRepository);
    }

    /**Get all gameSetUpEntities*/
    public List<LobbyOverviewGetDTO> getLobbies() {
        List<GameSetUpEntity> gameSetUpEntities=this.gameSetUpRepository.findAll();
        List<LobbyOverviewGetDTO> lobbies=new ArrayList<>();
        for (GameSetUpEntity entity: gameSetUpEntities) {
            lobbies.add(LobbyOverviewGetDTOMapper.convertGameSetUpEntityToLobbyOverviewGetDTOMapper(entity));
        }
        return lobbies;
    }



}
