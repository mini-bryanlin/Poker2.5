// Game.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
public class Game {
    private ArrayList<Player> players;
    private Deck deck;
    private int currentDealer;
    private int bigBlind;
    private int smallBlind;
    private int startingChips;
    private int pot;
    private ArrayList communityCards;
    private Scanner scanner = new Scanner(System.in);
    
    public Game(ArrayList players,int startingChips, int smallBlind) {
        this.players = players;
        this.deck = new Deck();
        this.currentDealer = 0;
        this.smallBlind = smallBlind;
        this.bigBlind = smallBlind * 2;
        this.startingChips = startingChips;
        this.pot = 0;
        this.communityCards = new ArrayList<>();
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
    private HandStrength evaluateHandStrength(ArrayList<Card> cards) {
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
           
        }
        Collections.sort(values);
        if (isRoyalFlush(cards)) {
            return HandStrength.ROYAL_FLUSH;
        }
         else if (isFlush(cards)&& isStraight(values)) {
            return HandStrength.STRAIGHT_FLUSH;
        }else if (hasFourOfAKind) {
            return HandStrength.FOUR_OF_A_KIND;
        }else if (hasThreeOfAKind && pairCount >= 1) {
            return HandStrength.FULL_HOUSE;
        }else if (isFlush(cards)) {
            return HandStrength.FLUSH;
        }else if (isStraight(values)) {
            return HandStrength.STRAIGHT;
        } else if (hasThreeOfAKind) {
            return HandStrength.THREE_OF_A_KIND;
        } else if (pairCount >= 2) {
            return HandStrength.TWO_PAIR;
        } else if (pairCount == 1) {
            return HandStrength.ONE_PAIR;
        } else if (isStraight(values)) {
            return HandStrength.STRAIGHT;
        } else {
            return HandStrength.HIGH_CARD;
        }
    }
    
