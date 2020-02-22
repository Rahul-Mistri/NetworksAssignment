package app;

import java.io.*;  
import java.net.*;
import java.util.*;
public class MyServer {  
    private static final int PORT=6666;
    public static void main(String[] args){  
        try{  
            ServerSocket ss=new ServerSocket(PORT);  
            Socket s=ss.accept();//establishes connection   
            PrintWriter out=new PrintWriter(s.getOutputStream(),true);
            String date=(new Date()).toString();
            out.println(date);
            s.close();
            ss.close();  
        }
        catch(Exception e){System.out.println(e);}  
    }  
}  