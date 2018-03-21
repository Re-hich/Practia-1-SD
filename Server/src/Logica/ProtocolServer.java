package Logica;


import Utils.ComUtils;
import Utils.Errors;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Complements Protocol
 * @author Orlando i Hicham
 */
public class ProtocolServer {

    private final ComUtils comUtils;
    private boolean end = false;// per finalitzar el joc
    private int initialBet = 100; // aposta inicial per a jugar per defecte 100
    private int serverChips = 0; //diners del servidor
    private int clientChips = 0; // diners del client
    private int roundBet = 0;  /// aposta de la jugada
    private int lastBet; // ultima aposta que hem fet
    private String serverHand; // ma del servidor
    private String clientHand; // ma del client
    private int turn;  // turn
    private boolean drawRound = false; // controloem si ja hem fet la ronda descartar cartes
    private boolean firstBet = true; 
    private boolean start = true;
    private Errors errors = new Errors();

    /**
     * ProtocolFunctions constructor
     * @param comUtils 
     */
    public ProtocolServer(ComUtils comUtils){
        this.comUtils = comUtils;
        serverHand = "";
        clientHand = "";
        //si el turn es 0 nosaltres no tindrem que fer la priemera aposta
        if(turn == 0){
            firstBet = false;
        }
    }
    
    /**
     * Generates new turn 
     * @return 
     */
    public int generateDealer(){
        Integer[] dealerOptions = new Integer[]{0,1};
        Collections.shuffle(Arrays.asList(dealerOptions));//desordenem
        turn = dealerOptions[0];
        return turn;
    }
    
    /**
     * Funcio per tal d'assignar un dealer a la jugada
     * @return 
     */
    public int getTurn(){  
        return turn;
    }
    
    /**
     * This function allows us make a decision
     * @param options
     * @return 
     */
    public String generateDecision(String option){
        String decision = "";
        switch(option){
            case "BLNC":
                //El client envia BLNC
                String[] optionBlnc = new String[]{"BLNC"};
                decision = optionBlnc[0];        
                break;
            case "CALL":
                //Si el client fa un CALL, nosaltres fem un SHOW
                String[] optionCall = new String[]{"SHOW"};
                decision = optionCall[0];        
                break;
            case "SHOW":
                //Si el client fa un SHOW, nosaltres fem un show
                String[] optionPass = new String[]{"SHOW"};
                decision = optionPass[0];        
                break;
            case "CHCK":
                String[] optionChck = new String[]{"CHCK","BETT"};
                Collections.shuffle(Arrays.asList(optionChck));
                decision = optionChck[0];        
                break;
            case "BETT":
                //El client envia un BETT
                if (this.start) {
                    String[] optionBett = new String[]{"BETT"};
                    decision = optionBett[0];
                } else {
                    String[] optionBett = new String[]{"CALL","FOLD"};
                    Collections.shuffle(Arrays.asList(optionBett));
                    decision = optionBett[0];
                }
                break;
            case "RPLY":
                String[] optionRply = new String[]{"BLNC"};
                Collections.shuffle(Arrays.asList(optionRply));
                decision = optionRply[0];        
                break;

        }
        return decision;
    }
    
    /**
     * Server generates bet
     */
    public void serverBet(PrintWriter pw) throws IOException{
        try {
                comUtils.write_buffer(comUtils.string_to_buffer("BET_"));
                comUtils.write_SP();
                //Aposta que farem (server)
                int serverBet = (int) Math.floor(Math.random()*(10-100+1)+100);
                if(serverBet < getServerChips()){
                    comUtils.write_buffer(comUtils.int_to_buffer(serverBet));
                    setServerChips(getServerChips()-serverBet);
                    setRoundBet(getRoundBet() + serverBet);
                    setLastBet(serverBet);
                    pw.println("S: BET_ "+Integer.toString(serverBet));
                }
                else if(getServerChips() > 0 ){
                    serverBet = getServerChips();
                    comUtils.write_buffer(comUtils.int_to_buffer(serverBet));
                    setServerChips(getServerChips()-serverBet);
                    setRoundBet(getRoundBet() + serverBet);
                    setLastBet(serverBet);
                    pw.println("S: BET_ "+Integer.toString(serverBet));
                    
                }
                else{
                    //Falta enviar el error
                    errors.sendChipsError(comUtils);
                    pw.println("S: ERRO 09Not Chips");
                }

        } catch (IOException ex) {
            errors.sendDataError(comUtils);
        }
    }
    
