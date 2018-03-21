
import java.io.IOException;


/**Class Errors, send errors to socket
 *
 * @author Orlando i Hicham
 */
public class Errors {

    /** Send socket error
     * 
     * @param comUtils
     * @throws IOException
     */
    public void sendSocketError(ComUtils comUtils) throws IOException {
        String errorMessage= "Socket error";
        String lenErrorMessage = Integer.toString(errorMessage.length());
        try {
            comUtils.write_buffer(comUtils.string_to_buffer("ERRO"));//Send ERRO to the socket
            comUtils.write_buffer(comUtils.string_to_buffer(" "));//Send space
            if(Integer.parseInt(lenErrorMessage)< 10){
                comUtils.write_buffer(comUtils.string_to_buffer("0"));//Send length of message
            }
            comUtils.write_buffer(comUtils.string_to_buffer(lenErrorMessage));//Send length of message
            comUtils.write_buffer(comUtils.string_to_buffer(errorMessage));//Send message
            System.err.println("Error: "+errorMessage);
        } catch (IOException ex) {
            System.err.println("Error: "+errorMessage);
        }
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
            comUtils.write_buffer(comUtils.string_to_buffer(" "));//Send space
            if(Integer.parseInt(lenErrorMessage)< 10){
                comUtils.write_buffer(comUtils.string_to_buffer("0"));//Send length of message
            }
            comUtils.write_buffer(comUtils.string_to_buffer(lenErrorMessage));//Send length of message
            comUtils.write_buffer(comUtils.string_to_buffer(errorMessage));//Send message
            System.err.println("Error: "+errorMessage);
        } catch (IOException ex) {
            System.err.println("Error: "+errorMessage);
        }
    }
}
    
