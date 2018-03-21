/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import Utils.ComUtils;
import Utils.Errors;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;

/**
 *
 * @author Orlando i Hicham
 */
public class ServerThread extends Thread{
    Socket socket=null;
    int portServidor = 1234;
    ComUtils comUtils;
    Errors errors = new Errors();
    int id ;
    File file;
    PrintWriter pw;
    Hashtable<Integer, Integer> usuaris;// = new Hashtable<String, Integer>();
    
    /**
     * Constructor
     * @param socket
     * @param usuaris
     * @throws IOException 
     */
    public ServerThread(Socket socket,Hashtable<Integer, Integer> usuaris) throws IOException{    
        this.socket = socket;
        this.usuaris = usuaris;
    }

   /**
    * Run method
    */
    @Override
    public void run() {
        
        try{
            while (true) {

                System.out.println("Connexió amb un nou client. ");
                comUtils = new ComUtils(socket);
                if(comUtils == null){
                    errors.errorIniConection(comUtils);
                }
                //El client ens envia el STRT  
                comUtils.read_bytes(4); // Llegim STRT
                comUtils.read_bytes(1); // Llegim el espai 
                id = comUtils.bytesToInt32(comUtils.read_bytes(4), "be"); // Obtenim el id 
                //comprovem si el tenim ja
                if(usuaris.containsKey(id)){
                    file = new File("Server"+Thread.currentThread().getName()+".log");
                    //Creem un protocol
                    Protocol protocol = new Protocol(id,comUtils,file,usuaris);
                    protocol.iniciarPartida();
                }
                else{
                    usuaris.put(id, 10);
                    file = new File("Server"+Thread.currentThread().getName()+".log");
                    //Creem un protocol
                    Protocol protocol = new Protocol(id,comUtils,file,usuaris);
                    protocol.iniciarPartida();
                }
            }
        }
        catch(Exception e){
            System.out.println("Thread closed");
        }
    }
}
