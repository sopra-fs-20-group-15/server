package ch.uzh.ifi.seal.soprafs20.repository;
import ch.uzh.ifi.seal.soprafs20.GameLogic.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("cardRepository")
public interface CardRepository extends JpaRepository<Card, Long>{
}
