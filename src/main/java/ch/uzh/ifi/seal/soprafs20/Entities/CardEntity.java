package ch.uzh.ifi.seal.soprafs20.Entities;

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

public class CardEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

}