    /**
     * Function to raise the client bet
     */
    public void serverRaise(PrintWriter pw) throws IOException{
        try{
            // la pujada del server
            int serverRaise = (int) Math.floor(Math.random()*(10-100+1)+100);
            //l'aposta del client
            int clientBet = comUtils.bytesToInt32(comUtils.read_bytes(4), "be");
            //li restem les fitxes que ha apostat al client
            setClientChips(getClientChips()-clientBet);
            
            if(serverRaise+clientBet < getServerChips()){
                //assignme lapujada al total de la ronda
                setRoundBet(getRoundBet()+serverRaise+clientBet+clientBet);
                //enviem la resposta al client
                comUtils.write_buffer(comUtils.string_to_buffer("RISE"));
                comUtils.write_SP();
                comUtils.write_buffer(comUtils.int_to_buffer(serverRaise));
                setLastBet(serverRaise);
                setServerChips(getServerChips()-(serverRaise+clientBet));
                pw.println("S: RISE "+serverRaise);
            }
            else if(getServerChips() > clientBet){
                //el servidor sube lo que pueda
                //assignme lapujada al total de la ronda
                setRoundBet(getRoundBet()+getServerChips()+clientBet+clientBet);
                //enviem la resposta al client
                comUtils.write_buffer(comUtils.string_to_buffer("RISE"));
                comUtils.write_SP();
                comUtils.write_buffer(comUtils.int_to_buffer(getServerChips()));
                setLastBet(getServerChips());
                setServerChips(0);
                pw.println("S: RISE "+getServerChips());
            } 
            else{
                errors.sendChipsError(comUtils);
                pw.println("S: ERRO 09Not Chips");
            }
            
        }catch (IOException ex) {
            errors.sendDataError(comUtils);
        }     
    }
    
    /**
     * Function to server draw
     */
    public void draw_server(int clientNum,PrintWriter pw) throws IOException{
        try{
            //actualment, eliminem de forma aleatoria cartes(la priemra), sense logica
            int draw_server = serverDRWS();//numero de cartes a eliminar aleatoriament
            for(int i = 0 ; i < draw_server;i++){
                serverHand.drawCard(0);
                Card serverC = deck.getCard();
                getServerHand().getHand().add(serverC);
            }            
            // li enviem la nostra resposta al client
            String drws = "S: DRWS";
            comUtils.write_buffer(comUtils.string_to_buffer("DRWS"));
            for(int j = 0; j < clientNum;j++){                                       
                // envio carta al cliente 
                comUtils.write_SP();
                Card c = getCard();
                getClientHand().getHand().add(c);
                comUtils.write_buffer(comUtils.string_to_buffer(c.getString()));
                drws = drws+" "+ c.getString();
            }               
    
            comUtils.write_SP();
            comUtils.write_buffer(comUtils.string_to_buffer(Integer.toString(draw_server)));
            pw.println(drws + " '"+ draw_server+"'");
        }catch (IOException ex) {
            errors.sendDataError(comUtils);
        }
        
    }
    
    /**
     * This function is used to asign chips when servr wins
     */
    public void serverWins(){
        // asignem les fitxes al servidor
        setServerChips(getServerChips()+getRoundBet());
    }
    
    /**
     * This function is used to asign chips when servr wins
     */
    public void clientWins(){
        // asignem les fitxes al servidor
        setClientChips(getClientChips ()+ getRoundBet());
    }
    
    /**
     * Sends STKS to client
     * @param pw 
     */
    public void gameChipsInformation(PrintWriter pw) throws IOException{
        try{    
            comUtils.write_buffer(comUtils.string_to_buffer("STKS"));
            comUtils.write_SP();
            comUtils.write_int32(getClientChips()); // podem utilitzar la que ens dona ja que la funcio permet pasar un int que son 4 bytes   
            comUtils.write_SP();
            comUtils.write_int32(getServerChips());  
            pw.println("S: STKS "+getClientChips()+" "+getServerChips());
        }catch (IOException ex) {
            errors.sendDataError(comUtils);
        }       
    }
    
    /**
     * Function to show server hand
     */
    public void showdown(PrintWriter pw) throws IOException{
        try {
            comUtils.write_buffer(comUtils.string_to_buffer("SHOW"));
            String show = "S: SHOW";
            //bucle per recorrer tota la ma del servidor i anar mostrant les cartes
            for(int i = 0; i < serverHand.getHand().size(); i++){
                comUtils.write_SP();
                comUtils.write_buffer(comUtils.string_to_buffer(serverHand.getHand().get(i).getString()));
                show = show + " " +serverHand.getHand().get(i).getString();     
            }
            pw.println(show);
             //Després de mostrar les nostres cartes(server), mirem a veure qui guanya
            whoWins();           
        } catch (IOException ex) {
            errors.sendDataError(comUtils);
        }
    }
    
