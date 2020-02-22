package app;
import java.io.*;  
import java.net.*;  
import java.util.*;


public class MyClient {  
    private static final int SERVERPORT=6666;
    private static final String SERVERIP="localhost";
    public static void main(String[] args) {  
        try{      
        Socket s=new Socket(SERVERIP,SERVERPORT);  
        BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedReader fromUser=new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out=new PrintWriter(s.getOutputStream(),true);
        System.out.println("Type name to see a random name. Or Quit.");
        while(true){
            String userIn=fromUser.readLine();
            if(userIn.equals("quit")){break;}
            out.println(userIn);

            String serverResponse=in.readLine();

            System.out.println(serverResponse);
        }
        s.close();  
        }
        catch(Exception e){System.out.println(e);}  
    }  
}  