package Poker;

import java.util.*;

public class Action {
    public static void giveToPot(hand player,hand pot,int amount){
        hand.transfer(player,pot,amount);
    }
    public static void manageBet(ArrayList<hand> players,hand pot) {
        boolean flag = false;
        Scanner input = new Scanner(System.in);
        int maxAmount = 0;
        while (flag == false) { // flag is for making sure there is no more bets
            for (hand i : players) {
                boolean innerFlag = false;
                while (innerFlag == false) { // innerFlag is for making sure the choice is available
                    System.out.println("|Bet(1)|Call(2)|Raise(3)|Fold(4)|Check(5)");
                    int choice =0;
                    try{// to correct
                        choice = input.nextInt();
                    }catch(Exception e){
                        System.out.println("Not a valid choice try again");
                    }
                    if (choice == 1) {
                        if (maxAmount == 0) {
                            System.out.println("Amount: ");
                            int amount = input.nextInt();
                            giveToPot(i, pot, amount);
                            innerFlag = true;
                            maxAmount = amount;
                            i.currentAmount = amount;
                        } else {
                            System.out.println("Betting is not an option");
                        }
                    } else if (choice == 2) {
                        if (maxAmount != 0) {
                            int amount = maxAmount- i.currentAmount;
                            giveToPot(i, pot, amount);
                            innerFlag = true;
                            i.currentAmount = amount;
                        } else {
                            System.out.println("Calling is not an option");
                        }
                    }else if (choice == 3) {
                        if (maxAmount != 0) {
                            boolean innerInner = false;
                            while (innerInner == false) { //innerInner is for making sure the raised amount is valid
                                System.out.println("Amount (At least twice from the previous bet): ");
                                int amount = input.nextInt();
                                giveToPot(i, pot, amount);
                                innerInner = true;
                                innerFlag = true;
                                maxAmount = amount;
                                i.currentAmount = amount;
                            }
                        } else {
                            System.out.println("Raising is not an option");
                        }
                    }else if (choice == 4) {
                        players.remove(i);
                    }else if (choice == 5) {
                        if (maxAmount == 0) {

                        } else {
                            System.out.println("Checking is not an option");
                        }
                    }

                }
            }
            // check if there is no more betting to do
            for (hand i: players){
                if (i.currentAmount == maxAmount){
                    flag = true;
                }else{
                    flag = false;
                }
            }
        }
        // reset players current amount to 0
        for (hand i: players){
            i.currentAmount = 0;
        }
    }
}

