// Player.java
import java.util.ArrayList;
import java.util.List;
 
public class Player {
    private String name;
    private ArrayList<Card> hand;
    private int chips;
    private boolean isActive;
    public int currentBet;
    private boolean hasActed;
    public HandStrength strength;
    
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

