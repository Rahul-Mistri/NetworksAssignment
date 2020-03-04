

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;

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
    // hashmap to store usernames and passwords
    public static HashMap<String, String> usersAndPass;
    // LinkedList to store DataObjects
    public static LinkedList<FileObject> list = new LinkedList<FileObject>();

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // INITIAL DATA LOADS

        // Loads User/Paswword hash map
        storeUsersAndPassword();
        // Load the server files into linked list
        fillList();

        // Establish connection
        ServerSocket connectionServerSocket = new ServerSocket(PORT);

        // Create server shutdown handler
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                // System.out.print("\033[H\033[2J");
                // System.out.flush();
                System.out.print("\nServer was shutdown!\n");
                writeList();
                
            }
        });

        // Loop that constantly listens for client connections
        while (true) {
            // Blocks here until client makes a connection request, then create connection
            // socket
            Socket connectionSocket = connectionServerSocket.accept();

            // Thread that handles client communication in a new thread by passing in
            // communication socket
            MyClientHandler myClientThread = new MyClientHandler(connectionSocket);

            // Adds the client to the array list -- future use
            myClients.add(myClientThread);

            // Executes the thread used for client data transfer
            pool.execute(myClientThread);
        }

    }

    /**
     * Loads the user names and passwords into hashmap
     */
    public static void storeUsersAndPassword() {
        try {
            usersAndPass = new HashMap<String, String>();
            Scanner sc = new Scanner(new FileReader(getFile_Path("server_setup", "users.txt")));
            while (sc.hasNext()) {
                String line = sc.nextLine();
                String temp[] = line.split(" ");
                usersAndPass.put(temp[0], temp[1]);
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    /**
     * Loads the server files stored on the machine in the linked list
     */

    public static void fillList() {
        try {
            Scanner sc = new Scanner(new FileReader(getFile_Path("server_setup", "ExistingFiles.txt")));
            while (sc.hasNext()) {
                String line = sc.nextLine();
                String temp[] = line.split("#");
                if (temp.length == 3) {
                    list.add(new FileObject(temp[0], temp[1], temp[2]));
                } else {
                    list.add(new FileObject(temp[0], temp[1]));
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    /**
     * Returns the path of a given file with a given subdirectory (underneath the
     * root dir)
     * 
     * @return String This returns the path
     * @param String subdirectory
     * @param String filename
     */

    public static void writeList(){
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(getFile_Path("server_setup", "ExistingFiles.txt")));
            for (FileObject f : list) {
                pw.println(f.toString());
            }
            pw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String getFile_Path(String subdirectory, String filename) {
        Path currentRelativePath = Paths.get("");
        Path currentDir = currentRelativePath.toAbsolutePath();
        String subDir_And_Filename = /*"NetworkingAss"+ File.separatorChar +*/subdirectory + File.separatorChar + filename;
        Path filepath = currentDir.resolve(subDir_And_Filename);
        return filepath.toString();
    }

    /**
     * Adds file objects to the linked used during runtime queries
     * 
     * @param f
     */
    public static void addToList(FileObject f){
        list.add(f);
    }

    /**
     * Gets the password that used during the upload of a specified filename in the
     * constructor
     * 
     * @param filename
     * @return String
     */
    public static String getPass(String filename) {
        for (FileObject f : list) {
            if (f.getFileName().equals(filename)) {
                return f.getPassword();
            }
        }
        return "";
    }

    /**
     * Prints out the files to the user during runtime queries
     * 
     * @return String
     */
    public static String toStringAll() {
        String x = "";
        for (FileObject f : list) {
            x += f.prettyToString() + "###";

        }
        return x;
    }

}