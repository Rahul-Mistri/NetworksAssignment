package app;

import java.io.*;
import java.net.*;
import java.util.*;

public class MyClientHandler implements Runnable {
    private Socket myClient;
    private PrintWriter connectionOut;
    private PrintWriter communicationOut;
    private BufferedReader communicationIn;

    // constructor
    public MyClientHandler(Socket myClientSocket) throws IOException {
        // assignes the communication socket to myClient.
        this.myClient = myClientSocket;
        // connectionOut is a printWriter that will be use to send the client a new PORT
        // number.
        connectionOut = new PrintWriter(myClient.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            // communicationPort is the unique port that the server and client will
            // communicate through.
           // int communicationPort = MyServer.getComPort();
            // the printerWriter connectionOut is used to send the client its unique
            // communicationPort number.
           // connectionOut.println(communicationPort);
            // establishess a new connection with the client using its unique PORT.
           // ServerSocket communicationServerSocket = new ServerSocket(communicationPort);
            // communicationSocket waits for any response from the client.
          //  Socket communicationSocket = communicationServerSocket.accept();
            // communicationIn is a bufferedReader used to receive any information sent by
            // the client on the unique Port.

            communicationIn = new BufferedReader(new InputStreamReader(myClient.getInputStream()));
            // communicationOut is a printWriter used to send any information to the client
            // through the unique port.
            communicationOut = new PrintWriter(myClient.getOutputStream(), true);

            //Authentication
            //user authentication
            String userNameTxt="Enter Username: ";
            communicationOut.println(userNameTxt);
            String uName = communicationIn.readLine();
            String passwordTxt="Enter Password: ";
            communicationOut.println(passwordTxt);
            String password = communicationIn.readLine();
            String checkPass="";
            for (String i : MyServer.usersAndPass.keySet()) {
                if(i.equals(uName)){
                    checkPass=MyServer.usersAndPass.get(i);
                }
            }
            if(checkPass.equals(password)){
            communicationOut.println("success");
            // continous loop
            boolean flag = true;
            while (flag) {
                // reads what the client sent.
                    String  menu="1) UPLOAD \n2) DOWNLOAD \n3) QUERY \n4) QUIT \n";
                    communicationOut.println(menu);
                String request = communicationIn.readLine();

                // compare statement of what the client entered
                switch (request) {
                    case "1":
                    case "UPLOAD":
                        System.out.println("Told me to upload");
                        break;
                    case "2":
                    case "DOWNLOAD":
                        System.out.println("Told me to download");
                        break;
                    case "3":
                    case "QUERY":
                        System.out.println("Told me to QUERY");
                        break;
                    case "4":
                    case "QUIT":
                        System.out.println("Told me to QUIT");
                        communicationOut.println("connection on port #"+myClient.getPort()+" is closed");
                        flag=false;
                        break;
                    default:
                        break;
                }

            }
        }
        else{
            System.out.println("Invalid user name and password. Please try again.");
            communicationOut.println("fail");
        }
        } catch (IOException e) {

        } finally {
            try {
                // closing bufferedReader and printWriter
                communicationIn.close();
                communicationOut.close();
                System.out.println("Closed stuff");
            } catch (IOException e) {
            }
        }

    }
}