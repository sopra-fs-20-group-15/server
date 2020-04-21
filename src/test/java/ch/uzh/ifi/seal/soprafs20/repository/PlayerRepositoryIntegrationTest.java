package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.Entities.PlayerEntity;
import ch.uzh.ifi.seal.soprafs20.constant.PlayerStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void findByName_success() {
        // given
        PlayerEntity player = new PlayerEntity();
        player.setUsername("abc");
        player.setToken("1");
        player.setPassword("erh");
        player.setLeaderBoardScore(1);
        player.setStatus(PlayerStatus.ONLINE);

        entityManager.persist(player);
        entityManager.flush();

        // when
        PlayerEntity found = playerRepository.findByUsername(player.getUsername());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getUsername(), player.getUsername());
        assertEquals(found.getToken(), player.getToken());
    }
}

