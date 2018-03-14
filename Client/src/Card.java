/** Class Card
 * 
 * @author Carles
 */
public class Card {
    //Attributes
    private String type;
    private String stick;
    
    //Constructor
    /**Construcor
     * 
     * @param type
     * @param stick 
     */
    public Card(String type, String stick){
        this.type = type;
        this.stick = stick;
    }

    //Methods
    /** Prints card
     * 
     * @return String 
     */
    public String toString(){
        return (type+stick);
    }
}
