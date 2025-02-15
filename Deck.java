// Deck.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
    private final List<Card> cards;
    private final Random random;
    
    public Deck() {
        this.cards = new ArrayList<>();
        this.random = new Random();
        initializeDeck();
    }
    
    private void initializeDeck() {
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }
    
    public void shuffle() {
        Collections.shuffle(cards, random);
    }
    
    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No cards remaining in the deck");
        }
        return cards.remove(cards.size() - 1);
    }
    
    public List<Card> drawCards(int numCards) {
        if (numCards > cards.size()) {
            throw new IllegalArgumentException("Not enough cards remaining in the deck");
        }
        
        List<Card> drawnCards = new ArrayList<>();
        for (int i = 0; i < numCards; i++) {
            drawnCards.add(drawCard());
        }
        return drawnCards;
    }
    
    public void returnCard(Card card) {
        cards.add(card);
    }
    
    public void returnCards(List<Card> cards) {
        this.cards.addAll(cards);
    }
    
    public int remainingCards() {
        return cards.size();
    }
    
    public void reset() {
        cards.clear();
        initializeDeck();
    }
    
    @Override
    public String toString() {
        return "Deck{" +
               "remaining cards=" + remainingCards() +
               ", cards=" + cards +
               '}';
    }
    static {
        Deck deck = new Deck();

        // Shuffle the deck
        deck.shuffle();

        // Draw a single card
        Card card = deck.drawCard();
        System.out.println("Drawn card: " + card); // Will print something like "A♠"

        // Draw multiple cards
        List<Card> hand = deck.drawCards(5);
        System.out.println("Drawn hand: " + hand);

        // Check remaining cards
        System.out.println("Remaining cards: " + deck.remainingCards());

        // Reset the deck to initial state
        deck.reset();
    }
}
