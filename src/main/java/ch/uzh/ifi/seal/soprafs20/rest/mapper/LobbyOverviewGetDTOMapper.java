package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyOverviewGetDTO;

public class LobbyOverviewGetDTOMapper {
    public static LobbyOverviewGetDTO convertGameSetUpEntityToLobbyOverviewGetDTOMapper (GameSetUpEntity gameSetUpEntity){
        LobbyOverviewGetDTO lobbyOverviewGetDTO= new LobbyOverviewGetDTO();

        lobbyOverviewGetDTO.setGameName(gameSetUpEntity.getGameName());
        lobbyOverviewGetDTO.setGameType(gameSetUpEntity.getGameType());
        lobbyOverviewGetDTO.setNumOfActualPlayers((long) gameSetUpEntity.getPlayerTokens().size());
        lobbyOverviewGetDTO.setNumOfDesiredPlayers(gameSetUpEntity.getNumberOfPlayers());
        lobbyOverviewGetDTO.setNumOfAngels(gameSetUpEntity.getNumberOfAngles());
        lobbyOverviewGetDTO.setNumOfDevils(gameSetUpEntity.getNumberOfDevils());

        return lobbyOverviewGetDTO;
    }
}
