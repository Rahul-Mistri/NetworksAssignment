

public class FileObject {

    /**
     * Object that contains the owner name, file name and password
     * This object type will be used in a list for queries so that "query" commands can be processed more efficiently 
     * Than reading from the filesystem for every "query" request 
     */
    private String owner;
    private String fileName;
    private String password = "";

    /**
     * Constructor for the file object
     * @param owner
     * @param fileName
     */
    
    public FileObject(String owner, String fileName){
        this(owner, fileName, "");
    }
    /**
     * Overloaded construcor for the file object
     * @param owner
     * @param fileName
     * @param password
     */
    public FileObject(String owner, String fileName, String password){
        this.owner=owner;
        this.fileName= fileName;
        this.password=password;
    }

    
    /** 
     * Get owner
     * * @return String
     */
    public String getOwner(){
        return owner;
    }

    
    /** 
     * Get filename
     * @return String
     */
    public String getFileName(){
        return fileName;
    }

    
    /** 
     * Get password
     * @return String
     */
    public String getPassword(){
        return password;
    }

    
    /** 
     * Get public state of file
     * @return boolean
     */
    public boolean isPublic(){
        return password.length()==0;
    }

    
    /** 
     * Compares 2 passwords and returns true if they match
     * @param pwd
     * @return boolean
     */
    public boolean match(String pwd){
        return pwd.equals(password);
    }

    
    /** 
     * @return String
     */
    public String toString(){
        return (owner+"#"+fileName+"#"+password);
    }

    
    /** 
     * toString that displays a permission icon next to filename
     * @return String
     */
    public String prettyToString(){
        String x = "\uD83C\uDF10";
        if (password.length()>0)
        {
            x = "\uD83D\uDD12";
        }
        return (x+"\t\t\t"+fileName+"\t\t\t"+owner);
    }

    
}