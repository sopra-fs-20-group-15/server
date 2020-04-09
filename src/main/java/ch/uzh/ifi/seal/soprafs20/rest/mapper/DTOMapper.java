package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.GameLogic.Player;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically transform/map the internal representation
 * of an entity (e.g., the Player) to the external/API representation (e.g., PlayerGetDTO for getting, PlayerPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    Player convertUserPostDTOtoEntity(PlayerPostDTO playerPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    PlayerGetDTO convertEntityToUserGetDTO(Player player);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "password")
    Player convertUserPutDTOtoEntity(PlayerPutDTO playerPutDTO);

    @Mapping(source = "userId", target = "id")
    Player convertUserIdStringToEntity(String userId);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "id", target = "id")
    PlayerTokenDTO convertEntityToUserTokenDTO(Player player);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "id", target = "id")
    Player convertUserTokenDTOToEntity(PlayerTokenDTO playerTokenDTO);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "username", target = "username")
    Player convertUserPutUserIdDTOToEntity(PlayerPutUserIdDTO playerPutUserIdDTO);




}
