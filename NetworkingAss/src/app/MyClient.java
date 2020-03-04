package app;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MyClient {

    // Setting the constant connection port
    private static final int SERVERPORT = 6666;
    // setting the constant ip of the server
    private static final String SERVERIP = "196.47.241.137";  //196.42.87.87

    /**
     * @param args
     */
    // private static final String SERVERIP = "localhost";

    public static void main(String[] args) {
        try {

            // Create socket to connect to server with parameterized server address and port
            Socket connectionSocket = new Socket(SERVERIP, SERVERPORT);

            // Creates objects for server/client communication, I/O and file writing
            // purposes
            BufferedReader communicationIn = new BufferedReader(
                    new InputStreamReader(connectionSocket.getInputStream()));
            
            BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));
            
            PrintWriter communicationOut = new PrintWriter(connectionSocket.getOutputStream(), true);
            OutputStream os = connectionSocket.getOutputStream();

             communicationOut = new PrintWriter(connectionSocket.getOutputStream(), true);

            // Display server's authentication prompt
            System.out.print(communicationIn.readLine());
            // Read username
            String userName;
            userName = fromUser.readLine();
            communicationOut.println(userName);
            // Display password prompt
            String passwordPrompt = (communicationIn.readLine());
            System.out.println(passwordPrompt);
            // Read in password while hiding it in the cli
            Console console = System.console();
            char[] pwd = console.readPassword();
            //String final_password = new String(pwd);
            // Send password to the server
            communicationOut.println(pwd);
            // Get authentication result
            String state = communicationIn.readLine();
            // Proceed with requests if username and password were succesful
            if (state.equals("success")) {

                // Run continuos command loop until user selects "quit" option
                String userInput = "";
                Boolean flag = true;
                while (flag) {

                    // Create input/output variables for every new command
                    communicationIn = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    fromUser = new BufferedReader(new InputStreamReader(System.in));
                    communicationOut = new PrintWriter(connectionSocket.getOutputStream(), true);

                    // Reading and displaying servers menu prompt
                    String cLine = communicationIn.readLine();
                    System.out.println(cLine);

                    while (!(cLine.equals(""))) {

                        cLine = communicationIn.readLine();
                        System.out.println(cLine);

                    }

                    // Read users command option
                    userInput = fromUser.readLine();

                    // Sending the command request to the server.
                    communicationOut.println(userInput);

                    // Switch statement for every available command option
                    switch (userInput) {
                        case "1":
                        case "UPLOAD":
                            upload(communicationIn, communicationOut, fromUser, os);
                            break;

                        case "2":
                        case "DOWNLOAD":
                            download(communicationIn, communicationOut, fromUser, connectionSocket);
                            break;

                        case "3":
                        case "QUERY":
                            makequery(communicationIn,communicationOut);
                            break;

                        case "4":
                        case "QUIT":
                            System.out.println(communicationIn.readLine());
                            flag = false;
                            break;

                    }

                }

            }
            // User authentication failed
            else {
                System.out.println("Invalid User name and password \nExiting...");
            }
            // closing the communication socket.
            connectionSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
    }
/**
     * List query method that prints all available files to be downloaded
     * 
     * @param communicationIn
     */
