package ch.uzh.ifi.seal.soprafs20.repository;
import ch.uzh.ifi.seal.soprafs20.Entities.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("chatRepository")
public interface ChatRepository extends JpaRepository<ChatEntity, Long>{
}
