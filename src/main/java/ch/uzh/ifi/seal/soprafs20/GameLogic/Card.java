package ch.uzh.ifi.seal.soprafs20.GameLogic;

import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumber;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotANumberbetweenOneAndFive;
import ch.uzh.ifi.seal.soprafs20.repository.CardRepository;
import ch.uzh.ifi.seal.soprafs20.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal PlayerEntity Representation
 * This class composes the internal representation of the user and defines how the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 *  nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
//Even though there is a red line beneath "CARD", it should still work
@Table(name = "CARD")

public class Card {

    @Id
    @GeneratedValue
    protected Long id;

    @ElementCollection
    protected List<String> words = new ArrayList<String>();


/**Getters and Setters*/
    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    /**
     * functions about controlling and getting the Id of a word on a card
     */


}

