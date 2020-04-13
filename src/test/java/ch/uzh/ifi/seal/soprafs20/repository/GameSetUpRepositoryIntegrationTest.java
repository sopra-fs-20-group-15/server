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
        gameSetUp.setGameName("A");
        gameSetUp.setNumberOfPlayers(3L);
        gameSetUp.setNumberOfAngles(0L);
        gameSetUp.setNumberOfDevils(0L);
        gameSetUp.setGameType(GameType.PRIVATE);
        gameSetUp.setPassword("123");
        gameSetUp.setHostName("Usher");

        entityManager.persist(gameSetUp);
        entityManager.flush();

        // when
        GameSetUpEntity found = gameSetUpEntityRepository.findByGameName("A");

        // then
        assertEquals(found.getGameName(), gameSetUp.getGameName());
        assertEquals(found.getNumberOfPlayers(), gameSetUp.getNumberOfPlayers());
        assertEquals(found.getNumberOfAngles(), gameSetUp.getNumberOfAngles());
        assertEquals(found.getNumberOfDevils(), gameSetUp.getNumberOfDevils());
        assertEquals(found.getGameType(), gameSetUp.getGameType());
        assertEquals(found.getPassword(), gameSetUp.getPassword());
        assertEquals(found.getHostName(), gameSetUp.getHostName());
    }
}
