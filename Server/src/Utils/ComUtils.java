package Utils;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.*;
import java.io.*;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

//package practica_0;
public class ComUtils {

    /* Mida d'una cadena de caracters */
    private final int STRSIZE = 40;
    /* Objectes per escriure i llegir dades */
    private DataInputStream dis;
    private DataOutputStream dos;

    public ComUtils(Socket socket) throws IOException {
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
    }
    
    /* Extensions de la practica*/
    /*Creem un nou constructor que rebi un FILE en comptes d'un socket*/
    public ComUtils(File file) throws IOException{
    	dis = new DataInputStream(new FileInputStream(file));
    	dos = new DataOutputStream(new FileOutputStream(file));
    }
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

    /* Llegir un string  mida variable size = nombre de bytes especifica la longitud*/
    public String read_string_variable(int size) throws IOException {
        byte bHeader[] = new byte[size];
        char cHeader[] = new char[size];
        int numBytes = 0;

        // Llegim els bytes que indiquen la mida de l'string
        bHeader = read_bytes(size);
        // La mida de l'string ve en format text, per tant creem un string i el parsejem
        for (int i = 0; i < size; i++) {
            cHeader[i] = (char) bHeader[i];
        }
        numBytes = Integer.parseInt(new String(cHeader));

        // Llegim l'string
        byte bStr[] = new byte[numBytes];
        char cStr[] = new char[numBytes];
        bStr = read_bytes(numBytes);
        for (int i = 0; i < numBytes; i++) {
            cStr[i] = (char) bStr[i];
        }
        return String.valueOf(cStr);
    }

    /* Escriure un string mida variable, size = nombre de bytes especifica la longitud  */
 /* String str = string a escriure.*/
    public void write_string_variable(int size, String str) throws IOException {

        // Creem una seqüència amb la mida
        byte bHeader[] = new byte[size];
        String strHeader;
        int numBytes = 0;

        // Creem la capçalera amb el nombre de bytes que codifiquen la mida
        numBytes = str.length();

        strHeader = String.valueOf(numBytes);
        int len;
        if ((len = strHeader.length()) < size) {
            for (int i = len; i < size; i++) {
                strHeader = "0" + strHeader;
            }
        }
        for (int i = 0; i < size; i++) {
            bHeader[i] = (byte) strHeader.charAt(i);
        }
        // Enviem la capçalera
        dos.write(bHeader, 0, size);
        // Enviem l'string writeBytes de DataOutputStrem no envia el byte més alt dels chars.
        dos.writeBytes(str);
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
    
    public void write_buffer(byte[] byteArray){
        
        try {
            int numBytes = byteArray.length;
            dos.write(byteArray, 0, numBytes);
        } catch (IOException ex) {
            Logger.getLogger(ComUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void write_SP(){
        try {
            char SP =' ';
            dos.write((byte) SP);
        } catch (IOException ex) {
            Logger.getLogger(ComUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void writeTest() throws IOException {
       write_string("Funciona");
        
    }

    String readTest() throws IOException {
        return read_string();
    }
}
