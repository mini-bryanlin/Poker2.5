package Poker;

import java.util.ArrayList;

public class CheckHand {
    public static boolean high(hand players, pot pot){
        ArrayList<Card> list = pot.GroupCard(pot);
        list.add(players.cardOne);
        list.add(players.cardTwo);
        int max = 0;
        for (Card i : list){
            if (i.value >max){
                max = i.value;
            }
        }
        players.highCard = max;
        boolean flag = true;
        return flag;
    }
    public static boolean pair(hand players, pot pot){
        ArrayList<Card> list = pot.GroupCard(pot);
        list.add(players.cardOne);
        list.add(players.cardTwo);
        int pairNum = 0;
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1 ; j < list.size(); j++) {
                if (list.get(i).value == (list.get(j).value)) {
                    pairNum = list.get(i).value;
                    players.pairNum.add(pairNum);
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
    public static boolean twoPair(hand players, pot pot){
        ArrayList<Card> list = pot.GroupCard(pot);
        list.add(players.cardOne);
        list.add(players.cardTwo);
        int pairNum = 0;
        boolean flag = false;
        int times = 0;
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1 ; j < list.size(); j++) {
                if (list.get(i).value == (list.get(j).value)) {
                    pairNum = list.get(i).value;
                    players.pairNum.add(pairNum);
                    times++;
                }
            }
        }
        if (times >= 2) {
            flag = true;
        }
        return flag;
    }
    public static boolean threeOfAKind(hand players, pot pot){
        ArrayList<Card> list = pot.GroupCard(pot);
        list.add(players.cardOne);
        list.add(players.cardTwo);
        boolean flag = false;
        boolean stop = false;
        for (int i = 0; i < list.size() && !stop; i++) {
            int times = 0;
            for (int j = 0 ; j < list.size() && !stop; j++) {
                if (list.get(i).value == list.get(j).value){
                    times ++;
                }
                if (times == 3){
                    players.threeOfAKind = list.get(i).value;
                    flag = true;
                    stop = true;
                }
            }
        }
        return flag;
    }
    public static boolean straight(hand players, pot pot){
        ArrayList<Card> list = pot.GroupCard(pot);
        list.add(players.cardOne);
        list.add(players.cardTwo);

        boolean flag = false;

        for (int i = 0; i < list.size(); i++) {
            int target = list.get(i).value;
            int streak = 1;
            boolean straight = true;
            while(straight) {
                for (Card j : list) {
                    if (target + 1 == j.value) {
                        target = j.value;
                        streak++;
                        break;
                    } else {
                        straight = false;
                    }
                }
            }
            if (streak >=5){
                flag = true;
                break;
            }
        }
        return flag;
    }


}
