package Poker;
import java.util.*;
public class DeckOfCards {
    public static ArrayList<ArrayList<Integer>> create_deck(){
        ArrayList<ArrayList<Integer>> deck = new ArrayList<ArrayList<Integer>>();
        deck.add(new ArrayList<Integer>(Arrays.asList(2,3,4,5,6,7,8,9,10,11,12,13,14)));//♠
        deck.add(new ArrayList<Integer>(Arrays.asList(2,3,4,5,6,7,8,9,10,11,12,13,14)));//♢
        deck.add(new ArrayList<Integer>(Arrays.asList(2,3,4,5,6,7,8,9,10,11,12,13,14)));//♣
        deck.add(new ArrayList<Integer>(Arrays.asList(2,3,4,5,6,7,8,9,10,11,12,13,14)));//♡
        return deck;
    }




}
