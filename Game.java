// Game.java
import java.util.ArrayList;
import java.util.List;
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
        players.add(new Player(name, 100));
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