    public static boolean isRoyalFlush(ArrayList<Card> cards) {
        // Check if there are at least 5 cards
        

        // Create a set of required ranks for a royal flush
        ArrayList<Card.Rank> requiredRanks = new ArrayList<>();
        requiredRanks.add(Card.Rank.TEN);
        requiredRanks.add(Card.Rank.JACK);
        requiredRanks.add(Card.Rank.QUEEN);
        requiredRanks.add(Card.Rank.KING);
        requiredRanks.add(Card.Rank.ACE);

        // Check if all required ranks are present in the hand
        ArrayList<Card.Rank> ranksInHand = new ArrayList<>();
        for (Card card : cards) {
            ranksInHand.add(card.getRank());
        }

        if (!ranksInHand.containsAll(requiredRanks)) {
            return false; // Not all required ranks are present
        }

        // Check if all required cards are of the same suit
        Card.Suit suit = null;
        for (Card card : cards) {
            if (requiredRanks.contains(card.getRank())) {
                if (suit == null) {
                    suit = card.getSuit(); // Set the suit for the first card
                } else if (card.getSuit() != suit) {
                    return false; // Cards are not of the same suit
                }
            }
        }

        return true; // Royal flush found
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

    

    
    
    public void evaluateHand(Player player) {
        // Ensure the player's hand is not null
        if (player == null || player.getHand() == null) {
            throw new IllegalArgumentException("Player or player's hand cannot be null");
        }
    
        // Ensure communityCards is not null
        if (communityCards == null) {
            throw new IllegalStateException("Community cards have not been initialized");
        }
    
        // Combine the player's hand and community cards
        ArrayList<Card> allCards = new ArrayList<>(player.getHand());
        allCards.addAll(communityCards);
    
        // Ensure there are exactly 7 cards (2 in hand + 5 community cards)
        if (allCards.size() != 7) {
            throw new IllegalStateException("Exactly 7 cards are required to evaluate a hand");
        }
    
        // Evaluate the hand strength
        player.strength = evaluateHandStrength(allCards);
    }
    private List<Player> getActivePlayers() {
        List<Player> activePlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.isActive()) {
                activePlayers.add(player);
            }
        }
        return activePlayers;
    }
    private void dealCommunityCards(int numCards) {
        for (int i = 0; i < numCards; i++) {
            communityCards.add(deck.drawCard());
        }
        System.out.println("Community cards: " + communityCards);
    }
    private void determineWinner() {
        for (Player player: players){
            evaluateHand(player);
        }
        Player winner = getActivePlayers().get(0);
        for (Player player: players){
            if (player.strength.getStrength() > winner.strength.getStrength()){
                winner = player;
            }
        }
        
        System.out.println(winner.getName() + " wins the pot of " + pot + " chips!");
        winner.win(pot);
    }
    public void startGame(int smallBlindindex) {
        System.out.println("Starting a new poker game!");
        deck.shuffle();
    
        // Deal 2 cards to each player (pre-flop)
        for (Player player : players) {
            player.receiveCard(deck.drawCard());
            player.receiveCard(deck.drawCard());
            System.out.println(player.getName() + " has been dealt 2 cards.");
        }
        //blinds
        System.out.println("\n--- Blinds ---");
        players.get(smallBlindindex).placeBet(smallBlind);
        System.out.println(players.get(smallBlindindex).getName()+" is the small blind for " + smallBlind);
        players.get(smallBlindindex+1).placeBet(smallBlind*2);
        System.out.println(players.get(smallBlindindex+1).getName()+" is the big blind for " + smallBlind*2);
        // Pre-flop round
        System.out.println("\n--- Pre-flop Round ---");
        bettingRound();
    
        // Flop round
        if (getActivePlayers().size() > 1) {
            System.out.println("\n--- Flop Round ---");
            dealCommunityCards(3);
            bettingRound();
             
        }
    
        // Turn round
        if (getActivePlayers().size() > 1) {
            System.out.println("\n--- Turn Round ---");
            dealCommunityCards(1);
            bettingRound();
        }
    
        // River round
        if (getActivePlayers().size() > 1) {
            System.out.println("\n--- River Round ---");
            dealCommunityCards(1);
            bettingRound();
        }
    
        // Showdown
        if (getActivePlayers().size() > 1) {
            System.out.println("\n--- Showdown ---");
            determineWinner();
        } else {
            Player winner = getActivePlayers().get(0);
            System.out.println("\nAll players folded. " + winner.getName() + " wins the pot of " + pot + " chips!");
            winner.win(pot);
        }
    }
    private boolean checkCalled(ArrayList players, int currentBet){
        for (Player player : getActivePlayers()){
            if (player.getCurrentBet() != currentBet){
                return false;
            }

        }
        return true;
    }

    private void bettingRound() {
        int currentBet = 0;
        
        for (Player player : getActivePlayers()) {
            System.out.println("\n" + player.getName() + ", it's your turn.");
            System.err.println("Pot: " + pot);
            System.out.println("Community cards: " + communityCards);
            System.out.println("Your hand: " + player.getHand());
            System.out.println("Current bet: " + currentBet);
            System.out.println("Your chips: " + player.getChips());
            
            System.out.println("Choose an action: (1) Fold, (2) Call, (3) Bet, (4) Stand");
            int choice = scanner.nextInt();
    
            switch (choice) {
                case 1: // Fold
                    player.fold();
                    System.out.println(player.getName() + " folds.");
                    break;
                case 2: // Call
                    
                    if (currentBet == 0){
                        System.out.println("Cannot Call");
                        break;
                    }
                    int callAmount = currentBet - player.getCurrentBet();
                    player.placeBet(callAmount);
                    pot += callAmount;
                    System.out.println(player.getName() + " calls " + callAmount + " chips.");
                    break;
                case 3: // Bet
                    System.out.println("Enter the amount to bet:");
                    int betAmount = scanner.nextInt();
                    player.placeBet(betAmount);
                    pot += betAmount;
                    currentBet = betAmount;
                    System.out.println(player.getName() + " bets " + betAmount + " chips.");
                    break;
                case 4: // Stand
                    System.out.println(player.getName() + " stands.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
        while (checkCalled(players, currentBet)==false){
            for (Player player : getActivePlayers()) {
                if (player.getCurrentBet() == currentBet){
                    continue;
                }
                System.out.println("\n" + player.getName() + ", it's your turn.");
                System.err.println("Pot: " + pot);
                System.out.println("Community cards: " + communityCards);
                System.out.println("Your hand: " + player.getHand());
                System.out.println("Current bet: " + currentBet);
                System.out.println("Your chips: " + player.getChips());
                
                System.out.println("Choose an action: (1) Fold, (2) Call, (3) Bet, (4) Stand");
                int choice = scanner.nextInt();
        
                switch (choice) {
                    case 1: // Fold
                        player.fold();
                        System.out.println(player.getName() + " folds.");
                        break;
                    case 2: // Call
                        
                        if (currentBet == 0){
                            System.out.println("Cannot Call");
                            break;
                        }
                        int callAmount = currentBet - player.getCurrentBet();
                        player.placeBet(callAmount);
                        pot += callAmount;
                        System.out.println(player.getName() + " calls " + callAmount + " chips.");
                        break;
                    case 3: // Bet
                        System.out.println("Enter the amount to bet:");
                        int betAmount = scanner.nextInt();
                        player.placeBet(betAmount);
                        pot += betAmount;
                        currentBet = betAmount;
                        System.out.println(player.getName() + " bets " + betAmount + " chips.");
                        break;
                    case 4: // Stand
                        System.out.println(player.getName() + " stands.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }

        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask for the number of players
        System.out.println("Enter the number of players (2-8):");
        int numPlayers = scanner.nextInt();

        // Validate the number of players
        if (numPlayers < 2 || numPlayers > 8) {
            System.out.println("Invalid number of players. Please enter a number between 2 and 8.");
            return;
        }

        // Initialize players
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 1; i <= numPlayers; i++) {
            System.out.println("Enter name for Player " + i + ":");
            String name = scanner.next();
            players.add(new Player(name, 1000)); // Each player starts with 1000 chips
        }
        
        // Start the game
        int time = 0;
        while (true){
            int blind = time%numPlayers;
            Game game = new Game(players,1000,5);
            game.startGame(blind);
            time += 1;
        
        }
    }
}

