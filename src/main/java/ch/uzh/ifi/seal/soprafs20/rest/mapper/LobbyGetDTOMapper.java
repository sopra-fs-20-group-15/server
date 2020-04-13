package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Angel;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Devil;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;

import java.util.ArrayList;
import java.util.List;

public class LobbyGetDTOMapper {
    public static LobbyGetDTO convertGameSetUpEntityToLobbyGetDTO(GameSetUpEntity gameSetUpEntity, PlayerRepository playerRepository) {
        LobbyGetDTO lobbyGetDTO=new LobbyGetDTO();
        lobbyGetDTO.setGameName(gameSetUpEntity.getGameName());
        lobbyGetDTO.setGameSetUpId(gameSetUpEntity.getId());
        lobbyGetDTO.setNumOfDesiredPlayers(gameSetUpEntity.getNumberOfPlayers());
        lobbyGetDTO.setNumOfDevils(gameSetUpEntity.getNumberOfDevils());
        lobbyGetDTO.setNumOfAngels(gameSetUpEntity.getNumberOfAngles());
        lobbyGetDTO.setNumOfActualPlayers((long) gameSetUpEntity.getPlayerTokens().size());

        List<String> playerNames=new ArrayList<>();
        for (String token: gameSetUpEntity.getPlayerTokens()) {
            String playerName= playerRepository.findByToken(token).getUsername();
            if (playerRepository.findByToken(token).getId()==gameSetUpEntity.getId()) lobbyGetDTO.setHostName(playerName);
            playerNames.add(playerName);
        }
        for (int i = 1; i <= gameSetUpEntity.getNumberOfAngles(); i++) {
            playerNames.add("Angel_Nr_" + String.valueOf(i));
        }
        for (int i = 1; i <= gameSetUpEntity.getNumberOfDevils(); i++) {
            playerNames.add("Devil_Nr_" + String.valueOf(i));
        }
        lobbyGetDTO.setPlayerNames(playerNames);

        return lobbyGetDTO;
    }
}
