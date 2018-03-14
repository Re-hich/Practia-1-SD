package Model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author adri
 */
public class Card{

    private String rank;
    private String suit;
    private String card;  
    /**
     * Constructor
     * @param rank
     * @param suit 
     */
    public Card(String rank, String suit){
        this.rank = rank;
        this.suit = suit;
        card = rank+suit;
    }
    /**
     * Compare to
     * @param o
     * @return 
     */
     public int compareTo(Card o) {
        String a=new String(String.valueOf(this.getRank()));
        String b=new String(String.valueOf(o.getRank()));
        return a.compareTo(b);
    }
    
    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public String getString() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }    
    
}
