package app;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {
    private static final int PORT = 6666;
    private static String[] names = { "Georgeo", "Rahul", "Silas" };
    private static ArrayList<MyClientHandler> myClients = new ArrayList<>();

    private static ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(PORT);

        while (true) {
            Socket s = ss.accept();// establishes connection
            MyClientHandler myClientThread = new MyClientHandler(s);
            myClients.add(myClientThread);
            pool.execute(myClientThread);
        }
    }

    public static String getRandom() {
        String name = names[(int) (Math.random() * names.length)];
        return name;
    }
}