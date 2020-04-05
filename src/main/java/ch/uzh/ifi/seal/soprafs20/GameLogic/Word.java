package ch.uzh.ifi.seal.soprafs20.GameLogic;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
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
@Table(name = "WORD")
public class Word implements Serializable {

    private static final long serialVersionUID = 1L;
/**Cannot be deleted since it is the primary key*/
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String word;
}
