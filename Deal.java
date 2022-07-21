package Poker;

import java.util.*;

public class Deal {
    public static Card deal (ArrayList<ArrayList<Integer>> deck){
        Random one = new Random();
        boolean flag = false;
        int suit= 0;
        int indexValue=0;
        int value = 0;
        while (!flag){
            suit = one.nextInt(4);
            if (deck.get(suit).size() != 0){
                indexValue = one.nextInt((deck.get(suit).size()-1));
                value = deck.get(suit).get(indexValue);
                deck.get(suit).remove(indexValue);
                flag = true;
            }
        }
        return new Card(suit,value);
    }
}