    /**
     * Function to decided who wins
     */
    public void whoWins(){
        int clientSol = clientHand.solHand();
        int serverSol = serverHand.solHand();
        
        if(clientSol < serverSol){
            clientWins();
        }else if(clientSol > serverSol){
            serverWins();
        }
        else{
            int cR = clientHand.getBestRank();
            int sR = serverHand.getBestRank();
            if(cR > sR){
                clientWins();
            }else{
                serverWins();
            }
        }
    }
    
    /**
     * Control the client option (NOT used)
     * @param options
     * @param command
     * @return 
     */
    public boolean controlOption(String options[],String command){
        for(int i = 0; i < options.length; i++){
            if(command == options[i]){
                return true;
            }
        }       
        return false;
    }
    
    /**
     *  
     */
    public void responseError(PrintWriter pw) throws IOException{
        comUtils.read_bytes(1);  
        int lenghtMessageError = Integer.parseInt(new String(comUtils.read_bytes(2)));
        String message = new String(comUtils.read_bytes(lenghtMessageError));
        pw.println("C: ERRO "+lenghtMessageError+message);
    }
    
    
    /**
     * nº server draw
     * @return 
     */
    public int serverDRWS(){               
        int numD = (int) Math.floor(Math.random()*(1-5+1)+5);
        return numD;
    }
    
    /**
     * Bet round
     * @param roundBet 
     */
    public void setRoundBet(int roundBet) {
        this.roundBet = roundBet;
    }
    /**
     * Game status
     * @return 
     */
    public boolean isEnd() {
        return end;
    }
    /**
     * Set status
    */
    public void setEnd(boolean end) {
        this.end = end;
    }
    
    
    
    /**
     * get ANTE
     * @return 
     */
    public int getInitialBet() {
        return initialBet;
    }
    /**
     * Sets ANTE
     * @param initialBet 
     */
    public void setInitialBet(int initialBet) {
        this.initialBet = initialBet;
    }
    
    /**
     * get Start game
     * @return 
     */
    public boolean isStart() {
        return start;
    }
    
    /**
     * set start game
     * @param start 
     */
    public void setStart(boolean start) {
        this.start = start;
    }
    
    /**
     * get Server Chips
     * @return 
     */
    public int getServerChips() {
        return serverChips;
    }
    /**
     * Set Server Chips
     * @param serverChips 
     */
    public void setServerChips(int serverChips) {
        this.serverChips = serverChips;
    }
    /**
     * Get Client Chips
     * @return 
     */
    public int getClientChips() {
        return clientChips;
    }
    /**
     * Set Client Chips
     * @param clientChips 
     */
    public void setClientChips(int clientChips) {
        this.clientChips = clientChips;
    }
    /**
     * Get round bet
     * @return 
     */
    public int getRoundBet() {
        return roundBet;
    }
    /**
     * Get last bet
     * @return 
     */
    public int getLastBet() {
        return lastBet;
    }
    /**
     * Set last bet
     * @param lastBet 
     */
    public void setLastBet(int lastBet) {
        this.lastBet = lastBet;
    }
    
    /**
     * Returns if pass round draw
     * @return 
     */
    public boolean isDrawRound() {
        return drawRound;
    }

    /**
     * Set status draw round
     * @param drawRound 
     */
    public void setDrawRound(boolean drawRound) {
        this.drawRound = drawRound;
    }

    /**
     * Returns deck
     * @return 
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Set new deck
     * @param deck 
     */
    public void setDeck(Deck deck) {
        this.deck = deck;
    }
    
    /**
     * Returns Server hand
     * @return 
     */
    public Hand getServerHand() {
        return serverHand;
    }

    /**
     * Set Server hand
     * @param serverHand 
     */
    public void setServerHand(Hand serverHand) {
        this.serverHand = serverHand;
    }

    /**
     * Returns Client hand
     * @return 
     */
    public Hand getClientHand() {
        return clientHand;
    }

    /**
     * Set Client hand
     * @param clientHand 
     */
    public void setClientHand(Hand clientHand) {
        this.clientHand = clientHand;
    }

    /**
     * Returns if now is first bet
     * @return 
     */
    public boolean isFirstBet() {
        return firstBet;
    }

    /**
     * Set status first bet
     * @param firstBet 
     */
    public void setFirstBet(boolean firstBet) {
        this.firstBet = firstBet;
    }
}
