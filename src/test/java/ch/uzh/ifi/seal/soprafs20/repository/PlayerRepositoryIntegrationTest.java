package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByName_success() {
        // given
        Player player = new Player();
        player.setName("Firstname Lastname");
        player.setUsername("firstname@lastname");
        player.setStatus(UserStatus.OFFLINE);
        player.setToken("1");

        entityManager.persist(player);
        entityManager.flush();

        // when
        Player found = userRepository.findByName(player.getName());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getName(), player.getName());
        assertEquals(found.getUsername(), player.getUsername());
        assertEquals(found.getToken(), player.getToken());
        assertEquals(found.getStatus(), player.getStatus());
    }
}
