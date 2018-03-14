import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**Class ClientIAProtocol
 *
 * @author Carles
 */
public class ClientIAProtocol {  
    //Attributes
    private ComUtils comUtils;
    private Player player;
    private DataServer dataServer;
    private Errors errors;
    private ProtocolFunctions protFunc;
    
    private Scanner sc;
    private boolean end = false;
    //private boolean isFromFold = false;

    
    //Constructor
    /**Constructor 
     * 
     * @param socket
     * @throws IOException 
     */
    public ClientIAProtocol(Socket socket) throws IOException{
        try{
            comUtils = new ComUtils(socket); //Open I/O connection with server;
            player = new Player();
            player.setRandomID();
            String commands[] = {"START"};
            player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commands)));
            dataServer = new DataServer();
            errors = new Errors();
            protFunc = new ProtocolFunctions();
            sc = new Scanner(System.in);
        }
        catch (IOException ex) {
            //Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex);
            errors.sendSocketError(comUtils);
            end = true;
        }
    }
    
    //Methods
    /** Do the protocol
     * 
     */
    public void doProtocol(){
        printPlayerMenu();//Function to print menu of player
        String command;//Input command
        ArrayList<String> commandsAvailable = player.getAvailableCommands();
        Collections.shuffle(commandsAvailable);
        command = commandsAvailable.get(0);
        System.out.println(command);
        commandManager(command);//Call method with command

    }
    
    
    /** Do function with selected command
     * 
     * @param command
     */
    private void commandManager(String command){

        switch(command){
            /*START
            This command is used for starting a new game with the server. 
            An identifier is needed. After this message an ANTE and a STAKES 
            messages from the server are expected*/
            case "START":
                try {
                    //Send start
                    protFunc.clientStrt(comUtils, player);
                    //Read the server response
                    serverResponseManagement();//read ANTE
                    serverResponseManagement();//read STKS
                } catch (IOException ex) {
                    try {
                        //Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex);
                        errors.sendSocketError(comUtils);
                        end = true;
                    } catch (IOException ex1) {
                        Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }

                break;
            /*ANTE_OK
            This command is used for accepting the ANTE message coming from 
            the server. After this command the client expects a DEALER and a 
            HAND message.*/
            case "ANTE_OK":
            try {
                //Send anok
                protFunc.clientAnok(comUtils);
                player.setBalance(player.getBalance()-dataServer.getMinimumBet());//Substract credits of initial bet

                //Read the server response
                serverResponseManagement();//Read DEAL
                if(!end){
                    serverResponseManagement();//Read HAND
                    //Read bet of server
                    if(dataServer.getDeal().equals("1")){
                        serverResponseManagement();
                    }
                }

                
            } catch (IOException ex) {
                try {
                        //Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex);
                        errors.sendSocketError(comUtils);
                        end = true;
                    } catch (IOException ex1) {
                        Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex1);
                    }
            }

                break;
            /*QUIT
            This command is used for quitting the game instead of ante. After 
            command the communication with the server is closed.*/
            case "QUIT":
                end = true;
                break;
            /*BET
            This command is used for betting a new amount of money. After this
            command a CALL or FOLD or RAISE message is expected.*/
            case "BET":
                try {
                    //Client bets
                    int chips = player.getRandomBet();//Random bet
                    System.out.println(Integer.toString(chips));
                    if(player.getBalance()>= chips){
                        //Set last bet to player
                        player.setLastBet(chips);
                        //Substract chips from player balance
                        player.setBalance(player.getBalance()-chips);
                        //Send BET to server
                        protFunc.clientBet(comUtils,chips);
                        //Read the server response (CALL or FOLD or RAISE)
                        serverResponseManagement(); 
                    }
                    else{
                        errors.sendDataError(comUtils);
                        end = true;
                        //System.err.println("Error: Player has not enough money");
                    }
                } catch (IOException ex) {
                    try {
                            //Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex);
                            errors.sendSocketError(comUtils);
                            end = true;
                        } catch (IOException ex1) {
                            Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                }  

                break;
            /*PASS
            This command is used for letting the other player to bet or not. 
            After this command a BET or a PASS command is expected.*/
            case "PASS":
                try {
                    protFunc.clientPass(comUtils);//Send PASS to the server
                    if(dataServer.getDeal().equals("0")){
                        //Read the server response (BET or PASS)
                        serverResponseManagement();
                    }
                    else{
                        if(dataServer.isHasDraw()){
                            //Read the server response (SHOWDOWN)
                            serverResponseManagement();
                        }
                        else{
                            String commandsPass[] = {"DRAW"};
                            player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsPass)));
                        } 
                    }
                } catch (IOException ex) {
                    try {
                            //Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex);
                            errors.sendSocketError(comUtils);
                            end = true;
                        } catch (IOException ex1) {
                            Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                }
                break;
            /*CALL
            This command is used when the other player has betted an you want
            equals his bet. After this message the betting phase is over, and
            depending on the phase a DRAW message or a SHOWNDOWN or a STAKES 
            message is expected. If you don't have enough stakes for equals the 
            bet, then the bet is set to the maximum amount of money that the 
            player has.*/
            case "CALL":
                try {
                    //Client call
                    int chips2 = dataServer.getLastBet(); //Read chips
                    if(player.getBalance()>= chips2){ //If player has enough money:
                        //Set last bet to player
                        player.setLastBet(chips2);
                        //Substract chips from player balance
                        player.setBalance(player.getBalance()-chips2);
                        //Sends call
                        protFunc.clientCall(comUtils);

                    }
                    else{//Player doen't have enough money Do ALL-IN
                        chips2 = player.getBalance();
                        //Set last bet to player
                        player.setLastBet(chips2);
                        //Substract chips from player balance
                        player.setBalance(player.getBalance()-chips2);
                        //Sends call
                        protFunc.clientCall(comUtils);
                    
                    }
                    if(dataServer.isHasDraw()){
                            //System.out.println("Server has priority");
                            //Read the server response (DRAW or SHOWDOWN or STAKES)
                            serverResponseManagement();
                        }
                    else{
                        String commandsCall[] = {"DRAW"};
                        player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsCall)));
                    }
                    
                } catch (IOException ex) {
                    try {
                            //Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex);
                            errors.sendSocketError(comUtils);
                            end = true;
                        } catch (IOException ex1) {
                            Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                }    

                break;
            /*FOLD
            This command is used when the other player has betted an you don't 
            want to follow him. In this case your round is over and a STAKES 
            message is expected.*/
            case "FOLD":
                try {
                    //Send FOLD to the server
                    protFunc.clientFold(comUtils);

                    //Read the server response (STAKES)
                    serverResponseManagement();
                    //isFromFold = true;
                } catch (IOException ex) {
                    try {
                            //Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex);
                            errors.sendSocketError(comUtils);
                            end = true;
                        } catch (IOException ex1) {
                            Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                }  

                break;
            /*RAISE
            This command is used when the other player has betted an you 
            want to follow him and you want raise with a new amount of money. 
            After this command a CALL or RISE or FOLD message is expected.*/
            case "RAISE":
                try {
                    //Client raise
                    if(player.getBalance()>0){
                        int chips3 = player.getRandomBet();
                        int totalBet = dataServer.getLastBet()+chips3;
                        if(player.getBalance()>= totalBet){
                            //Set last bet to player
                            player.setLastBet(totalBet);
                            //Substract totalBet from player balance
                            player.setBalance(player.getBalance()-totalBet);
                            //Sends info to server
                            protFunc.clientRise(comUtils,chips3);

                            //Read the server response (CALL or FOLD or RAISE)
                            serverResponseManagement();

                        }
                    }
                    else{
                        errors.sendDataError(comUtils);
                        end = true;
                        //System.err.println("Error: Player has not enough money");
                    }
                } catch (IOException ex) {
                    try {
                            //Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex);
                            errors.sendSocketError(comUtils);
                            end = true;
                        } catch (IOException ex1) {
                            Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                }

                break;
            /*DRAW	
            This command is used for exchanging cards in your hand for new ones.
            If none card is desired, an DRAW message with a 0 is sent.*/
            case "DRAW":
                try {
                    //Sends random DRAW
                    protFunc.clientRandomDraw(comUtils, player, sc);
                    //Read DRWS
                    //System.out.println("Estoy esperando DRWS");
                    serverResponseManagement();
                } catch (IOException ex) {
                    try {
                            //Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex);
                            errors.sendSocketError(comUtils);
                            end = true;
                        } catch (IOException ex1) {
                            Logger.getLogger(ClientProtocol.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                }

                break;
            default:
                System.out.println("Use: <command>");
                break;
        }
    }
    
    
    /** Manage server response
     * Read first 4 Bytes, then read the other Bytes by the 4 Bytes(command) received.
     */
    private void serverResponseManagement() throws IOException{
        byte commandBytes[];
        
        commandBytes = comUtils.read_bytes(4); //Read 4 Bytes for the command
        String command = new String(commandBytes);
        command = command.toUpperCase();


        switch(command){
            case "ANTE"://Read minimumbet of server
                protFunc.responseAnte(comUtils, dataServer);
                
                String commandsAnte[] = {"ANTE_OK"};
                player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsAnte)));
                break;
            case "STKS": //Read actual stack
                protFunc.responseStks(comUtils, player, dataServer);
                player.clearHand();
                dataServer.setHasDraw(false);
                
                String commandsStks[] = {"ANTE_OK"};
                player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsStks)));
                
                break;
            case "DEAL": //Read the deal from server
                protFunc.responseDeal(comUtils, dataServer);
                
                if(dataServer.getDeal().equals("0")){ //Deal 0
                    String commandsDeal[] = {"BET","PASS"};
                    player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsDeal)));
                }
                else{ //Deal 1
                    String commandsDeal[] = {"CALL","RAISE","PASS","FOLD"};
                    player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsDeal)));
                }
                break;
            case "HAND": //Read hand received from server
                protFunc.responseHand(comUtils, player);
                break;
            case "BET_": //Read bet from server
                protFunc.responseBet(comUtils, dataServer);
                

                String commandsBet[] = {"CALL","RAISE","FOLD"};
                player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsBet)));

                break;
            case "PASS": //Read pass from server
                System.out.println("Server PASS.");
                
                if(!dataServer.isHasDraw()){
                    if(dataServer.getDeal().equals("0")){ //Deal 0 //ya habriamos apostado por tanto descartamos o nos vamos
                        String commandsPass[] = {"DRAW"};
                        player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsPass)));
                    }
                    else{//Deal 1
                        String commandsPass[] = {"BET","PASS","FOLD"};
                        player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsPass)));
                    }
                }
                else{ //Ya hemos robado
                    if(dataServer.getDeal().equals("1")){
                        String commandsPass[] = {"BET","PASS","FOLD"};
                        player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsPass)));  
                    }else{//Esperamos el showdown
                        serverResponseManagement();
                    }
                    
                }
                break;
            case "CALL": //Read a call from server  
                protFunc.responseCall(player, dataServer);
                
                if(!dataServer.isHasDraw()){
                    String commandsCall[] = {"DRAW"};
                    player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsCall)));
                
                }
                else{//Esperamos el showdown
                    serverResponseManagement();
                }
                
                break;
            case "RISE": //Read a raise from server  
                protFunc.responseRise(comUtils, player, dataServer);
                
                String commandsRise[] = {"CALL","RAISE"};
                player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsRise)));
                break;
            case "FOLD": //Read a fold from server                  
                System.out.println("Server FOLD");
                serverResponseManagement(); //Read STKS
                //isFromFold = true;
  
                break;
            case "DRWS":
                protFunc.responseDrws(comUtils,player,dataServer);
                
                dataServer.setHasDraw(true);
                
                if(dataServer.getDeal().equals("0")){ //Deal 0 //ya habriamos apostado por tanto descartamos o nos vamos
                    String commandsDrws[] = {"BET","PASS","FOLD"};
                player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsDrws)));
                }
                else{//Deal 1
                    String commandsDrws[] = {"CALL","RAISE","FOLD"};
                    player.setAvailableCommands(new ArrayList<String>(Arrays.asList(commandsDrws)));
                    serverResponseManagement();//Read BET,PASS,FOLD
                }
                
                
                break;
            case "SHOW":
                protFunc.responseShow(comUtils,dataServer);
                serverResponseManagement(); //Read STKS
                
                break;
                
            case "ERRO":
                protFunc.responseError(comUtils);
                end = true;
                break;
        }
        
    }
    /**Prints player menu
     * 
     */
    void printPlayerMenu() {
        System.out.print(player.printPlayerMenu());
    }

    /**
     * 
     * @return boolean 
     */
    public boolean isEnd() {
        return end;
    }


}
