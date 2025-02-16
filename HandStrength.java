public enum HandStrength {
    HIGH_CARD(0,"High Card"),
    ONE_PAIR(1,"One Pair"),
    TWO_PAIR(2,"Two Pair"),
    THREE_OF_A_KIND(3, "Three of a Kind"),
    STRAIGHT(4, "Straight"),
    FLUSH(5, "Flush"),
    FULL_HOUSE(6, "Full House"),
    FOUR_OF_A_KIND(7, "Four of a Kind"),
    STRAIGHT_FLUSH(8, "Straight Flush"),
    ROYAL_FLUSH(9, "Royal Flush");

    private int strength;
    private String name;
    HandStrength(int strength, String name) {
        this.strength = strength;
        this.name = name;
    }
    public int getStrength() {
        return strength;
    }
    public String getName() {
        return name;
    }

}