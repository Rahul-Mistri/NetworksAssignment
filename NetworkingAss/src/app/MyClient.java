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

            BufferedReader communicationIn= new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));;
            BufferedReader fromUser= new BufferedReader(new InputStreamReader(System.in));;
            PrintWriter communicationOut= new PrintWriter(connectionSocket.getOutputStream(), true);;
            OutputStream os = connectionSocket.getOutputStream();

            // TEMPORARY BufferedReader fromUser will get an input from the user.
            // PrintWriter communicationOut is used to send messages to the server.
            
           
            /////////////START INCOMING
            communicationOut = new PrintWriter(connectionSocket.getOutputStream(), true);
            // System.out.println(menu);

            // authentication
            // username
            System.out.print(communicationIn.readLine());
            String userName;
            userName = fromUser.readLine();
            communicationOut.println(userName);
            // password
            String passwordPrompt = (communicationIn.readLine());
            System.out.print(passwordPrompt);
            //password = fromUser.readLine();
            Console console = System.console();
            char[] pwd= console.readPassword();
            String final_password = new String(pwd);
            System.out.println("Password is "+final_password);
            

            communicationOut.println(pwd);
            String state = communicationIn.readLine();
            if (state.equals("success")) {
                // continous loop

                




                 //BEGINNIING ORIGINAL

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
                    //END ORIGINAL

                    
                    //END INCOMING////////////////////

                    // user input taken in .
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
                        makequery(communicationIn);
                        break;

                    case "4":
                    case "QUIT":
                        System.out.println(communicationIn.readLine());
                        flag = false;
                        break;

                }

            }
            
           

        } else {
            System.out.println("Invalid User name and password \nExiting...");
        }
        // closing the communication socket.
        connectionSocket.close();
        

        
        
    }
    catch (Exception e) {
        // System.out.println(e);
    }
}


private static void makequery(BufferedReader communicationIn){
    try {
        String line = communicationIn.readLine();
        String temp[]=line.split("###");
        for (String st : temp) {
            System.out.println(st);
        }
    } catch (Exception e) {
        //TODO: handle exception
    }
}

    private static void upload(BufferedReader communicationIn, PrintWriter communicationOut, BufferedReader fromUser,
            OutputStream os) {

        try {
            // Ask for upload file on the client side
            System.out.println("Enter the filename");
            String filename = fromUser.readLine();

            // Find file in the users loal directory
            File transferFile = getFile(filename);

            // File does not exist
            if (!transferFile.exists()) {
                // Display message to the user
                System.out.println("Error 404");

                // Notify message to the server
                communicationOut.println("Error 404");
            }

            // File exists
            // Upload toserver
            else {
                // Send filename to the server
                communicationOut.println(filename);
                // Sedn file length to the server
                communicationOut.println(transferFile.length());
                //ask if want password
                System.out.print("Do you want to password protect your file? (Y/N): ");
                String ans = ""+fromUser.readLine().toUpperCase().charAt(0);
                String pwd = "";
                if (ans.equals("Y")){
                    System.out.print("Enter file password: ");
                    pwd = fromUser.readLine();
                }

                communicationOut.println(pwd);

                // Transform file data into bytearray
                byte[] bytearray = new byte[(int) transferFile.length()];
                FileInputStream fin = new FileInputStream(transferFile);
                BufferedInputStream bin = new BufferedInputStream(fin);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                bin.read(bytearray, 0, bytearray.length);
                System.out.println("Sending Files...");

                // Write results to the server
                os.write(bytearray, 0, bytearray.length);
                os.flush();

                // Requires acknowledgment of client to synchronize their processes
                communicationIn.readLine();
                System.out.println("done");

                // Starts to send the file content

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

                //SECURITY
                //boolean flag = true; //IF THROW NOT WORKING

                //read if file privacy is public or protected
                String privacy = communicationIn.readLine(); //protected or public
                if (privacy.equals("protected")) {
                    System.out.print("Enter file password: ");
                    String pass = fromUser.readLine();
                    communicationOut.println(pass);
                    String validity = communicationIn.readLine(); //valid or invalid
                    if (validity.equals("invalid")){
                        throw new Exception("Incorrect password...\nExiting download session");
                        //flag = false;
                    }
                } 

                //if (flag){ //IF THROW NOT WORKING

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
                    char[] animationChars =new char[]{'|','/','-','\\'};
                    int i=0;

                    // Read the server's remaining byte streams in chunks
                    do {
                        // Reads the bytestream and adds it to the byteArray
                        bytesRead = is.read(bytearray, currentTot, (bytearray.length - currentTot));
                        System.out.print("Downloading: "+ (Math.round(((currentTot+0.0)/bytearray.length)*100))+"% "+animationChars[i%4]+"\r");
                        i++;
                        try{
                            Thread.sleep((100));
                        }
                        catch(InterruptedException ex){
                            ex.printStackTrace();
                        }
                        // Updates the remaining bytes to be read
                        if (bytesRead > 0)
                            currentTot += bytesRead;
                    } while (bytesRead > 0);
                    System.out.println("Downloading: Done!          ");

                    // Write the locally stored byte streams into client file
                    bos.write(bytearray, 0, currentTot);
                    bos.flush();

                    // Close the filewriting outputstream object
                    bos.close();

                    // Send acknowledgement to server to synchronize their progress
                    communicationOut.println("Client has downloaded file");
                //} //IF THROW NOT WORKING
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        }
    }

    public static File getFile(String filename) {
        Path currentRelativePath = Paths.get("");
        Path currentDir = currentRelativePath.toAbsolutePath();
        String subdirectory = "NetworkingAss"+ File.separatorChar +"client_storage";
        String subDir_And_Filename = subdirectory + File.separatorChar + filename;
        Path filepath = currentDir.resolve(subDir_And_Filename);
        File transferfile = filepath.toFile();
        return transferfile;
    }
}
