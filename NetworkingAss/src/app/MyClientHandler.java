package app;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MyClientHandler implements Runnable {
    private Socket myClient;
    private PrintWriter connectionOut;
    private PrintWriter communicationOut;
    private BufferedReader communicationIn;
    private OutputStream os;

    // constructor
    public MyClientHandler(Socket myClientSocket) throws IOException {
        // assignes the communication socket to myClient.
        this.myClient = myClientSocket;
        // connectionOut is a printWriter that will be use to send the client a new PORT
        // number.

    }

    @Override
    public void run() {
        try {
            os = myClient.getOutputStream();
            connectionOut = new PrintWriter(os, true);

            communicationIn = new BufferedReader(new InputStreamReader(myClient.getInputStream()));
            // communicationOut is a printWriter used to send any information to the client
            // through the unique port.
            communicationOut = new PrintWriter(myClient.getOutputStream(), true);

            // Authentication
            // user authentication
            String userNameTxt = "Enter Username: ";
            communicationOut.println(userNameTxt);
            String uName = communicationIn.readLine();
            String passwordTxt = "Enter Password: ";
            communicationOut.println(passwordTxt);
            String password = communicationIn.readLine();
            String checkPass = "";
            for (String i : MyServer.usersAndPass.keySet()) {
                if (i.equals(uName)) {
                    checkPass = MyServer.usersAndPass.get(i);
                }
            }
            if (checkPass.equals(password)) {
                communicationOut.println("success");
                // continous loop
                boolean flag = true;
                while (flag) {
                    // reads what the client sent.
                    System.out.println("Top of the menu");
                    String menu = "1) UPLOAD \n2) DOWNLOAD \n3) QUERY \n4) QUIT \n";
                    communicationOut.println(menu);
                    String request = communicationIn.readLine();

                    // compare statement of what the client entered
                    switch (request) {
                        case "1":
                        case "UPLOAD":
                            upload_query();
                            break;
                        case "2":
                        case "DOWNLOAD":
                            download_query();
                            System.out.println("server switch after download");
                            break;
                        case "3":
                        case "QUERY":
                            System.out.println("Told me to QUERY");
                            break;
                        case "4":
                        case "QUIT":
                            System.out.println("Told me to QUIT");
                            communicationOut.println("connection on port #" + myClient.getPort() + " is closed");
                            flag = false;
                            break;
                        default:
                            break;
                    }
                }
            } else {
                System.out.println("Invalid user name and password. Please try again.");
                communicationOut.println("fail");
            }
        } catch (IOException e) {

        } finally {
            try {
                // closing bufferedReader and printWriter
                communicationIn.close();
                communicationOut.close();
                os.close();
                System.out.println("Closed stuff");
            } catch (IOException e) {
            }
        }

    }



























    private void upload_query() {

        try {
            // Determines whether user entered existent file
            String client_file_result = communicationIn.readLine();
            // Execute upload if the file is valid
            if (!client_file_result.equalsIgnoreCase("Error 404")) {
                // Name of the file to be uploaded
                String filename = client_file_result;

                // Setup to read the bytestream
                int filesize = Integer.parseInt(communicationIn.readLine());
                int bytesRead;
                int currentTot = 0;
                byte[] bytearray = new byte[filesize];

                // Variables for server input and stream objects that write to a local file
                InputStream is = myClient.getInputStream();
                FileOutputStream fos = new FileOutputStream("Server_Copy_Of_" + filename);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                // Read the first byte stream
                bytesRead = is.read(bytearray, 0, bytearray.length);
                currentTot = bytesRead;

                // Read the server's remaining byte streams in chunks
                do {
                    // Reads the bytestream and adds it to the byteArray
                    bytesRead = is.read(bytearray, currentTot, (bytearray.length - currentTot));
                    // Updates the remaining bytes to be read
                    if (bytesRead > 0)
                        currentTot += bytesRead;
                } while (bytesRead > 0);

                // Write the locally stored byte streams into client file
                bos.write(bytearray, 0, currentTot);
                bos.flush();

                // Close the filewriting outputstream object
                bos.close();

                // Send acknowledgement to server to synchronize their progress
                communicationOut.println("Server has downloaded file");

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void download_query() {

        // Ask for file to be downloaded
        communicationOut.println("Enter the filename: ");
        try {
            // Receive filename and send it to user or error 404 if not found
            String filename = communicationIn.readLine();

            // Retrieve requested_file in servers system
            System.out.println("The servers directory path is " + System.getProperty("user.dir"));
            // String directory_path = System.getProperty("user.dir") + "/NetworkingAss/";
            File transferFile = getFile(filename);

            // Send file or not found response

            // Does not exist
            if (!transferFile.exists()) {
                communicationOut.println("Error 404");
            }
            // File exists
            // Send back to user
            else {

                communicationOut.println("Ok(200)");

                // Sending file to client
                communicationOut.println(transferFile.length());
                byte[] bytearray = new byte[(int) transferFile.length()];
                FileInputStream fin = new FileInputStream(transferFile);
                BufferedInputStream bin = new BufferedInputStream(fin);
                bin.read(bytearray, 0, bytearray.length);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Sending Files...");
                os.write(bytearray, 0, bytearray.length);
                os.flush();
                // os.close();

                // Requires acknowledgment of client to synchronize their processes
                communicationIn.readLine();
                System.out.println("done");

                // Acknowledgment of user

            }

        }

        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public File getFile(String filename) {
        Path currentRelativePath = Paths.get("");
        Path currentDir = currentRelativePath.toAbsolutePath();
        String subdirectory = "NetworkingAss";
        String subDir_And_Filename = subdirectory + File.separatorChar + filename;
        Path filepath = currentDir.resolve(subDir_And_Filename);
        File transferfile = filepath.toFile();
        return transferfile;
    }

}