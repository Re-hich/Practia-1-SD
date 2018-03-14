package Model;


import java.util.ArrayList;
import java.util.Collections;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author adri
 */
public class Deck {
    private ArrayList<Card> deck = new ArrayList<Card>();
    /**
     * Constructor
     */
    public Deck(){
        //Crear lescartes de la baralla
        String nums[] = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        String suits[] = {"D","H","S","C"};
        for(int i = 0; i < suits.length;i++){
            for(int j = 0; j < nums.length;j++){
                Card c = new Card(nums[j],suits[i]);
                deck.add(c);
            }
        
        }
        // Barallem el deck 
        Collections.shuffle(deck);
    }
    /**
     * Function to get a card of deck
     * @return 
     */
    public Card getCard(){
        //Agafem una carta aleatoria de la baralla
        //int cardPos = (int) Math.floor(Math.random()*(1-deck.size()+1)+deck.size());
        Card c = deck.remove(0);
        //deck.remove(cardPos);
        return c; // fem un get card per tal d'obtenir l'string de la carta
    }
    
    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }
    
}
