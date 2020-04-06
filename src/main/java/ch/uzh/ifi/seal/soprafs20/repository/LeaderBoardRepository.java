package ch.uzh.ifi.seal.soprafs20.repository;
import ch.uzh.ifi.seal.soprafs20.entity.LeaderBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface LeaderBoardRepository extends CrudRepository<LeaderBoard,Long> {
}
