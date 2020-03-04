

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MyClientHandler implements Runnable {
    // Declare variables for I/O, socket-connection, state-flags and user-input
    // purposes
    private Socket myClient;
    private PrintWriter connectionOut;
    private PrintWriter communicationOut;
    private BufferedReader communicationIn;
    private OutputStream os;
    private String myClientUname = "";
    private boolean close_unexpectedly = false;

    /**
     * Constructor for the Client "Thread"
     * 
     * @param Socket This is the socket created for communication between the client
     *               and server
     */
    public MyClientHandler(Socket myClientSocket) throws IOException {

        this.myClient = myClientSocket;

    }

    /**
     * Runs the body of the communication thread
     */
    @Override
    public void run() {
        try {
            // Creates objects for server/client communication, I/O and file writing
            // purposes
            os = myClient.getOutputStream();
            connectionOut = new PrintWriter(os, true);
            communicationIn = new BufferedReader(new InputStreamReader(myClient.getInputStream()));
            communicationOut = new PrintWriter(myClient.getOutputStream(), true);

            // Authentication
            // Send username prompt to the client
            String userNameTxt = "Enter Username: ";
            communicationOut.println(userNameTxt);
            // Read clients username response
            String uName = communicationIn.readLine();
            // Send password prompt to the client
            String passwordTxt = "Enter Password: ";
            communicationOut.println(passwordTxt);
            // Read clients password response
            String password = communicationIn.readLine();
            // Checks if the password for the specified user matches the true password
            // stored on the servers file system
            String checkPass = "";
            for (String i : MyServer.usersAndPass.keySet()) {
                if (i.equals(uName)) {
                    checkPass = MyServer.usersAndPass.get(i);
                }
            }
            // If passwords are matching prompt user for commands
            if (checkPass.equals(password)) {
                // Succesful login notification
                communicationOut.println("success");
                // Display succesful connection on the server side
                System.out.println("Client with address " + myClient.getRemoteSocketAddress().toString()
                        + " has opened connection");
                // continous loop
                myClientUname = uName;

                // Flag represents communication status
                // True: Communication is enabled
                // False: Client quit the communication
                boolean flag = true;
                while (flag) {
                    // Send menu prompt to the client
                    String menu = " \n \nMenu\n----------------\n1) UPLOAD \n2) DOWNLOAD \n3) QUERY \n4) QUIT \nEnter a command\n----------------\n";
                    communicationOut.println(menu);
                    // Read the user's selection
                    String request = communicationIn.readLine();

                    // Switch statement for every available command option
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
                            query();
                            break;
                        case "4":
                        case "QUIT":
                            communicationOut.println("connection on port #" + myClient.getPort() + " is closed");
                            flag = false;
                            break;
                        default:
                            break;
                    }
                }
            }
            // User authentication failed
            else {

                communicationOut.println("fail");
            }

        }

        catch (IOException e) {
        }

        // Handles the exception in which the client closes the connection
        catch (NullPointerException ex) {
            System.err.println("Client with address " + myClient.getRemoteSocketAddress().toString()
                    + " has closed connection unexpectly ");
            close_unexpectedly = true;
        } finally {
            try {
                // Close I/O objects
                communicationIn.close();
                communicationOut.close();
                os.close();
                // Display message to the server when specific client closes the connection
                if (!close_unexpectedly) {
                    System.err.println("Client with address " + myClient.getRemoteSocketAddress().toString()
                            + " has closed connection");
                }
            } catch (IOException e) {
            }
        }

    }

    // Sends list of files to be displayed when client makes a "query" request
    private void query() {

        communicationOut.println(MyServer.toStringAll());
        try {
            communicationIn.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
                String password = communicationIn.readLine();
                if (password.length() > 0) {
                    MyServer.addToList(new FileObject(myClientUname, filename, password));
                } else {
                    MyServer.addToList(new FileObject(myClientUname, filename));
                }
                int bytesRead;
                int currentTot = 0;
                byte[] bytearray = new byte[filesize];

                // Variables for server input and stream objects that write to a local file
                InputStream is = myClient.getInputStream();
                String file_path = getFile_Path("server_storage", filename);
                FileOutputStream fos = new FileOutputStream(file_path);
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
                System.out.println(communicationIn.readLine());

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
            } else {
                communicationOut.println("Ok(200)");

                // File exists
                // Send back to user

                // SECURITY

                boolean flag = false;
                String pass = MyServer.getPass(filename);
                if (pass.length() != 0) {
                    communicationOut.println("protected");
                    String pwd = communicationIn.readLine(); // user inputted password
                    if (pwd.equals(pass)) {
                        communicationOut.println("valid");
                        flag = true;
                    } else {
                        communicationOut.println("invalid");
                        flag = false;
                    }
                } else {
                    communicationOut.println("public");
                    flag = true;

                }
                if (flag == true) {

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

        }

        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param filename
     * @return File
     */
    public File getFile(String filename) {
        Path currentRelativePath = Paths.get("");
        Path currentDir = currentRelativePath.toAbsolutePath();
        String subdirectory = /*"NetworkingAss" + File.separatorChar + */"server_storage";
        String subDir_And_Filename = subdirectory + File.separatorChar + filename;
        Path filepath = currentDir.resolve(subDir_And_Filename);
        File transferfile = filepath.toFile();
        return transferfile;
    }

    /**
     * @param subdirectory
     * @param filename
     * @return String
     */
    public String getFile_Path(String subdirectory, String filename) {
        Path currentRelativePath = Paths.get("");
        Path currentDir = currentRelativePath.toAbsolutePath();
        String subDir_And_Filename = /*"NetworkingAss" + File.separatorChar + */subdirectory + File.separatorChar
                + filename; // subdirectory + File.separatorChar + filename;
        Path filepath = currentDir.resolve(subDir_And_Filename);
        return filepath.toString();
    }

}