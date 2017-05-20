package server.model.card;

/*
 * @author  ab3llini
 * @since   19/05/17.
 */

import java.util.ArrayList;
import java.util.Collections;

/**
 *  * Represents a card desk of custom type
 * @param <T> the type of the deck cards
 */
public class Deck<T> {

    /**
     * The cards contained in the deck
     */
    ArrayList<T> cards;

    /**
     * Constructor with a provided array list of cards
     * @param cards the cards of the deck
     */
    public Deck (ArrayList<T> cards) {

        this();

        this.cards = cards;

    }

    /**
     * Constructor without any card
     * Cards shall be provided via setters
     */
    public Deck () {

        this.cards = new ArrayList<T>();

    }

    /**
     * Adds a card to the deck
     * @param card the card to be added
     * @return true or false
     */
    public boolean addCard(T card) {

        return this.cards.add(card);

    }

    /**
     * Removes a card to the deck
     * @param card the card to be removed
     * @return true or false
     */
    public boolean removeCard(T card) {

        return this.cards.remove(card);

    }

    /**
     * Shuffle the cards!
     */
    public Deck<T> shuffle() {

        Collections.shuffle(this.cards);

        return this;

    }

    public ArrayList<T> getCards() {
        return cards;
    }
}
