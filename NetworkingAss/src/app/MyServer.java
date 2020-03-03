package app;

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
    public static LinkedList<FileObject> list=new LinkedList<FileObject>();

    public static void main(String[] args) throws IOException {
        // establishes a connection on the connection port called
        // connectionServerSocket.
        storeUsersAndPassword();
        fillList();

        ServerSocket connectionServerSocket = new ServerSocket(PORT);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                // System.out.print("\033[H\033[2J");  
                // System.out.flush(); 
                System.out.print("\nServer was shutdown!\n");
                
            }
            });

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

    // reading usersAndPasswords.txt
    public static void storeUsersAndPassword() {
        try {
            usersAndPass = new HashMap<String, String>();
            //System.out.println("fak"+getFile_Path("server_setup","users.txt"));
            Scanner sc = new Scanner(new FileReader(getFile_Path("server_setup","users.txt")));
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

    public static void fillList() {
        try {
            Scanner sc = new Scanner(new FileReader(getFile_Path("server_setup","ExistingFiles.txt")));
            while (sc.hasNext()) {
                String line = sc.nextLine();
                String temp[] = line.split("#");
                if (temp.length==3){
                    list.add(new FileObject(temp[0], temp[1], temp[2]));
                }
                else{
                    list.add(new FileObject(temp[0], temp[1]));
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }


    public static String getFile_Path(String subdirectory, String filename) {
        Path currentRelativePath = Paths.get("");
        Path currentDir = currentRelativePath.toAbsolutePath();
        String subDir_And_Filename = "NetworkingAss"+ File.separatorChar +subdirectory + File.separatorChar + filename;
        Path filepath = currentDir.resolve(subDir_And_Filename);
        return filepath.toString();
    }

    public static void writeList(){
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(getFile_Path("server_setup","ExistingFiles.txt")));
            for (FileObject f : list) {
                pw.println(f.toString());
            }
            pw.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static void addToList(FileObject f){
        list.add(f);
    }

    public static String getPass(String filename){
        for (FileObject f : list) {
            if(f.getFileName().equals(filename)){
                return f.getPassword();
            }
        }
        return "";
    }

    public static String toStringAll(){
        String x = "";
        for (FileObject f : list) {
            x+=f.prettyToString()+"###";
            
        }
        return x;
    }

}