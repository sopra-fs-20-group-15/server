package ch.uzh.ifi.seal.soprafs20.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
/**
@DataJpaTest
public class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void findByName_success() {
        // given
        PlayerEntity player = new PlayerEntity();
        player.setName("Firstname Lastname");
        player.setUsername("firstname@lastname");
        player.setStatus(PlayerStatus.OFFLINE);
        player.setToken("1");

        entityManager.persist(player);
        entityManager.flush();

        // when
        PlayerEntity found = playerRepository.findByName(player.getName());

        // then
        assertNotNull(found.getId());
        assertEquals(found.getName(), player.getName());
        assertEquals(found.getUsername(), player.getUsername());
        assertEquals(found.getToken(), player.getToken());
        assertEquals(found.getStatus(), player.getStatus());
    }
}*/
