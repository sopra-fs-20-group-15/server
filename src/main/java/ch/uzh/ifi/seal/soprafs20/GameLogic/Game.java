package ch.uzh.ifi.seal.soprafs20.GameLogic;

import javax.persistence.*;
import java.util.List;

/**
 * Internal Player Representation
 * This class composes the internal representation of the user and defines how the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 *  nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
//Even though there is a red line beneath "Player", it should still work
@Table(name = "GAME")
public class Game {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long activePlayerId;

    @Column(nullable = false)
    private Boolean rightGuess;

    @Column(nullable = false)
    private Boolean validClue;

    @ElementCollection
    List<Long> passivePlayerIds;

    @ElementCollection
    List<Long> CardIds;

    @Column(nullable = false)
    private Long Milliseconds;

    @Column(nullable = false)
    private Long nrOfDuplicates;




}
