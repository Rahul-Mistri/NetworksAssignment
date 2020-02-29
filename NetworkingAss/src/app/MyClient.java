package app;

import java.io.*;
import java.net.*;
import java.util.*;

public class MyClient {

    // Setting the constant connection port
    private static final int SERVERPORT = 6666;
    // setting the constant ip of the server
    private static final String SERVERIP = "localhost";

    public static void main(String[] args) {
        try {
            // connectionSocket is making a connection request to the server using the
            // serversIp and the port 6666.
            Socket connectionSocket = new Socket(SERVERIP, SERVERPORT);
            // bufferedReader connectionIn is used to receive a unique PORT number from the
            // server such that the server and the client can talk through this port.
            // serverResponseWithPortNumber takes in the unique communication port number as
            // a string.
            // String serverResponseWithPortNumber = connectionIn.readLine();
            // communicationPort is the unique communication port number as a Integer.
            // int communicationPort = Integer.parseInt(serverResponseWithPortNumber);
            // System.out.println("Communicating through PORT no: " + communicationPort);
            // closing the connectionSocket as it is not needed anymore
            // connectionSocket.close();
            // communicationSocket is making a connection request to the server using the
            // serversIp and the new communicationPort received.
            // Socket communicationSocket = new Socket(SERVERIP, communicationPort);
            // bufferedReader communicationIn is used to receive information from server.
            BufferedReader communicationIn = new BufferedReader(
                    new InputStreamReader(connectionSocket.getInputStream()));

            // TEMPORARY BufferedReader fromUser will get an input from the user.
            BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
            // PrintWriter communicationOut is used to send messages to the server.
            PrintWriter communicationOut = new PrintWriter(connectionSocket.getOutputStream(), true);
            // System.out.println(menu);

            // authentication
            // username
            System.out.print(communicationIn.readLine());
            String userName;
            userName = fromUser.readLine();
            communicationOut.println(userName);
            // password
            System.out.print(communicationIn.readLine());
            String password;
            password = fromUser.readLine();
            communicationOut.println(password);
            String state = communicationIn.readLine();
            if (state.equals("success")) {
                // continous loop
                String userInput = "";
                Boolean flag = true;
                while (flag) {

                    // user input taken in .
                    String cLine = communicationIn.readLine();
                    System.out.println(cLine);
                    while (!(cLine.equals(""))) {

                        // System.out.println(cLine != null);

                        cLine = communicationIn.readLine();
                        System.out.println(cLine);

                        // System.out.println(menu);

                    }
                    userInput = fromUser.readLine();

                    // sending the request to the server.
                    communicationOut.println(userInput);
                    // receiving the reply from the server.
                    if ((userInput.equals("4")) || (userInput.equals("QUIT"))) {
                        System.out.println(communicationIn.readLine());
                        flag = false;
                    }

                }
            }

            else {
                System.out.println("Invalid User name and password \nExiting...");
            }
            // closing the communication socket.
            connectionSocket.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}