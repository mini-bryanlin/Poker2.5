// Game.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
public class Game {
    private List<Player> players;
    private Deck deck;
    private int currentDealer;
    private int bigBlind;
    private int smallBlind;
    private int startingChips;
    private int pot;
    private ArrayList communityCards;
    
    public Game(int startingChips, int smallBlind) {
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.currentDealer = 0;
        this.smallBlind = smallBlind;
        this.bigBlind = smallBlind * 2;
        this.startingChips = startingChips;
        this.pot = 0;
        this.communityCards = new ArrayList<>();
    }
    // adding community cards
    public void dealCommunityCards(int numCards) {
        if (numCards < 0 || numCards > 5) {
            throw new IllegalArgumentException("Number of community cards must be between 0 and 5");
        }
        communityCards.addAll(deck.drawCards(numCards));
    }
    // adding to pot
    public void addToPot(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot add a negative amount to the pot");
        }
        pot += amount;
    }
    // get pot 
    public int getPot() {
        return pot;
    }
    //add player
    public void addPlayer(String name) {
        players.add(new Player(name, startingChips));
    }
    //reset pot
    public void resetPot() {
        pot = 0;
    }

    public void removePlayer(String name) {
        players.removeIf(player -> player.getName().equals(name));
    }
    public void resetCommunityCards() {
        communityCards.clear();
    }
    // reset game
    public void resetGame() {
        resetPot();
        resetCommunityCards();
        deck.reset();
        deck.shuffle();
    }
    private HandStrength evaluateHandStrength(List<Card> cards) {
        int pairCount = 0;
        boolean hasThreeOfAKind= false;
        boolean hasFourOfAKind = false;
        ArrayList values = new ArrayList();
        HashMap<Integer,Integer> valueCounts = new HashMap();
        for (Card card : cards) {
            
            int value = card.getRank().getValue();
            values.add(value);
            valueCounts.put(value, valueCounts.getOrDefault(value, 0) + 1);
        }
        for (int count : valueCounts.values()) {
            if (count == 2) {
                pairCount++;
            } else if (count == 3) {
                hasThreeOfAKind = true;
            } else if (count == 4) {
                hasFourOfAKind = true;
            }
        Collections.sort(values);   
        }
        if (hasFourOfAKind) {
            return HandStrength.FOUR_OF_A_KIND;
        }
        else if (hasThreeOfAKind && pairCount >= 1) {
            return HandStrength.FULL_HOUSE; // Full house: three of a kind + at least one pair
        } else if (hasThreeOfAKind) {
            return HandStrength.THREE_OF_A_KIND; // Three of a kind
        } else if (pairCount >= 2) {
            return HandStrength.TWO_PAIR; // Two pairs
        } else if (pairCount == 1) {
            return HandStrength.ONE_PAIR; // One pair
        } else {
            return HandStrength.HIGH_CARD; // High card
        }   
    }
    
    private static boolean isStraight(ArrayList<Integer> values) {
        // Sort the ArrayList in ascending order
        Collections.sort(values);

        // Check all possible 5-card combinations
        for (int i = 0; i <= values.size() - 5; i++) {
            // Check if the 5 cards are consecutive
            if (values.get(i + 4) - values.get(i) == 4) {
                return true; // Straight found
            }
        }

        // Special case for low straight (A-2-3-4-5)
        // Check if the ArrayList contains 2, 3, 4, 5, and 14 (Ace)
        if (values.contains(2) &&
            values.contains(3) &&
            values.contains(4) &&
            values.contains(5) &&
            values.contains(14)) {
            return true; // Low straight found
        }

        return false; // No straight found
    }
        
    
    private boolean isFlush(ArrayList<Card> cards) {
        HashMap<Card.Suit, Integer> suitCount = new HashMap<>();
        for (Card card : cards) {
            suitCount.put(card.getSuit(), suitCount.getOrDefault(card.getSuit(), 0) + 1);
            if (suitCount.get(card.getSuit()) >= 5) {
                return true;
            }
        }
        return false;
    }

    

    
    
    public HandStrength evaluateHand(Player player) {
        // Ensure the player's hand is not null
        if (player == null || player.getHand() == null) {
            throw new IllegalArgumentException("Player or player's hand cannot be null");
        }
    
        // Ensure communityCards is not null
        if (communityCards == null) {
            throw new IllegalStateException("Community cards have not been initialized");
        }
    
        // Combine the player's hand and community cards
        List<Card> allCards = new ArrayList<>(player.getHand());
        allCards.addAll(communityCards);
    
        // Ensure there are exactly 7 cards (2 in hand + 5 community cards)
        if (allCards.size() != 7) {
            throw new IllegalStateException("Exactly 7 cards are required to evaluate a hand");
        }
    
        // Evaluate the hand strength
        return evaluateHandStrength(allCards);
    }

    public void startNewRound() {
        // Reset deck and shuffle
        resetGame();
        
        // Reset all players
        for (Player player : players) {
            player.resetForNewRound();
        }
        
        // Deal cards
        for (int i = 0; i < 2; i++) {  // Two cards per player
            for (Player player : players) {
                player.receiveCard(deck.drawCard());
            }
        }

        // Post blinds
        int smallBlindPos = (currentDealer + 1) % players.size();
        int bigBlindPos = (currentDealer + 2) % players.size();
        
        players.get(smallBlindPos).placeBet(smallBlind);
        players.get(bigBlindPos).placeBet(bigBlind);
        
        // Move dealer button for next round
        currentDealer = (currentDealer + 1) % players.size();
    }
}