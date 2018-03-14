package Model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author adri
 */
public class Hand {
    private ArrayList<Card> hand ;
    private int bestRank;

    /**
     * Construcor
     */
    public Hand(){
        hand = new ArrayList<>();
    }
    
    /**
     * Decided best hand 
     * @return  
     */
    public int solHand(){
        order(hand);//ma del client

        int result = 0;
        if(straightColor()){
            result = 1;
        }
        else if(poker()){
            result = 2;
        }
        else if(full()){
            result = 3;
        }
        else if(flush()){
            result = 4;
        }
        else if(straight()){
            result = 5;
        }
        else if(three()){
            result = 6;
        }
        else if(twoPair()){
            result = 7;
        }
        else if(onePair()){
            result = 8;
        }
        else{
            highCard();
            result = 9;
        
        }
        return result;
    }
    
    /**
     * Straight Color hand
     * @return 
     */
    public boolean straightColor(){
        for(int i = 1; i < hand.size(); i++){
            if(straight() && flush() ){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Poker  hand
     * @return 
     */
    public boolean poker(){
    
        for(int i = 3; i < hand.size(); i++){
            if(hand.get(i-3).getRank() == hand.get(i).getRank()){
                setBestRank(getValue(hand.get(i).getRank()));
                return true;
            }
        }
        return false;
    }
    
    /**
     * Full hand
     * @return 
     */
    public boolean full(){
        
        boolean t = false;
        String r = "";
        for(int i = 2 ; i < hand.size(); i++){
            if(hand.get(i-2).getRank() == hand.get(i).getRank()){
                t = true;
                r = hand.get(i).getRank();
            }
        }
        // si tenim un trio, busquem si teni dues cates iguals
        if(t){
            for(int i = 1 ; i< hand.size(); i++){
                if(hand.get(i-1).getRank() == hand.get(i).getRank() && hand.get(i).getRank() != r){
                    setBestRank(getValue(hand.get(i).getRank()));
                    return true;
                }
            }  
        }
        
        t = false;
        for(int i = 1 ; i< hand.size(); i++){
                if(hand.get(i-1).getRank() == hand.get(i).getRank()){
                    t = true;
                    r = hand.get(i).getRank();
                }
        }
        if(t){
            for(int i = 2 ; i < hand.size(); i++){
                if(hand.get(i-2).getRank() == hand.get(i).getRank() && r != hand.get(i).getRank()){
                    setBestRank(getValue(hand.get(i).getRank()));
                    return true;
            }
        }
        }
        return false;
    }
    
    /**
     * Flush hand
     * @return 
     */
    public boolean flush(){
        for(int i = 1; i < hand.size(); i++){
            if(hand.get(i-1).getSuit() != hand.get(i).getSuit()){
                return false;
            }
        }
        setBestRank(getValue(hand.get(0).getRank()));
        return true;
    }
    
    /**
     * Straight hand
     * @return 
     */
    public boolean straight(){     
        for(int i = 1; i < hand.size();i++){
            if(getValue(hand.get(i-1).getRank()) != (getValue(hand.get(i).getRank())-1)){
                return false;
            }
            
        }
        setBestRank(getValue(hand.get(4).getRank() ));
        return true;
    }
    
    /**
     * Three hand
     * @return 
     */
    public boolean three(){      
        for(int i = 2; i < hand.size(); i++){
            if(hand.get(i-2).getRank() == hand.get(i).getRank()){
                setBestRank(getValue(hand.get(i-1).getRank() ));
                return true;
            }
        }

        return false;
    }
    
    /**
     * Two pair hand
     * @return 
     */
    public boolean twoPair(){
        int numPairs = 0;
        String pairTrobat = "";
        for(int i = 1; i < hand.size(); i++){
            if(hand.get(i-1).getRank() == hand.get(i).getRank()){
                numPairs += 1;
                if(numPairs == 2){
                    setBestRank(getValue(hand.get(i-1).getRank() ));
                    return true;
                }             
            }
        }
        return false;              
    }
    
   /**
    * Pair hand
    * @return 
    */
    public boolean onePair(){     
        for(int i = 1; i < hand.size(); i++){
            if(hand.get(i-1).getRank() == hand.get(i).getRank()){
                setBestRank(getValue(hand.get(i).getRank()));
                return true;              
            }            
        }       
        return false;   
    }
    
    /**
     * High card hand
     */
    public void highCard(){
        String maxC = hand.get(0).getRank();
        for(int i  = 1 ; i < hand.size(); i++){
            if(getValue(maxC) < getValue(hand.get(i).getRank())){
                maxC = hand.get(i).getRank();
            }          
        }
    }
    
    /**
     * 
     * @return 
     */
    public boolean cStr(){
        int numC = 0 ;
        
        for(int i = 0; i < hand.size(); i++){
            for(int j = 0; j < hand.size(); j++){
        
            }
        }
        return false;
    }
    
    /**
     * Returns card value by rank
     * @param c
     * @return 
     */
    public int getValue(String c){
        int value = 0;
        switch(c){
            case "2":
                value = 2;               
            case "3":
                value = 3;       
            case "4":
                value = 4; 
            case "5":
                value = 5; 
            case "6":
                value = 6; 
            case "7":
                value = 7; 
            case "8":
                value = 8; 
            case "9":
                value = 9; 
            case "10":
                value = 10; 
            case "J":
                value = 11; 
            case "Q":
                value = 12; 
            case "K":
                value = 13;
            case "A":
                value = 14;   
        }

        return value;
    }
    /**
     * 
     * @param Cards 
     */
    public void order(ArrayList<Card>  Cards){
        Collections.sort(Cards, new Comparator<Card>(){
        public int compare(Card a, Card b) {
            int resultado = a.getRank().compareTo(b.getRank());
            if (resultado != 0 ) {
                return resultado;
            }
            resultado = a.getRank().compareTo(b.getRank());
            if (resultado != 0 ) {
                return resultado;
            }
            resultado = a.getRank().compareTo(b.getRank());
            if (resultado != 0 ) {
                return resultado;
            }
            return resultado;
        }
    });
    }
    
    /**
     * Ens permet descartar una carta de la baralla
     * @param pos 
     */
    public void drawCard(int pos){
        hand.remove(pos);
    }    
    
    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public int getBestRank() {
        return bestRank;
    }

    public void setBestRank(int bestRank) {
        this.bestRank = bestRank;
    }
    
}
