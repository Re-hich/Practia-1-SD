import java.net.*;
import java.io.*;
/**Class ComUtils
 * 
 * @author Carles
 */
public class ComUtils {

    /* Mida d'una cadena de caracters */
    private final int STRSIZE = 40;
    /* Objectes per escriure i llegir dades */
    private DataInputStream dis;
    private DataOutputStream dos;
    /**Constructor
     * 
     * @param socket
     * @throws IOException 
     */
    public ComUtils(Socket socket) throws IOException {
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }
    
    /* Extensions de la practica*/

    /* Llegir un enter de 32 bits (4 bytes) */
    public int read_int32() throws IOException {
        byte bytes[] = new byte[4];
        bytes = read_bytes(4);

        return bytesToInt32(bytes, "be");
    }

    /* Escriure un enter de 32 bits */
    public void write_int32(int number) throws IOException {
        byte bytes[] = new byte[4];

        int32ToBytes(number, bytes, "be");
        dos.write(bytes, 0, 4);
    }
    


    /* Llegir un string de mida STRSIZE ,siempre leera 40 bytes*/
    public String read_string() throws IOException {
        String str;
        byte bStr[] = new byte[STRSIZE];
        char cStr[] = new char[STRSIZE];

        bStr = read_bytes(STRSIZE);

        for (int i = 0; i < STRSIZE; i++) {
            cStr[i] = (char) bStr[i];
        }

        str = String.valueOf(cStr);

        return str.trim();
    }

    /* Escriure un string */
    public void write_string(String str) throws IOException {
        int numBytes, lenStr;
        byte bStr[] = new byte[STRSIZE];

        lenStr = str.length();

        if (lenStr > STRSIZE) {
            numBytes = STRSIZE;
        } else {
            numBytes = lenStr;
        }

        for (int i = 0; i < numBytes; i++) {
            bStr[i] = (byte) str.charAt(i);
        }

        for (int i = numBytes; i < STRSIZE; i++) {
            bStr[i] = (byte) ' ';
        }

        dos.write(bStr, 0, STRSIZE);
    }
    
    /* Escriure un string */
    public byte[] string_to_buffer(String str) throws IOException {
        int lenStr = str.length();
        byte[] byteArray=new byte[lenStr];

        for (int i = 0; i < lenStr; i++) {
            byteArray[i] = (byte)str.charAt(i);
        }
        //System.out.println(new String(byteArray));
        return byteArray;
    }
    
    public byte[] int_to_buffer(int num){
        byte bytes[] = new byte[4];
        int32ToBytes(num, bytes, "be");
        
        return bytes;
    }
    
    public void write_buffer(byte[] byteArray) throws IOException{

        int numBytes = byteArray.length;
        dos.write(byteArray, 0, numBytes);

    }
    /* Passar d'enters a bytes */
    private int int32ToBytes(int number, byte bytes[], String endianess) {
        if ("be".equals(endianess.toLowerCase())) {
            bytes[0] = (byte) ((number >> 24) & 0xFF);
            bytes[1] = (byte) ((number >> 16) & 0xFF);
            bytes[2] = (byte) ((number >> 8) & 0xFF);
            bytes[3] = (byte) (number & 0xFF);
        } else {
            bytes[0] = (byte) (number & 0xFF);
            bytes[1] = (byte) ((number >> 8) & 0xFF);
            bytes[2] = (byte) ((number >> 16) & 0xFF);
            bytes[3] = (byte) ((number >> 24) & 0xFF);
        }
        return 4;
    }

    /* Passar de bytes a enters */
    public int bytesToInt32(byte bytes[], String endianess) {
        int number;

        if ("be".equals(endianess.toLowerCase())) {
            number = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16)
                    | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        } else {
            number = (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8)
                    | ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }
    

    //llegir bytes.

    public byte[] read_bytes(int numBytes) throws IOException {
        int len = 0;
        byte bStr[] = new byte[numBytes];
        int bytesread = 0;
        do {
            bytesread = dis.read(bStr, len, numBytes - len);
            if (bytesread == -1) {
                throw new IOException("Broken Pipe");
            }
            len += bytesread;
        } while (len < numBytes);
        return bStr;
    }


    void writeTest() throws IOException {
       write_string("Funciona");
        
    }

    String readTest() throws IOException {
        return read_string();
    }
}
