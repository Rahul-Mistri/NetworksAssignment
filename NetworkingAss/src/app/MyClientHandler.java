package app;

import java.io.*;
import java.net.Socket;

public class MyClientHandler implements Runnable {
    private Socket myClient;
    private PrintWriter out;
    private BufferedReader in;
    private static final int PORT = 6666;

    public MyClientHandler(Socket myClientSocket) throws IOException {
        this.myClient = myClientSocket;
        in = new BufferedReader(new InputStreamReader(myClient.getInputStream()));
        out = new PrintWriter(myClient.getOutputStream(), true);

    }

    @Override
    public void run() {
        try {
            while (true) {
                String request = in.readLine();
                if (request.contains("name")) {
                    out.println(MyServer.getRandom());
                } else {
                    out.println("Error. Type name to print out a random name. Or quit to quit the app.");
                }
            }
        } catch (IOException e) {

        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
            }
        }

    }
}