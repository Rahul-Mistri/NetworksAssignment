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
            int communicationPort = MyServer.getComPort();
            // the printerWriter connectionOut is used to send the client its unique
            // communicationPort number.
            connectionOut.println(communicationPort);
            // establishess a new connection with the client using its unique PORT.
            ServerSocket communicationServerSocket = new ServerSocket(communicationPort);
            // communicationSocket waits for any response from the client.
            Socket communicationSocket = communicationServerSocket.accept();
            // communicationIn is a bufferedReader used to receive any information sent by
            // the client on the unique Port.
            communicationIn = new BufferedReader(new InputStreamReader(communicationSocket.getInputStream()));
            // communicationOut is a printWriter used to send any information to the client
            // through the unique port.
            communicationOut = new PrintWriter(communicationSocket.getOutputStream(), true);
            // continous loop
            while (true) {
                // reads what the client sent.
                String request = communicationIn.readLine();
                // compare statement of what the client entered
                if (request.contains("name")) {
                    // TEMPORARY-- gets random name.
                    communicationOut.println(MyServer.getRandom());
                } else {
                    communicationOut.println("Error. Type name to print out a random name. Or quit to quit the app.");
                }
            }
        } catch (IOException e) {

        } finally {
            try {
                // closing bufferedReader and printWriter
                communicationIn.close();
                communicationOut.close();
            } catch (IOException e) {
            }
        }

    }
}