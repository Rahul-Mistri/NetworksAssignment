package app;
import java.io.*;  
import java.net.*;  
import java.util.*;
public class MyClient {  
    public static void main(String[] args) {  
        try{      
        Socket s=new Socket("localhost",6666);  
        DataOutputStream dout=new DataOutputStream(s.getOutputStream());  
        Scanner in=new Scanner(System.in);
        System.out.println("Write a message");
        String sIn =in.nextLine();
        dout.writeUTF(sIn);  
        dout.flush();  
        dout.close();  
        s.close();  
        }
        catch(Exception e){System.out.println(e);}  
    }  
}  