// Player.java
import java.util.List;
import java.util.ArrayList;
 
public class Player {
    private String name;
    private ArrayList<Card> hand;
    private int chips;
    private boolean isActive;
    private int currentBet;
    private boolean hasActed;
    
    public Player(String name, int startingChips) {
        this.name = name;
        this.chips = startingChips;
        this.hand = new ArrayList<>();
        this.isActive = true;
        this.currentBet = 0;
        this.hasActed = false;
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public List<Card> getHand() {
        return hand;
    }
    
    public int getChips() {
        return chips;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public int getCurrentBet() {
        return currentBet;
    }
    
    // Game actions
    public void receiveCard(Card card) {
        hand.add(card);
    }
    
    public void placeBet(int amount) {
        if (amount > chips) {
            throw new IllegalArgumentException("Not enough chips!");
        }
        chips -= amount;
        currentBet += amount;
        hasActed = true;
    }
    
    public void fold() {
        isActive = false;
        hand.clear();
        hasActed = true;
    }
    
    public void win(int amount) {
        chips += amount;
    }
    
    public void resetForNewRound() {
        hand.clear();
        currentBet = 0;
        isActive = true;
        hasActed = false;
    }
    
    @Override
    public String toString() {
        return String.format("%s (Chips: %d)", name, chips);
    }
}

// Game.java
public class Game {
    private List<Player> players;
    private Deck deck;
    private int currentDealer;
    private int bigBlind;
    private int smallBlind;
    
    public Game(int startingChips, int smallBlind) {
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.currentDealer = 0;
        this.smallBlind = smallBlind;
        this.bigBlind = smallBlind * 2;
    }
    
    public void addPlayer(String name) {
        players.add(new Player(name, startingChips));
    }
    
    public void removePlayer(String name) {
        players.removeIf(player -> player.getName().equals(name));
    }
    
    public void startNewRound() {
        // Reset deck and shuffle
        deck.reset();
        deck.shuffle();
        
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