private static void makequery(BufferedReader communicationIn, PrintWriter communicationOut){
    try {
        String line = communicationIn.readLine();
        String temp[]=line.split("###");
        for (String st : temp) {
            System.out.println(st);
        }
        communicationOut.println("query done");
    } catch (Exception e) {
        
    }
}

    /**
     * Uploads a user's file to the servers file system
     * 
     * @param communicationIn
     * @param communicationOut
     * @param fromUser
     * @param os
     */
    private static void upload(BufferedReader communicationIn, PrintWriter communicationOut, BufferedReader fromUser,
            OutputStream os) {

        try {

            // Ask for upload file on the client side
            System.out.println("Enter the filename");
            String filename = fromUser.readLine();

            // Find file in the users local directory
            File transferFile = getFile("client_uploads", filename);

            // File does not exist
            if (!transferFile.exists()) {
                // Display message to the user
                System.out.println("Error 404");

                // Notify message to the server
                communicationOut.println("Error 404");
            }

            // File exists
            // Upload file to server
            else {
                // Send filename to the server
                communicationOut.println(filename);
                // Send file length to the server for file reading purposes on the receivers
                // side
                communicationOut.println(transferFile.length());
                // Ask if the user wants to set a password for uploaded file
                System.out.print("Do you want to password protect your file? (Y/N): ");
                String ans = "" + fromUser.readLine().toUpperCase().charAt(0);
                String pwd = "";
                if (ans.equals("Y")) {
                    System.out.print("Enter file password: ");
                    pwd = fromUser.readLine();
                }
                // Send users selected option to the user
                communicationOut.println(pwd);

                // Transform file data into bytearray to read the file into byte "chunks"
                byte[] bytearray = new byte[(int) transferFile.length()];
                FileInputStream fin = new FileInputStream(transferFile);
                BufferedInputStream bin = new BufferedInputStream(fin);

                // Thread that syncrhonizes the "write" process on the sender's device and the
                // "read" process on the receivers device
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                // Read file into bytearray
                bin.read(bytearray, 0, bytearray.length);
                // Display progress information to the sender
                System.out.println("Sending Files...");

                // Write results to the server
                os.write(bytearray, 0, bytearray.length);
                os.flush();

                // Requires acknowledgment of client to synchronize their processes
                System.out.println(communicationIn.readLine());
                System.out.println("done");
                communicationOut.println("Client read file");
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Download a file from the servers file system
     * 
     * @param communicationIn
     * @param communicationOut
     * @param fromUser
     * @param connectionSocket
     */
    private static void download(BufferedReader communicationIn, PrintWriter communicationOut, BufferedReader fromUser,
            Socket connectionSocket) {

        try {

            // Read the servers filename request and print to user
            String service_file_request = communicationIn.readLine();
            System.out.print(service_file_request);

            // Record the user's requested file and send to server
            String filename = fromUser.readLine();
            communicationOut.println(filename);

            // Retrieve the fetching result (found/notFound)
            String server_header = communicationIn.readLine();

            // Download file or Display 404 message

            // Not found action
            if (server_header.equalsIgnoreCase("Error 404")) {
                System.out.println("Error 404");

            }

            // Download incoming file
            else {
                // Read if file privacy is public or protected

                // Can be protected or public
                String privacy = communicationIn.readLine();
                if (privacy.equals("protected")) {
                    System.out.print("Enter file password: ");
                    String pass = fromUser.readLine();
                    communicationOut.println(pass);
                    String validity = communicationIn.readLine();
                    if (validity.equals("invalid")) {
                        throw new Exception("Incorrect password...\nExiting download session");

                    }
                }

                // Setup to read the bytestream
                int filesize = Integer.parseInt(communicationIn.readLine());
                int bytesRead;
                int currentTot = 0;
                byte[] bytearray = new byte[filesize];

                // Variables for server input and stream objects that write to a local file
                InputStream is = connectionSocket.getInputStream();
                FileOutputStream fos = new FileOutputStream(getFile_Path("client_downloads", filename));
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                // Read the first byte stream
                bytesRead = is.read(bytearray, 0, bytearray.length);
                currentTot = bytesRead;
                char[] animationChars = new char[] { '|', '/', '-', '\\' };
                int i = 0;

                // Read the server's remaining byte streams in chunks
                do {
                    // Reads the bytestream and adds it to the byteArray
                    bytesRead = is.read(bytearray, currentTot, (bytearray.length - currentTot));
                    System.out.print("Downloading: " + (Math.round(((currentTot + 0.0) / bytearray.length) * 100))
                            + "% " + animationChars[i % 4] + "\r");
                    i++;
                    try {
                        Thread.sleep((100));
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    // Updates the remaining bytes to be read
                    if (bytesRead > 0)
                        currentTot += bytesRead;
                } while (bytesRead > 0);

                // Let user know about the download's progress
                System.out.println("Downloading: Done!          ");

                // Write the locally stored byte streams into client file
                bos.write(bytearray, 0, currentTot);
                bos.flush();

                // Close the filewriting outputstream object
                bos.close();

                // Send acknowledgement to server to synchronize their progress
                communicationOut.println("Client has downloaded file");
                // } //IF THROW NOT WORKING
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        }
    }

    /**
     * Returns the file of a given file with a given subdirectory (underneath the
     * root dir)
     * 
     * @param subdirectory
     * @param filename
     * @return File
     */
    public static File getFile(String subdirectory, String filename) {
        Path currentRelativePath = Paths.get("");
        Path currentDir = currentRelativePath.toAbsolutePath();
        String subDir_And_Filename = "NetworkingAss" + File.separatorChar + subdirectory + File.separatorChar
                + filename;
        Path filepath = currentDir.resolve(subDir_And_Filename);
        File transferfile = filepath.toFile();
        return transferfile;
    }

    /**
     * Returns the path of a given file with a given subdirectory (underneath the
     * root dir)
     * 
     * @param subdirectory
     * @param filename
     * @return String
     */
    public static String getFile_Path(String subdirectory, String filename) {
        Path currentRelativePath = Paths.get("");
        Path currentDir = currentRelativePath.toAbsolutePath();
        String subDir_And_Filename = "NetworkingAss" + File.separatorChar + subdirectory + File.separatorChar
                + filename; // subdirectory + File.separatorChar + filename;
        Path filepath = currentDir.resolve(subDir_And_Filename);
        return filepath.toString();
    }
}
