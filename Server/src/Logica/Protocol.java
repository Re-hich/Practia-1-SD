package Logica;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author adri
 */
import Utils.ComUtils;
import Utils.Errors;
import Model.Card;
import java.io.*;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

public class Protocol {
    ComUtils comUtils;
    ProtocolServer game;//creem un objecte procotolfunctions on hi han les opcions que utilitzara el protocol
    File file;
    Errors errors;
    PrintWriter pw=null;
    int id;
    String options[] = {"ANOK","QUIT"};; 
    Hashtable<Integer, Integer> usuaris;
    
    /**
     * Constructor
     * @param id
     * @param comUtils
     * @param file
     * @param usuaris 
     */
    Protocol(int id, ComUtils comUtils,File file, Hashtable<Integer, Integer> usuaris) {
        System.out.println("Connexió realitzada amb "+id);
        this.comUtils = comUtils;
        this.file = file;
        this.id = id;
        game = new ProtocolServer(comUtils);
        errors = new Errors();
        this.usuaris = usuaris;
    }
    
    /**
     * Starts new Game
     * @throws IOException 
     */
    void iniciarPartida() throws IOException, InterruptedException{
        try {
            //Asignem els diners de la partida (en un futur haurem de fer un getCoinsClient(id) per asignar)
            game.setInitialBet(100);
            game.setClientChips(usuaris.get(id));
            game.setServerChips(500);
            game.setEnd(false);  
            pw = new PrintWriter(file);
            pw.println("C: STRT "+id);
            pw.println("S: ANTE "+game.getInitialBet());                
            partida();
            usuaris.put(id, game.getClientChips());
            pw.close();
        } catch (IOException ex) {
            pw.println("C: QUIT");
            usuaris.put(id, game.getClientChips());
            pw.close();
            errors.sendDataError(comUtils);
        }
    }
    
