import java.io.*;
import java.net.*;

/**Class Client
 *
 * @author Carles
 */
public class Client {
    
    public static void main(String[] args) {
        //Atributtes
        String serverName;
        int portNumber;
        int mode = 0;//Default Manual
                

        InetAddress serverAddress;
        Socket socket = null;

        
        int argLength = args.length;
        //System.out.println(Integer.toString(argLength));
        if (argLength >3 && argLength<2) {
            System.out.println("Use: java Client <server_name> <port>");
            System.exit(1);
        }
        serverName = args[0];//Get first argument
        portNumber = Integer.parseInt(args[1]);//Get second argument
        
        if(argLength == 3){
            mode = Integer.parseInt(args[2]);
        }
        
        //serverName = "localhost";//Get first argument
        //portNumber = 1212;//Get second argument*/
        
        try {
      
            serverAddress = InetAddress.getByName(serverName);//Get IP from the server
            socket = new Socket(serverAddress, portNumber);//Open connection with server
            
            switch(mode){
                case 0://Manual Mode
                    System.out.println("MANUAL MODE");
                    ClientProtocol clientProtocol = new ClientProtocol(socket);
                    while(!clientProtocol.isEnd()){
                        clientProtocol.doProtocol();//Function to start reading commands
                    }
                    break;
                case 1:
                    System.out.println("IA MODE");
                    ClientIAProtocol clientIAProtocol = new ClientIAProtocol(socket);
            
                    while(!clientIAProtocol.isEnd()){
                        clientIAProtocol.doProtocol();//Function to start reading commands
                    }
                    break;
                case 2:
                    System.out.println("Advanced IA not implemented yet");
                    break;
                default:
                    System.out.println("MANUAL MODE");
                    ClientProtocol clientProtocolDefault = new ClientProtocol(socket);
                    while(!clientProtocolDefault.isEnd()){
                        clientProtocolDefault.doProtocol();//Function to start reading commands
                    }
                    break;
            }
            
        } catch (UnknownHostException ex) {
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error: Cannot connect to server: "+serverName);
        } catch (IOException ex){
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println( ex.getClass().getCanonicalName());
            System.err.println("Error: Broken Pipe");

        }
        finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ex) {
                //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Error: Broken Pipe");
            }  
        }
    }
    
    
}
