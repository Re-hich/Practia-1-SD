package Utils;


import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author adrip
 */
public class Errors {

    public void errorIniConection(ComUtils comUtils) {
    }
    /** Send data error
     * 
     * @param comUtils
     * @throws IOException
     */
    public void sendDataError(ComUtils comUtils) throws IOException {
        String errorMessage= "Data error";
        String lenErrorMessage = Integer.toString(errorMessage.length());
        try {
            comUtils.write_buffer(comUtils.string_to_buffer("ERRO"));//Send ERRO to the socket
            comUtils.write_SP(); 
            if(Integer.parseInt(lenErrorMessage) < 10){
                comUtils.write_buffer(comUtils.string_to_buffer("0"));//Send length of message
            }
            comUtils.write_buffer(comUtils.string_to_buffer(lenErrorMessage));//Send length of message
            comUtils.write_buffer(comUtils.string_to_buffer(errorMessage));//Send message
        } catch (IOException ex) {
            System.err.println("Error: "+errorMessage);
        }
    }
    
    /**
     * Sends Chips error
     * @param comUtils
     * @throws IOException 
     */
    public void sendChipsError(ComUtils comUtils) throws IOException {
        String errorMessage= "Not chips";
        String lenErrorMessage = Integer.toString(errorMessage.length());
        try {
            comUtils.write_buffer(comUtils.string_to_buffer("ERRO"));//Send ERRO to the socket
            comUtils.write_SP();
            
            if(Integer.parseInt(lenErrorMessage) < 10){
                comUtils.write_buffer(comUtils.string_to_buffer("0"));//Send length of message
            }
            comUtils.write_buffer(comUtils.string_to_buffer(lenErrorMessage));//Send length of message
            comUtils.write_buffer(comUtils.string_to_buffer(errorMessage));//Send message
        } catch (IOException ex) {
            System.err.println("Error: "+errorMessage);
        }
    }
    
    /**
     * Sends command error
     * @param comUtils
     * @throws IOException 
     */
    public void sendCommandError(ComUtils comUtils) throws IOException {
        String errorMessage= "Command error";
        String lenErrorMessage = Integer.toString(errorMessage.length());
        try {
            comUtils.write_buffer(comUtils.string_to_buffer("ERRO"));//Send ERRO to the socket
            comUtils.write_SP(); 
            if(Integer.parseInt(lenErrorMessage) < 10){
                comUtils.write_buffer(comUtils.string_to_buffer("0"));//Send length of message
            }
            comUtils.write_buffer(comUtils.string_to_buffer(lenErrorMessage));//Send length of message
            comUtils.write_buffer(comUtils.string_to_buffer(errorMessage));//Send message
        } catch (IOException ex) {
            System.err.println("Error: "+errorMessage);
        }
    }
}
