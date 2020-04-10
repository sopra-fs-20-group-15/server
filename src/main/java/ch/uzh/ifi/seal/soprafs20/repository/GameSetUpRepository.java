package ch.uzh.ifi.seal.soprafs20.repository;

import ch.uzh.ifi.seal.soprafs20.Entities.GameSetUpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("gameSetUpEntityRepository")
public interface GameSetUpRepository extends JpaRepository<GameSetUpEntity, Long> {
    GameSetUpEntity findByNumberOfBots(Long numberOfBots);
}
