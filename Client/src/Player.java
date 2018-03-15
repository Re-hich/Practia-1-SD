
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**Class Player
 *
 * @author Carles
 */
public class Player {
    //Attributes
    private final int NUMCARDS = 1; 
    private int id;
    private int balance; //Actual balance/credits
    private ArrayList<Card> hand; //Hand
    private int lastBet; //Last bet of player
    private int numCardsSwap; //Number of cards when draw
    
    private ArrayList<String> availableCommands; //Available commands for player
    
    
    //Constructor
    /**Constructor
     * 
     */
    public Player(){
        this.id = 1221;
        this.balance = 0;
        this.hand = new ArrayList<Card>();
        
        this.availableCommands = new ArrayList<String>();
        

    }
    
    //Methods
    /** Prints player menu
     * 
     * @return String
     */
    public String printPlayerMenu(){
        String menu = "\n";
        menu = menu +"################################\n";
        menu = menu +"#         ~POKER GAME~\n";
        menu = menu +"#   Client: "+this.id+" | Cash: "+Integer.toString(this.balance)+"\n";
        menu = menu +"################################\n";
        menu = menu +"#   Hand: "+printPlayerHand()+"\n";
        menu = menu +"################################\n";
        menu = menu +"#   Commands: "+printAvailableCommands()+"\n";
        menu = menu +"Enter Command:";
        
        return menu;
    }
    /**Returns players hand cards
     * 
     * @return String
     */
    private String printPlayerHand(){
        String total = "";
        for(Card c:hand){
            total = total + c.toString()+" ";
        }
        return total;
    }
    /**Return Available Player commands
     * 
     * @return String 
     */
    private String printAvailableCommands(){
        String total = "{";
        for(String c:availableCommands){
            total = total + c+", ";
        }
        total = total+"}";
        return total;
    }
    /** Print menu to discard cards
     * 
     */
    public void printPlayerHandDiscard(){
        String menu = "\n";
        menu = menu +"################################\n";
        menu = menu +"#   Hand:\n";
        int i = 0;
        for(Card c:hand){
            menu = menu + "       -"+Integer.toString(i)+": "+c.toString()+"\n";
            i++;
        }
        menu = menu +"################################\n";
        menu = menu +"Enter index:";
        System.out.print(menu);
    }
    /**Adds card to player hand
     * 
     * @param c 
     */
    public void addCardToHand(Card c){
        if(this.hand.size() < NUMCARDS){
            this.hand.add(c);
        }
    }
    /**Set random ID to a player
     * 
     */
    public void setRandomID(){
        this.id = this.getRandomNumber(1000,90000);
    }
    /** Class that reset hand
     * 
     */
    public void clearHand(){
        this.hand.clear();
    }
    //Getters and setters
    /**Get total of cards
     * 
     * @return int
     */
    public int getNUMCARDS() {
        return NUMCARDS;
    }
    /** Returns player hand
     * 
     * @return ArrayList<Card>
     */
    public ArrayList<Card> getHand() {
        return hand;
    }
    /** Sets player hand
     * 
     * @param hand 
     */
    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    /** Get player ID
     * 
     * @return int
     */
    public int getId() {
        return id;
    }
    /** Get balance of player
     * 
     * @return int
     */
    public int getBalance() {
        return balance;
    }
    /** Set balance player
     * 
     * @param balance 
     */
    public void setBalance(int balance) {
        this.balance = balance;
    }
    /** Return lastBet
     * 
     * @return int 
     */
    public int getLastBet() {
        return lastBet;
    }
    /** Set lastBet
     * 
     * @param lastBet 
     */
    public void setLastBet(int lastBet) {
        this.lastBet = lastBet;
    }
    /**Get NumCards Swap
     * 
     * @return int
     */
    public int getNumCardsSwap() {
        return numCardsSwap;
    }
    /**Set num cards swap
     * 
     * @param numCardsSwap 
     */
    public void setNumCardsSwap(int numCardsSwap) {
        this.numCardsSwap = numCardsSwap;
    }
    /** get available commands
     * 
     * @return ArrayList
     */
    public ArrayList<String> getAvailableCommands() {
        return availableCommands;
    }
    /** Set Available commands
     * 
     * @param availableCommands 
     */
    public void setAvailableCommands(ArrayList<String> availableCommands) {
        this.availableCommands = availableCommands;
    }
    /**Remove all available commands
     * 
     */
    public void removeAllAvailableCommands(){
        this.availableCommands.clear();
    }
    /**Set new available commands
     * 
     * @param commands
     */
    public void setNewAvailableCommands(String[] commands){
        removeAllAvailableCommands();//Remove commands
        for(int j = 0; j < commands.length;j++){
            this.availableCommands.add(commands[j]);
        }
    }
    /**Return a random bet for IA
     * 
     * @return int
     */
    public int getRandomBet(){
        int min = 1;//Min bet is 10 chips
        int max = this.balance/3;
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }
    /**Return random number between minimum and maximum
     * 
     * @param minimum
     * @param maximum
     * @return int
     */
    public int getRandomNumber(int minimum, int maximum){
        int min = minimum;
        int max = maximum;
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }
    /**Sets player id
     * 
     * @param id 
     */
    public void setId(int id) {
        this.id = id;
    }
    
    
    
    
}
