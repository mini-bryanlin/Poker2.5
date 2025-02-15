
// Card.java
public class Card {
    private final Suit suit;
    private final Rank rank;
    
    public enum Suit {
        HEARTS("♥"),
        DIAMONDS("♦"),
        CLUBS("♣"),
        SPADES("♠");
        
        private final String symbol;
        
        Suit(String symbol) {
            this.symbol = symbol;
        }
        
        public String getSymbol() {
            return symbol;
        }
    }
    public enum Rank {
        TWO(2, "2"),
        THREE(3, "3"),
        FOUR(4, "4"),
        FIVE(5, "5"),
        SIX(6, "6"),
        SEVEN(7, "7"),
        EIGHT(8, "8"),
        NINE(9, "9"),
        TEN(10, "10"),
        JACK(11, "J"),
        QUEEN(12, "Q"),
        KING(13, "K"),
        ACE(14, "A");
        
        private final int value;
        private final String symbol;
        
        Rank(int value, String symbol) {
            this.value = value;
            this.symbol = symbol;
        }
        
        public int getValue() {
            return value;
        }
        
        public String getSymbol() {
            return symbol;
        }
    }
    
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }
    
    public Suit getSuit() {
        return suit;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    @Override
    public String toString() {
        return rank.getSymbol() + suit.getSymbol();
    }
}

