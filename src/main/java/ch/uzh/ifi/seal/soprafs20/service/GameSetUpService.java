package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
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
public class GameSetUpService {

    private final PlayerRepository playerRepository;
    private final GameSetUpRepository gameSetUpRepository;

    @Autowired
    public GameSetUpService(@Qualifier("playerRepository") PlayerRepository playerRepository, @Qualifier("gameSetUpEntityRepository") GameSetUpRepository gameSetUpRepository) {
        this.playerRepository = playerRepository;
        this.gameSetUpRepository = gameSetUpRepository;
    }
    /**Getters*/

    public GameSetUpEntity getGameSetupById(Long id){
        Optional<GameSetUpEntity> gameOp = gameSetUpRepository.findById(id);
        if (gameOp.isEmpty()) throw new NotFoundException("No Game Setup with this id exists!");
        return gameOp.get();
    }

    public PlayerEntity getPlayerByToken(String playerToken){
        PlayerEntity player = playerRepository.findByToken(playerToken);
        if (player==null) throw new NotFoundException("No player with your Token exists");
        return player;
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
