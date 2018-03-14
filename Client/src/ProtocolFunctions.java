
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/** Class with all data send protocol.
 *
 * @author Carles
 */
public class ProtocolFunctions {
    
    /** Sends client START
     * 
     * @param comUtils
     * @param player
     * @throws IOException 
     */
    void clientStrt(ComUtils comUtils, Player player) throws IOException{
        comUtils.write_buffer(comUtils.string_to_buffer("STRT"));
        comUtils.write_buffer(comUtils.string_to_buffer(" "));
        comUtils.write_buffer(comUtils.int_to_buffer(player.getId()));
    }
    /** Sends client ANOK
     * 
     * @param comUtils
     * @throws IOException 
     */
    void clientAnok(ComUtils comUtils) throws IOException{
        comUtils.write_buffer(comUtils.string_to_buffer("ANOK"));//Send ANOK to the server
    }
    /** Sends client BET
     * 
     * @param comUtils
     * @param chips
     * @throws IOException 
     */
    void clientBet(ComUtils comUtils, int chips) throws IOException{
        
        //Sends info to server
        comUtils.write_buffer(comUtils.string_to_buffer("BET_"));
        comUtils.write_buffer(comUtils.string_to_buffer(" "));
        comUtils.write_buffer(comUtils.int_to_buffer(chips));
    }
    /** Sends client PASS
     * 
     * @param comUtils
     * @throws IOException 
     */
    void clientPass(ComUtils comUtils) throws IOException{
        comUtils.write_buffer(comUtils.string_to_buffer("PASS"));//Send PASS to the server
    }
    /** Sends client CALL
     * 
     * @param comUtils
     * @throws IOException 
     */
    void clientCall(ComUtils comUtils) throws IOException{
        //Sends info to server
        comUtils.write_buffer(comUtils.string_to_buffer("CALL"));//Send CALL to the server
    }
    /** Sends client FOLD
     * 
     * @param comUtils
     * @throws IOException 
     */
    void clientFold(ComUtils comUtils) throws IOException{
        comUtils.write_buffer(comUtils.string_to_buffer("FOLD"));//Send FOLD to the server
    }
    /**Sends client RISE
     * 
     * @param comUtils
     * @param chips3
     * @throws IOException 
     */
    void clientRise(ComUtils comUtils, int chips3) throws IOException {
        //Sends info to server
        comUtils.write_buffer(comUtils.string_to_buffer("RISE"));
        comUtils.write_buffer(comUtils.string_to_buffer(" "));
        comUtils.write_buffer(comUtils.int_to_buffer(chips3));
    }
    /** Sends client DRAW
     * 
     * @param comUtils
     * @param player
     * @param sc
     * @throws IOException 
     */
    void clientDraw(ComUtils comUtils,Player player,Scanner sc) throws IOException{
        int numCards;
        System.out.print("Number to exchange:");
        numCards = getScannerInt(sc);
        if(numCards == 0 ){
            comUtils.write_buffer(comUtils.string_to_buffer("DRAW"));
            comUtils.write_buffer(comUtils.string_to_buffer(" "));
            comUtils.write_buffer(comUtils.string_to_buffer("0"));
        }
        else if(numCards <= 5 && numCards >0){
            //Seleccionar las numCards de la mano i enviarlas al server.
            //Select player hand cards
            ArrayList<Integer> arrayIndex = new ArrayList<Integer>();
            for(int i=0; i< numCards; i++){
                player.printPlayerHandDiscard();//Print player hand
                int index = getScannerInt(sc);

                if(index < 5 && index >=0 && !arrayIndex.contains(index)){
                    arrayIndex.add(index);
                }
                else{
                    System.err.println("Error: Invalid index");
                }
            }
            //Get length of arraylist
            int sizeIndexs = arrayIndex.size();//Get num Cards to exchange
            //System.out.println(Integer.toString(sizeIndexs));
            player.setNumCardsSwap(sizeIndexs);
            comUtils.write_buffer(comUtils.string_to_buffer("DRAW"));
            comUtils.write_buffer(comUtils.string_to_buffer(" "));
            comUtils.write_buffer(comUtils.string_to_buffer(Integer.toString(sizeIndexs)));

            ArrayList<Card> discartCards = new ArrayList<Card>();
            for(int i = 0; i<sizeIndexs; i++){
                comUtils.write_buffer(comUtils.string_to_buffer(" "));//Put space
                //System.out.println(Integer.toString(arrayIndex.get(i)));
                Card card = player.getHand().get(arrayIndex.get(i));//Get card
                discartCards.add(card);
                //System.out.println(card.toString());
                comUtils.write_buffer(comUtils.string_to_buffer(card.toString()));
            }
            //Remove cards
            for(int i = 0; i<sizeIndexs; i++){
                player.getHand().remove(discartCards.get(i));
            } 
        }
    }
    /** Sends client random DRAW
     * 
     * @param comUtils
     * @param player
     * @param sc
     * @throws IOException 
     */
    void clientRandomDraw(ComUtils comUtils,Player player,Scanner sc) throws IOException{
        int numCards;
        numCards = player.getRandomNumber(1,5);
        if(numCards == 0 ){
            comUtils.write_buffer(comUtils.string_to_buffer("DRAW"));
            comUtils.write_buffer(comUtils.string_to_buffer(" "));
            comUtils.write_buffer(comUtils.string_to_buffer("0"));
        }
        else if(numCards <= 5 && numCards >0){
            //Seleccionar las numCards de la mano i enviarlas al server.
            //Select player hand cards
            ArrayList<Integer> arrayIndex = new ArrayList<Integer>();
            for(int i=0; i< numCards; i++){
                
                int index = player.getRandomNumber(0,5);

                if(index < 5 && index >=0 && !arrayIndex.contains(index)){
                    arrayIndex.add(index);
                }
                else{
                    System.err.println("Error: Invalid index");
                }
            }
            //Get length of arraylist
            int sizeIndexs = arrayIndex.size();//Get num Cards to exchange
            //System.out.println(Integer.toString(sizeIndexs));
            player.setNumCardsSwap(sizeIndexs);
            comUtils.write_buffer(comUtils.string_to_buffer("DRAW"));
            comUtils.write_buffer(comUtils.string_to_buffer(" "));
            comUtils.write_buffer(comUtils.string_to_buffer(Integer.toString(sizeIndexs)));

            ArrayList<Card> discartCards = new ArrayList<Card>();
            for(int i = 0; i<sizeIndexs; i++){
                comUtils.write_buffer(comUtils.string_to_buffer(" "));//Put space
                //System.out.println(Integer.toString(arrayIndex.get(i)));
                Card card = player.getHand().get(arrayIndex.get(i));//Get card
                discartCards.add(card);
                //System.out.println(card.toString());
                comUtils.write_buffer(comUtils.string_to_buffer(card.toString()));
            }
            //Remove cards
            for(int i = 0; i<sizeIndexs; i++){
                player.getHand().remove(discartCards.get(i));
            } 
        }
    }
    
    
    
    
    
    
    /**Read response ANTE
     * 
     * @param comUtils
     * @param dataServer
     * @throws IOException 
     */
    void responseAnte(ComUtils comUtils, DataServer dataServer) throws IOException{
        comUtils.read_bytes(1);//Read space character
        dataServer.setMinimumBet(comUtils.bytesToInt32(comUtils.read_bytes(4), "be"));//Read bet
        System.out.println("Accept bet amount of "+Integer.toString(dataServer.getMinimumBet())+"?");
    }
    /**Read response STKS
     * 
     * @param comUtils
     * @param player
     * @param dataServer
     * @throws IOException 
     */
    void responseStks(ComUtils comUtils, Player player, DataServer dataServer) throws IOException{
        comUtils.read_bytes(1);//Read space character

        player.setBalance(comUtils.read_int32());//Read player initial money
        comUtils.read_bytes(1);//Read space character

        dataServer.setBalance(comUtils.read_int32());//Server money
        System.out.println("Stakes:\nClient: "+player.getBalance()+" | Server: "+dataServer.getBalance());
    }
    /** Read response DEAL
     * 
     * @param comUtils
     * @param dataServer
     * @throws IOException 
     */
    void responseDeal(ComUtils comUtils, DataServer dataServer) throws IOException {
        comUtils.read_bytes(1);//Read space character

        dataServer.setDeal(new String(comUtils.read_bytes(1)));//Read priority of gameplay

        if(dataServer.getDeal().equals("1")){
            System.out.println("Deal: 1");
        }
        else{
            System.out.println("Deal: 0");
        }
    }
    /** Read response HAND
     * 
     * @param comUtils
     * @param player
     * @throws IOException 
     */
    void responseHand(ComUtils comUtils, Player player) throws IOException {
        //Read 5 cards
        for(int i = 0;i<player.getNUMCARDS();i++){
            comUtils.read_bytes(1);//Read space character

            String rank = new String(comUtils.read_bytes(1));//Read first character of card
            String type;
            if(rank.equals("1")){ //This means that the card is 10X
                String rankNext = new String(comUtils.read_bytes(1));//Read second character that will be 0
                type = new String(comUtils.read_bytes(1));//Read the type of the card

                String totalType = rank+rankNext; //Join 1 and 0
                if(totalType.equals("10")){
                    //Create card and add to player hand
                    Card card = new Card(totalType,type);
                    //System.out.println(card);
                    player.addCardToHand(card);
                }
            }
            else{
                type = new String(comUtils.read_bytes(1));
                Card card = new Card(rank,type);
                //System.out.println(card);
                player.addCardToHand(card);
            }
        }
    }
    /**Read response BET
     * 
     * @param comUtils
     * @param dataServer
     * @throws IOException 
     */
    void responseBet(ComUtils comUtils, DataServer dataServer) throws IOException {
        comUtils.read_bytes(1);//Read space character
                    
        dataServer.setLastBet(comUtils.bytesToInt32(comUtils.read_bytes(4), "be"));//Read bet of server

        System.out.println("Server betted "+Integer.toString(dataServer.getLastBet())+" chips.");
    }
    /**Gest CALL
     * 
     * @param player
     * @param dataServer 
     */
    void responseCall(Player player,DataServer dataServer) {
        dataServer.setLastBet(player.getLastBet());//Read bet of server    
        System.out.println("Server called "+Integer.toString(dataServer.getLastBet())+" chips.");
    }
    /** Read response RISE
     * 
     * @param comUtils
     * @param dataServer
     * @throws IOException 
     */
    void responseRise(ComUtils comUtils, Player player, DataServer dataServer) throws IOException {
        comUtils.read_bytes(1);//Read space character
                    
        dataServer.setLastBet(comUtils.bytesToInt32(comUtils.read_bytes(4), "be")+player.getLastBet());//Read bet of server

        System.out.println("Server rised "+Integer.toString(dataServer.getLastBet())+" chips.");
    }
    /** Read response DRWS
     * 
     * @param comUtils
     * @param player
     * @param dataServer
     * @throws IOException 
     */
    void responseDrws(ComUtils comUtils, Player player, DataServer dataServer) throws IOException {
        int numCards = player.getNumCardsSwap();
        for(int i = 0; i<numCards;i++){
            comUtils.read_bytes(1);//Read space character
            String rank = new String(comUtils.read_bytes(1));//Read first character of card
            String type;
            if(rank.equals("1")){ //This means that the card is 10X
                String rankNext = new String(comUtils.read_bytes(1));//Read second character that will be 0
                type = new String(comUtils.read_bytes(1));//Read the type of the card

                String totalType = rank+rankNext;
                if(totalType.equals("10")){
                    //Create card and add to player hand
                    player.addCardToHand(new Card(totalType,type));
                }
            }
            else{
                type = new String(comUtils.read_bytes(1));
                player.addCardToHand(new Card(rank,type));
            }
        }
        //Read numCards swapped server
        comUtils.read_bytes(1);//Read space character
        dataServer.setLastNumSwap(Integer.parseInt(new String(comUtils.read_bytes(1))));//Read server numCards swapped
        dataServer.setHasDraw(true);
        System.out.println("Server discard "+Integer.toString(dataServer.getLastNumSwap())+" cards.");
    }
    /**Response Show
     * 
     * @param comUtils
     * @param dataServer 
     */
    void responseShow(ComUtils comUtils, DataServer dataServer) throws IOException {
        //Read 5 server cards
        String total = "";
        for(int i = 0;i<dataServer.getNUMCARDS();i++){
            comUtils.read_bytes(1);//Read space character

            String rank = new String(comUtils.read_bytes(1));//Read first character of card
            String type;
            if(rank.equals("1")){ //This means that the card is 10X
                String rankNext = new String(comUtils.read_bytes(1));//Read second character that will be 0
                type = new String(comUtils.read_bytes(1));//Read the type of the card

                String totalType = rank+rankNext;
                if(totalType.equals("10")){
                    //Create card and add to player hand
                    total = total + " "+new Card(totalType,type).toString();
                }
            }
            else{
                type = new String(comUtils.read_bytes(1));
                total = total + " "+ new Card(rank,type).toString();
            }
        }
        System.out.println("Server Hand: "+total);
    }
    /** Response error from server
     * 
     * @param comUtils
     * @throws IOException 
     */
    void responseError(ComUtils comUtils) throws IOException {
        comUtils.read_bytes(1);//Read space character
        
        int lenghtMessageError = Integer.parseInt(new String(comUtils.read_bytes(2)));//Read lenght of the message error
        String message = new String(comUtils.read_bytes(lenghtMessageError)); //Read error message
        
        System.out.println(message);
    }
    /**Get input number from Scanner
     * 
     * @param sc
     * @return int
     */
    int getScannerInt(Scanner sc) {

        while (!sc.hasNextInt()) {
            System.out.println("Enter int, please!");
            sc.nextLine();
        }
        int value = sc.nextInt();
        return value;
    }

}
