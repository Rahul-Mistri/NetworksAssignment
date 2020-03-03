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
    private String myClientUname = "";
    private boolean close_unexpectedly = false;

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
                System.out.println("Client with address " + myClient.getRemoteSocketAddress().toString()
                        + " has opened connection");
                // continous loop
                myClientUname = uName;
                boolean flag = true;
                while (flag) {
                    // reads what the client sent.
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
                            query();
                            // System.out.println("Told me to QUERY");
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
            } else {
                communicationOut.println("fail");
            }
        } catch (IOException e) {
        } catch (NullPointerException ex) {
            System.err.println("Client with address " + myClient.getRemoteSocketAddress().toString()
                    + " has closed connection unexpectly ");
            close_unexpectedly = true;
        } finally {
            try {
                // closing bufferedReader and printWriter
                communicationIn.close();
                communicationOut.close();
                os.close();

                if (!close_unexpectedly) {
                    System.err.println("Client with address " + myClient.getRemoteSocketAddress().toString()
                            + " has closed connection");
                }
            } catch (IOException e) {
            }
        }

    }

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
                if (password.length()>0){
                    MyServer.addToList(new FileObject(myClientUname, filename,password));
                }
                else{
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
            }
            else{
                communicationOut.println("Ok(200)");
            
            // File exists
            // Send back to user


            //SECURITY
            
                boolean flag = false;
                String pass = MyServer.getPass(filename);
                if(pass.length()!=0){
                    communicationOut.println("protected");
                    String pwd = communicationIn.readLine(); //user inputted password
                    if (pwd.equals(pass)){
                        communicationOut.println("valid");
                        flag = true;
                    }
                    else{
                        communicationOut.println("invalid");
                        flag = false;
                    }
                }
                else{
                    communicationOut.println("public");
                    flag = true;

                }
                if(flag==true){

                    

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

    public File getFile(String filename) {
        Path currentRelativePath = Paths.get("");
        Path currentDir = currentRelativePath.toAbsolutePath();
        String subdirectory = "NetworkingAss"+ File.separatorChar +"server_storage";
        String subDir_And_Filename = subdirectory + File.separatorChar + filename;
        Path filepath = currentDir.resolve(subDir_And_Filename);
        File transferfile = filepath.toFile();
        return transferfile;
    }

    public String getFile_Path(String subdirectory,String filename) {
        Path currentRelativePath = Paths.get("");
        Path currentDir = currentRelativePath.toAbsolutePath();
        String subDir_And_Filename = "NetworkingAss"+ File.separatorChar + subdirectory + File.separatorChar+ filename; //subdirectory + File.separatorChar + filename;
        Path filepath = currentDir.resolve(subDir_And_Filename);
        return filepath.toString();
    }

}