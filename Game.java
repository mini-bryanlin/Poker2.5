package Poker;
import java.util.*;
public class Game {
    static ArrayList<hand> max = new ArrayList<hand>();
    static ArrayList<hand> players = new ArrayList<hand>();
    static ArrayList<String> suitSymbol = new ArrayList();
    private static boolean add;
    public static void setMax() {
        hand playerOne = new hand();
        max.add(playerOne);
        hand playerTwo = new hand();
        max.add(playerTwo);
        hand playerThree = new hand();
        max.add(playerThree);
        hand playerFour = new hand();
        max.add(playerFour);
        hand playerFive = new hand();
        max.add(playerFive);
        hand playerSix = new hand();
        max.add(playerSix);
        hand playerSeven = new hand();
        max.add(playerSeven);
        hand playerEight = new hand();
        max.add(playerEight);
        hand playerNine = new hand();
        max.add(playerNine);
        hand playerTen = new hand();
        max.add(playerTen);
        hand playerEleven = new hand();
        max.add(playerEleven);
        hand playerTwelve = new hand();
        max.add(playerTwelve);
        hand playerThirteen = new hand();
        max.add(playerThirteen);
        hand playerFourteen = new hand();
        max.add(playerFourteen);

    }
    public static void setPlayers(){
        Scanner raw = new Scanner(System.in);
        System.out.print("Number of players: \n");
        int playerNumber=0;
        try{
            playerNumber = raw.nextInt();
            for (int i = 0; i < playerNumber; i++){
                hand hello = max.get(i);
                players.add(hello);
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("Too many players| Max number is 14");
        }

    }
        public static void main(String[] args) {
        ArrayList<ArrayList<Integer>> deck = DeckOfCards.create_deck();
        suitSymbol.add("♠");
        suitSymbol.add("♢");
        suitSymbol.add("♣");
        suitSymbol.add("♡");
        Game.setMax();
        Game.setPlayers();
        pot currentPot = new pot();
        // Deal| 2 cards to every player
        for (hand i: players) {
            hand temp = i;
            for (int j = 1; j <= 2; j++) {
                Card tempCard = Deal.deal(deck);
                if (j == 1) {
                    temp.cardOne = tempCard;
                } else if (j == 2) {
                    temp.cardTwo = tempCard;
                }
            }
        }
        for (hand i: players){
            System.out.println(i.cardOne.value + " of " + suitSymbol.get(i.cardOne.suit) + " and " +i.cardTwo.value + " of " + suitSymbol.get(i.cardTwo.suit));
        }
       
        Action.manageBet(players,currentPot);
        currentPot.flop(deck,currentPot);
        System.out.println("Flop: " + currentPot.cardOne.value + " of " + currentPot.cardOne.suit + " and "+ currentPot.cardTwo.value + " of " + currentPot.cardTwo.suit + " and "+currentPot.cardThree.value + " of " + currentPot.cardThree.suit);
        Action.manageBet(players,currentPot);
        currentPot.turn(deck,currentPot);
        System.out.println("Turn : " + currentPot.cardOne.value + " of " + currentPot.cardOne.suit + " and "+ currentPot.cardTwo.value + " of " + currentPot.cardTwo.suit + " and "+currentPot.cardThree.value + " of " + currentPot.cardThree.suit + " and "+ currentPot.cardFour);
        Action.manageBet(players,currentPot);
        currentPot.river(deck,currentPot);
        System.out.println("River: " + currentPot.cardOne.value + " of " + currentPot.cardOne.suit + " and "+ currentPot.cardTwo.value + " of " + currentPot.cardTwo.suit + " and "+currentPot.cardThree.value + " of " + currentPot.cardThree.suit+ " and "+ currentPot.cardFour+ " and "+ currentPot.cardFive);
    }
}
