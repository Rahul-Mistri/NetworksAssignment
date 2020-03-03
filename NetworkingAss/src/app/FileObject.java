package app;

public class FileObject {

    private String owner;
    private String fileName;
    private String password = "";
    
    public FileObject(String owner, String fileName){
        this(owner, fileName, "");
    }
    public FileObject(String owner, String fileName, String password){
        this.owner=owner;
        this.fileName= fileName;
        this.password=password;
    }

    public String getOwner(){
        return owner;
    }

    public String getFileName(){
        return fileName;
    }

    public String getPassword(){
        return password;
    }

    public boolean isPublic(){
        return password.length()==0;
    }

    public boolean match(String pwd){
        return pwd.equals(password);
    }

    public String toString(){
        return (owner+"#"+fileName+"#"+password);
    }

    public String prettyToString(){
        String x = "\uD83C\uDF10";
        if (password.length()>0)
        {
            x = "\uD83D\uDD12";
        }
        return (x+"\t\t\t"+fileName+"\t\t\t"+owner);
    }

    
}