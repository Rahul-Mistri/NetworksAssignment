package app;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {
    // Setting the constant connection port
    private static final int PORT = 6666;
    // setting the default communication port (will increment by one)
    public static int comPort = 6666;
    // cuurent storage method.
    private static String[] names = { "Georgeo", "Rahul", "Silas" };
    // Array list used to store clients that connect to the server
    private static ArrayList<MyClientHandler> myClients = new ArrayList<>();
    // thread executor pool
    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws IOException {
        // establishes a connection on the connection port called
        // connectionServerSocket.
        ServerSocket connectionServerSocket = new ServerSocket(PORT);
        // continus loop
        while (true) {
            // connection socket is listening-- havent started communicating yet.
            Socket connectionSocket = connectionServerSocket.accept();

            // creates a clientHandler for the current client passing in the
            // connectionSocket as constructor
            MyClientHandler myClientThread = new MyClientHandler(connectionSocket);
            // adds the client to the array list -- future use
            myClients.add(myClientThread);
            // executes the run method of the thread client
            pool.execute(myClientThread);
        }

    }

    // TEMPORARY get random method for now
    public static String getRandom() {
        String name = names[(int) (Math.random() * names.length)];

        return name;

    }

    // synchronized method to assign a unique port number for every client that
    // connects
    public static synchronized int getComPort() {
        comPort += 1;
        return comPort;
    }
}