package Logica;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author apardolo7.alumnes
 */
import Utils.ComUtils;
import Utils.Errors;
import java.io.*;
import java.net.*;
import java.util.Hashtable;

/**
 * 
 * @author Orlando i Hicham
 */
public class Server {
    
  /**
   * Main
   * @param args 
   */
  public static void main(String[] args){
    int portServidor = 1212;
    Hashtable<Integer, Integer> usuaris = new Hashtable<Integer, Integer>();
    
    if (args.length > 1){
      System.out.println("Us: java Servidor [<numPort>]");
      System.exit(1);
    }
       
    if (args.length == 1)
        portServidor = Integer.parseInt(args[0]);   
    try{
        ServerSocket serverSocket = new ServerSocket(portServidor);
        while(true){
            Socket socket = serverSocket.accept();
            ServerThread thread = new ServerThread(socket,usuaris);
            thread.start();
        }
    }catch(Exception e){
        System.err.println("S'ha tancat el servidor");
    }
    } // fi del main
} // fi de la classe