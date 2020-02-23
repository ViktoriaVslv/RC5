package RC;

import java.io.*;
import java.io.FileOutputStream;

public class Main {
    public static void main(String[]args){
        try {
            FileInputStream readKey = new FileInputStream("key.txt");
            byte [] key=new byte[16];
            while (readKey.available() > 0) {
                for(int i=0;i<key.length; i++)
                    key[i]=(byte)readKey.read();
            }
            String key1 = new String(key);


            int w=64;
            Key k = new Key(w, 12, key1);

            Cryptographer c = new Cryptographer(k);
            FileInputStream fileIn = new FileInputStream("text.txt");
            FileOutputStream fileOut = new FileOutputStream("text1.txt");

            while (fileIn.available() > 0) {
                int []b= new int[w/8];
                for(int i=0;i<b.length; i++){
                    if(fileIn.available() > 0)
                        b[i]=fileIn.read();
                    else b[i]=0;
                }
                int []res=c.encryption(b);
                //int []res=c.decryption(b);
                for(int i=0;i<res.length; i++){
                    fileOut.write(res[i]);
                }
            }
            fileIn.close();
            fileOut.close();
        }
        catch (IOException e){
            System.out.println(e);
        }
    }
}
