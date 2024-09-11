package util;

public abstract class User {
    protected String username;
    protected String os;

    public User(String username){
        this.username = username;
        os = System.getProperty("os.name");
    }

    public void setUser(String username){
        this.username =  username;
        os = System.getProperty("os.name");
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString(){
        return "User {" +
                "\n\tusername=" + username +
                "\n\tos=" + os +
                "\n}";
    }
}
