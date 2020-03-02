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
    private static final String SERVERIP = "196.47.241.137";
    // private static final String SERVERIP = "localhost";
     

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
            BufferedReader communicationIn;
            BufferedReader fromUser;
            PrintWriter communicationOut;
            OutputStream os = connectionSocket.getOutputStream();

            // TEMPORARY BufferedReader fromUser will get an input from the user.
            // PrintWriter communicationOut is used to send messages to the server.
            // System.out.println(menu);

            // continous loop
            String userInput = "";
            Boolean flag = true;
            while (flag) {

                communicationIn = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                fromUser = new BufferedReader(new InputStreamReader(System.in));
                communicationOut = new PrintWriter(connectionSocket.getOutputStream(), true);

                // user input taken in.
                String cLine = communicationIn.readLine();
                System.out.println(cLine);

                while (!(cLine.equals(""))) {

                    cLine = communicationIn.readLine();
                    System.out.println(cLine);

                }

                // Taking user input
                userInput = fromUser.readLine();

                // sending the request to the server.
                communicationOut.println(userInput);

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
                        // do something
                        break;

                    case "4":
                    case "QUIT":
                        System.out.println(communicationIn.readLine());
                        flag = false;
                        break;

                }

            }

            // closing the communication socket.
            connectionSocket.close();

        } catch (Exception e) {
            // System.out.println(e);
        }
    }

    private static void upload(BufferedReader communicationIn, PrintWriter communicationOut, BufferedReader fromUser,
            OutputStream os) {

        try {
            //Ask for upload file on the client side
            System.out.println("Enter the filename");
            String filename = fromUser.readLine();

            //Find file in the users loal directory
            String directory_path = System.getProperty("user.dir") + "/NetworkingAss/";
            File transferFile = new File(directory_path + filename);
            System.out.println("File to be uploaded"+directory_path+filename);

            //File does not exist
            if(!transferFile.exists())
            {
                //Display message to the user
                System.out.println("Error 404");

                //Notify message to the server
                communicationOut.println("Error 404");
            }

            //File exists
            //Upload toserver
            else
            {
                //Send filename to the server
                communicationOut.println(filename);
                //Sedn file length to the server
                communicationOut.println(transferFile.length());
                
                //Transform file data into bytearray
                byte[] bytearray = new byte[(int) transferFile.length()];
                FileInputStream fin = new FileInputStream(transferFile);
                BufferedInputStream bin = new BufferedInputStream(fin);

                try {
                    Thread.sleep(1000);
                    } 
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                
                bin.read(bytearray, 0, bytearray.length);
                System.out.println("Sending Files...");
                
                //Write results to the server
                os.write(bytearray, 0, bytearray.length);
                os.flush();
                
                //Requires acknowledgment of client to synchronize their processes
                communicationIn.readLine();
                System.out.println("done");

                //Starts to send the file content

            }



        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void download(BufferedReader communicationIn, PrintWriter communicationOut, BufferedReader fromUser,
            Socket connectionSocket) {

        try {

            // Read the servers filename request and print to user
            String service_file_request = communicationIn.readLine();
            System.out.print(service_file_request);

            // Record the user's requested file and send to server
            String filename = fromUser.readLine();
            communicationOut.println(filename);

            // Download file or Display 404 message
            String server_header = communicationIn.readLine();

            // Not found action
            if (server_header.equalsIgnoreCase("Error 404")) {
                System.out.println("Error 404");

            }
            // Download incoming file
            else {

                // Setup to read the bytestream
                int filesize = Integer.parseInt(communicationIn.readLine());
                int bytesRead;
                int currentTot = 0;
                byte[] bytearray = new byte[filesize];

                // Variables for server input and stream objects that write to a local file
                InputStream is = connectionSocket.getInputStream();
                FileOutputStream fos = new FileOutputStream("copy.txt");
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
                communicationOut.println("Client has downloaded file");
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public File getFile(String filename)
    {
        Path currentRelativePath = Paths.get("");
        Path currentDir = currentRelativePath.toAbsolutePath();
        String subdirectory = "NetworkingAss";
        String subDir_And_Filename =  subdirectory + File.separatorChar + filename;
        Path filepath = currentDir.resolve(subDir_And_Filename);
        File transferfile = filepath.toFile();
        return transferfile;
    }
}