    /**
     * Game
     * @throws IOException 
     */
    void partida() throws IOException, InterruptedException{
        try{
            //Primer informem de la aposta inicial i la quantitat de fiches/dinners que tenim
            comUtils.write_buffer(comUtils.string_to_buffer("ANTE"));// string to buffer ens retorna bytes a partir d'un string 
            comUtils.write_SP();
            comUtils.write_buffer(comUtils.int_to_buffer(game.getInitialBet())); 
            game.gameChipsInformation(pw);
            while(!game.isEnd()){
                TimeUnit.MILLISECONDS.sleep(100);
                String command = new String(comUtils.read_bytes(4));
                command = command.toUpperCase();
                switch(command){
                    case "ANOK": // accepta les nostres condicions
                        if(game.getClientChips() < game.getInitialBet() || game.getServerChips() < game.getInitialBet()){
                            //enviamos mensaje de error y salimos
                            pw.println("S: ERRO 09Not Chips");
                            errors.sendChipsError(comUtils);
                            usuaris.put(id, game.getClientChips());
                            game.setEnd(true);
                        }
                        else{
                            game.setDrawRound(false);
                            game.getServerHand().getHand().clear();
                            //Creem una baralla cada cop que comença una ronda
                            game.resetDeck();
                            //afegim l'aposta inicial
                            game.setRoundBet(game.getInitialBet()+game.getInitialBet());
                            //restem al client l'aposta d'entrada
                            game.setClientChips(game.getClientChips() - game.getInitialBet());
                            game.setServerChips(game.getServerChips() - game.getInitialBet());

                            //Escrivim al log                                  
                            pw.println("C: ANOK");

                            //assignem el dealer de manera aleatoria
                            int dealer = game.generateDealer();
                            comUtils.write_buffer(comUtils.string_to_buffer("DEAL"));//hay que ver si funciona asi
                            comUtils.write_SP();
                            comUtils.write_buffer(comUtils.string_to_buffer(Integer.toString(dealer)));
                            //Escrivim
                            pw.println("S: DEAL "+"'"+dealer+"'");

                            //Repartir cartes al client
                            String cHand = "S: HAND";
                            comUtils.write_buffer(comUtils.string_to_buffer("HAND"));
                            game.getClientHand().getHand().clear();
                            for(int i = 0; i < 5 ; i++){
                                comUtils.write_SP();
                                Card card = game.getCard(); // ens retorna una carta aleatoria de la baralla
                                String c = card.getString();
                                game.getClientHand().getHand().add(card);
                                comUtils.write_buffer(comUtils.string_to_buffer(c));//retorna una carta del deck
                                cHand = cHand+" "+c;
                            }
                            pw.println(cHand);
                            // Ens asignem cartes a la nostra ma (server)
                            for(int i = 0; i < 5 ; i++){
                                Card card = game.getCard();// ens retorna una carta aleatoria de la baralla
                                game.getServerHand().getHand().add(card);
                            }
                            //Comprovem si som el dealer, si ho som fem una aposta
                            if(dealer == 1){
                                //Generem una accio aleatoria
                                String option = game.generateDecision("ANOK");
                                switch(option){
                                    case "PASS":
                                        comUtils.write_buffer(comUtils.string_to_buffer("PASS"));
                                        pw.println("S: PASS");
                                        //game.setServerChips(game.getServerChips()-game.getInitialBet());
                                        break;
                                    case "BET_":
                                        game.serverBet(pw);
                                        break;
                                }
                            }

                        }

                        break;

                    case "PASS":
                        pw.println("C: PASS ");
                        if(game.getDealer() == 1 && game.isDrawRound()){ // si some l dealer(juguem primer) i ja hem descartat
                            game.showdown(pw);
                            game.gameChipsInformation(pw);
                        }
                        else{
                            if(game.getDealer() != 1){
                                //generem una accio aleatoria per al cas PASS
                                String option = game.generateDecision("PASS");
                                switch(option){
                                    case "PASS":
                                        pw.println("S: PASS");
                                        comUtils.write_buffer(comUtils.string_to_buffer("PASS"));
                                        if(game.isDrawRound()){// en el cas que ja hem descartat cartes, s'acaba la ronda
                                            game.showdown(pw);
                                            game.gameChipsInformation(pw);
                                        }
                                        break;
                                    case "BET_":
                                        game.serverBet(pw);
                                        break;
                                }
                            }
                        }
                        break;

                    case "CALL"://cas per igualar l'aposta, acaba la ronda.
                        //sumem als diners de la jugada la ultima nostra aposta, ja que el client ens la iguala
                        if(game.getClientChips() > game.getLastBet()){
                        
                            game.setRoundBet(game.getRoundBet()+game.getLastBet());
                            game.setClientChips(game.getClientChips() - game.getLastBet());
                            pw.println("C: CALL");
                            // comprovem si ja hem descartat cartes
                            if(game.isDrawRound()){// si ja hem descartat
                                game.showdown(pw);
                                game.gameChipsInformation(pw);

                            }
                        }else{
                            pw.println("S: ERRO 09Not Chips");
                            errors.sendChipsError(comUtils);
                            game.setEnd(true);
                        }

                        break;

                    case "RISE"://El client iguala i puja la nostre aposta
                        /******************** POR AHORA OMITO  HACER OTRO RISE *////////////////////////
                        comUtils.read_bytes(1); // llegim el SP
                        int raise = comUtils.bytesToInt32(comUtils.read_bytes(4), "be");
                        pw.println("C: RISE "+raise);
                        //sumem l'ultima aposta que hem fet per tal d'igualar i pujar (raise)
                        if(game.isDrawRound()){
                            String optionRaise = game.generateDecision("RISE");
                            switch(optionRaise){
                                case "CALL":
                                    if(game.getServerChips() > raise){
                                        // igualem l'aposta del client
                                        int raise2 = raise + game.getLastBet();//afegim la nostra aposta anterior
                                        game.setRoundBet(game.getRoundBet()+raise2); // afegim a les fitxes de la ronda
                                        game.setClientChips(game.getClientChips()-raise); // restm al client lo que acaba d'apostar
                                        game.setServerChips(game.getServerChips() - raise2);   
                                        comUtils.write_buffer(comUtils.string_to_buffer("CALL"));
                                        pw.println("S: CALL");
                                        game.showdown(pw);
                                        game.gameChipsInformation(pw);

                                    }else{
                                        // igualem l'aposta del client amb el que tenim                                       
                                        game.setRoundBet(game.getRoundBet()+game.getServerChips()); // afegim a les fitxes de la ronda
                                        game.setClientChips(game.getClientChips()-raise); // restm al client lo que acaba d'apostar
                                        game.setServerChips(0);   
                                        comUtils.write_buffer(comUtils.string_to_buffer("CALL"));
                                        pw.println("S: CALL");
                                        game.showdown(pw);
                                        game.gameChipsInformation(pw);
                                    }
                                    break;  
                                case "FOLD":
                                    comUtils.write_buffer(comUtils.string_to_buffer("FOLD"));
                                    pw.println("S: FOLD");
                                    game.clientWins();
                                    game.gameChipsInformation(pw);//enviem STKS
                                    break;
                                case "RISE":
                                    game.serverRaise(pw);
                                    break;
                            }

                        }
                        else{// en el cas que ya hem descartat cartes
                            //La diferencia amb el si no hem descartat es que si fem el call hem de fer el showdown
                            String optionRaise = game.generateDecision("RISE");
                            switch(optionRaise){
                                case "CALL":
                                    if(game.getServerChips() > raise){
                                        // igualem l'aposta del client
                                        int raise2 = raise + game.getLastBet();//afegim la nostra aposta anterior
                                        game.setRoundBet(game.getRoundBet()+raise2); // afegim a les fitxes de la ronda
                                        game.setClientChips(game.getClientChips()-raise); // restm al client lo que acaba d'apostar
                                        game.setServerChips(game.getServerChips() - raise2);   
                                        comUtils.write_buffer(comUtils.string_to_buffer("CALL"));
                                        pw.println("S: CALL");

                                    }else{
                                        // igualem l'aposta del client amb el que tenim                                       
                                        game.setRoundBet(game.getRoundBet()+game.getServerChips()+raise); // afegim a les fitxes de la ronda
                                        game.setClientChips(game.getClientChips()-raise); // restm al client lo que acaba d'apostar
                                        game.setServerChips(0);   
                                        comUtils.write_buffer(comUtils.string_to_buffer("CALL"));
                                        pw.println("S: CALL");
                                    }

                                    break;
                                case "FOLD":
                                    comUtils.write_buffer(comUtils.string_to_buffer("FOLD"));
                                    pw.println("S: FOLD");
                                    game.clientWins();
                                    game.gameChipsInformation(pw);//enviem STKS
                                    break;
                                case "RISE":
                                    game.serverRaise(pw);
                                    break;
                            }
                        }

                        break;

                    case "BET_": // aposta del client
                        comUtils.read_bytes(1); // llegim el SP

                        //decidime que fem després de rebre la opciRo del client de apostar
                        String optionBet = game.generateDecision("BET_");
                        switch(optionBet){
                            case "CALL":                                   
                                // igualem l'aposta del client
                                int clientBet = comUtils.bytesToInt32(comUtils.read_bytes(4), "be");
                                if(game.getServerChips() > clientBet){
                                    game.setRoundBet(game.getRoundBet()+clientBet+clientBet);
                                    game.setClientChips(game.getClientChips() - clientBet);
                                    //Escrivim l'aposta al log
                                    pw.println("C: BET_ "+clientBet);

                                    comUtils.write_buffer(comUtils.string_to_buffer("CALL"));
                                    pw.println("S: CALL");
                                    game.setServerChips(game.getServerChips()-clientBet);

                                    if(game.isDrawRound()){
                                        game.showdown(pw);
                                        game.gameChipsInformation(pw);
                                    }
                                }
                                else{ // si toca la opcio de call pero no tenim diners, ens retirem
                                    
                                    game.clientWins();
                                    pw.println("S: FOLD");
                                    comUtils.write_buffer(comUtils.string_to_buffer("FOLD"));                             
                                    game.gameChipsInformation(pw);//enviem STKS
                                }

                                break;
                            case "FOLD":
                                int desc = comUtils.bytesToInt32(comUtils.read_bytes(4), "be");
                                game.clientWins();
                                pw.println("S: FOLD");
                                comUtils.write_buffer(comUtils.string_to_buffer("FOLD"));                             
                                game.gameChipsInformation(pw);//enviem STKS
                                break;
                            case "RISE":
                                game.serverRaise(pw);
                                break;
                        }
                        break;
                    case "DRAW"://estructra del draw DRAW<SP><'0'|'1'|'2'|'3'|'4'|'5'>(<SP><CARD>)[0-5]
                        game.setDrawRound(true);
                        comUtils.read_bytes(1); // llegim el SP
                        //Llegim a veruecuantes cartes descarta el client
                        String numDesc =new String(comUtils.read_bytes(1));
                        int numD = Integer.parseInt(numDesc);
                        String cDraw = "C: DRAW "+"'"+numD+"'";
                        //Li donem tantes cartes com ha descartat
                        for(int i = 0 ; i < numD; i++){
                            comUtils.read_bytes(1); // llegim el SP
                            String rank = new String(comUtils.read_bytes(1));
                            String suit;
                            if(rank.equals("1")){ 
                                String typeNext = new String(comUtils.read_bytes(1));
                                suit = new String(comUtils.read_bytes(1));
                                String totalType = rank+typeNext; // suma del 1 i del 0 
                                if(totalType.equals("10")){
                                    Card c = new Card(totalType,suit);
                                    cDraw = cDraw + " "+ c.getString();
                                    if(game.getClientHand().getHand().contains(c)){
                                        game.getClientHand().getHand().remove(c);

                                    }
                                }
                            }
                            else{
                                String suit2 = new String(comUtils.read_bytes(1));
                                Card c = new Card(rank,suit2);
                                cDraw = cDraw + " "+ c.getString();
                                if(game.getClientHand().getHand().contains(c)){
                                    game.getClientHand().getHand().remove(c);
                                    }
                            } 
                        }
                        //game.setDrawRound(true);
                        pw.println(cDraw);
                        game.draw_server(numD,pw);

                        if(game.getDealer() == 1){
                                //generem una accio aleatoria per al cas PASS
                                String option = game.generateDecision("DRAW");
                                switch(option){
                                    case "PASS":
                                        comUtils.write_buffer(comUtils.string_to_buffer("PASS"));
                                        pw.println("S: PASS");

                                        break;
                                    case "BET_":
                                        game.serverBet(pw);
                                        break;

                                    case "FOLD":
                                        comUtils.write_buffer(comUtils.string_to_buffer("FOLD"));
                                        pw.println("S: FOLD");
                                        game.clientWins();
                                        game.gameChipsInformation(pw);//enviem STKS
                                        break;
                                }
                                    }


                    break;

                    case "FOLD": // no anar a al jugada
                        game.serverWins();
                        game.gameChipsInformation(pw);//enviem STKS
                        break;

                    case "ERRO":
                        //tractem l'error
                        game.responseError(pw);

                        break;

                    case "QUIT"://El client vol sortir
                        //pw.println("C: QUIT\n");
                        game.setEnd(true);
                        usuaris.put(id, game.getClientChips());
                        //pw.close();
                        break;
                    default:
                        errors.sendCommandError(comUtils);
                        pw.println("S: 13Command Error");
                        usuaris.put(id, game.getClientChips());
                        pw.close();
                        game.setEnd(true); 
                    break;
                }
            }
        }
        catch(IOException ex){
            pw.println("C: QUIT");
            usuaris.put(id, game.getClientChips());
            pw.close();
        }
    }
}

