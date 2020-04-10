package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import ch.uzh.ifi.seal.soprafs20.constant.GameType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class GameSetUpRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Qualifier("gameSetUpEntityRepository")
    @Autowired
    private GameSetUpRepository gameSetUpEntityRepository;

    @Test
    public void setGameSetup_success() {
        // given
        GameSetUpEntity gameSetUp = new GameSetUpEntity();
        gameSetUp.setNumberOfPlayers(3L);
        gameSetUp.setNumberOfBots(0L);
        gameSetUp.setGameType(GameType.PRIVATE);
        gameSetUp.setPassword("123");
        gameSetUp.setHostId(1L);

        entityManager.persist(gameSetUp);
        entityManager.flush();

        // when
        GameSetUpEntity found = gameSetUpEntityRepository.findByNumberOfBots(gameSetUp.getNumberOfBots());

        // then
        assertEquals(found.getNumberOfPlayers(), gameSetUp.getNumberOfPlayers());
        assertEquals(found.getNumberOfBots(), gameSetUp.getNumberOfBots());
        assertEquals(found.getGameType(), gameSetUp.getGameType());
    }
}
