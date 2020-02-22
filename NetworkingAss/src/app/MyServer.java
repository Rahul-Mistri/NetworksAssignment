package app;

import java.io.*;  
import java.net.*;
import java.util.*;
public class MyServer {  
    private static final int PORT=6666;
    private static String[] names={"Georgeo","Rahul","Silas"};
    public static void main(String[] args) throws IOException{  
       
            ServerSocket ss=new ServerSocket(PORT);  
            Socket s=ss.accept();//establishes connection   
            PrintWriter out=new PrintWriter(s.getOutputStream(),true);
            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
            try{  
            while(true){
                String request=in.readLine();
                if (request.contains("name")){
                    out.println(getRandom());
                }
                else{
                    out.println("Error. Type name to print out a random name. Or quit to quit the app.");
                }
            }
        }finally{
            s.close();
            ss.close();  
        }
    }  
    public static String getRandom(){
        String name =names[(int)(Math.random()*names.length)];
        return name;
    }
}  