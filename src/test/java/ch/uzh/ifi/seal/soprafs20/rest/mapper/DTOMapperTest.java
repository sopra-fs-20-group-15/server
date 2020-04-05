package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation works.
 */
public class DTOMapperTest {
    @Test
    public void testCreateUser_fromUserPostDTO_toUser_success() {
        // create UserPostDTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("name");
        userPostDTO.setUsername("username");

        // MAP -> Create player
        Player player = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // check content
        assertEquals(userPostDTO.getName(), player.getName());
        assertEquals(userPostDTO.getUsername(), player.getUsername());
    }

    @Test
    public void testGetUser_fromUser_toUserGetDTO_success() {
        // create Player
        Player player = new Player();
        player.setName("Firstname Lastname");
        player.setUsername("firstname@lastname");
        player.setStatus(UserStatus.OFFLINE);
        player.setToken("1");

        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(player);

        // check content
        assertEquals(player.getId(), userGetDTO.getId());
        assertEquals(player.getName(), userGetDTO.getName());
        assertEquals(player.getUsername(), userGetDTO.getUsername());
        assertEquals(player.getStatus(), userGetDTO.getStatus());
    }
}
