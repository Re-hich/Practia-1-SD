
/** This class manages all parameters of the server
 *
 * @author Carles
 */
public class DataServer {
    //Variables of actual game
    private final int NUMCARDS = 5; 
    private int balance;
    private int minimumBet;//Minimum chips to bet
    private String deal;//1,server, 0 client
    private int lastBet;//Last bet of the server
    private int lastNumSwap; //Last number of cards swapped server
    private boolean hasDraw; //All discarted
    
    //Constructor
    /**Constructor
     * 
     */
    public DataServer(){
        this.minimumBet = 0;
        this.deal = "1";
        this.lastBet = 0;
    }

    

    //Getters and Setters
    /**Get total of cards
     * 
     * @return int
     */
    public int getNUMCARDS() {
        return NUMCARDS;
    }
    /** Get server balance
     * 
     * @return 
     */
    public int getBalance() {
        return balance;
    }
    /**Set server balance
     * 
     * @param balance 
     */
    public void setBalance(int balance) {    
        this.balance = balance;
    }

    /** Get minimum bet amount
     * 
     * @return int
     */
    public int getMinimumBet() {
        return minimumBet;
    }
    /** Set minimum bet amount
     * 
     * @param minimumBet 
     */
    public void setMinimumBet(int minimumBet) {
        this.minimumBet = minimumBet;
    }
    /** Get priority to bet
     * 
     * @return String
     */
    public String getDeal() {
        return deal;
    }
    /** Set priority to bet
     * 
     * @param priorityBet 
     */
    public void setDeal(String priorityBet) {
        this.deal = priorityBet;
    }
    /** Get last bet of server
     * 
     * @return int
     */
    public int getLastBet() {
        return lastBet;
    }
    /** Set last bet of server
     * 
     * @param lastBet 
     */
    public void setLastBet(int lastBet) {
        this.lastBet = lastBet;
    }
    /**Get last number of cards swapped of server
     * 
     * @return int
     */
    public int getLastNumSwap() {
        return lastNumSwap;
    }
    /**Set last number of cards swapped of server
     * 
     * @param lastNumSwap 
     */
    public void setLastNumSwap(int lastNumSwap) {
        this.lastNumSwap = lastNumSwap;
    }
        /** Get has Draw
     * 
     * @return boolean
     */
    public boolean isHasDraw() {
        return hasDraw;
    }
    /**Set has draw
     * 
     * @param hasDraw 
     */
    public void setHasDraw(boolean hasDraw) {
        this.hasDraw = hasDraw;
    }
}
