package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.Entities.CardEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.GameType;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ch.uzh.ifi.seal.soprafs20.GameLogic.*;

import javax.persistence.Column;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically transform/map the internal representation
 * of an entity (e.g., the PlayerEntity) to the external/API representation (e.g., PlayerGetDTO for getting, PlayerPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "gameName", target = "gameName")
    @Mapping(source = "numberOfPlayers", target = "numberOfPlayers")
    @Mapping(source = "numberOfAngles", target = "numberOfAngles")
    @Mapping(source = "numberOfDevils", target = "numberOfDevils")
    @Mapping(source = "gameType", target = "gameType")
    @Mapping(source = "password", target = "password")
    GameSetUpEntity convertGameSetUpPostDTOtoEntity(GamePostDTO gamePostDTO);

    @Mapping(source = "id", target = "gameId")
    @Mapping(source = "numberOfPlayers", target = "numberOfPlayers")
    @Mapping(source = "numberOfAngles", target = "numberOfAngles")
    @Mapping(source = "numberOfDevils", target = "numberOfDevils")
    @Mapping(source = "gameType", target = "gameType")
    CreatedGameSetUpDTO convertEntityToGameSetUpPostDTO(GameSetUpEntity gameSetUpEntity);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    PlayerEntity convertUserPostDTOtoEntity(PlayerPostDTO playerPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    PlayerGetDTO convertEntityToUserGetDTO(PlayerEntity playerEntity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "words", target = "words")
    CardGetDTO convertEntityToCardGetDTO(CardEntity cardEntity);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    PlayerEntity convertUserPutDTOtoEntity(PlayerPutDTO playerPutDTO);

    @Mapping(source = "userId", target = "id")
    PlayerEntity convertUserIdStringToEntity(String userId);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "id", target = "id")
    PlayerTokenDTO convertEntityToUserTokenDTO(PlayerEntity playerEntity);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "id", target = "id")
    PlayerEntity convertUserTokenDTOToEntity(PlayerTokenDTO playerTokenDTO);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "username", target = "username")
    PlayerEntity convertUserPutUserIdDTOToEntity(PlayerPutUserIdDTO playerPutUserIdDTO);

    @Mapping(source = "playerToken", target = "playerToken")
    @Mapping(source = "clue", target = "clue")
    Clue convertCluePostDTOToClue(CluePostDTO cluePostDTO);


}
