package eu.mjelen.openshifter.api;

public class UsersRegular {

    private String username;
    private String password;
    private Boolean sudoer = false;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getSudoer() {
        return sudoer;
    }

    public void setSudoer(Boolean sudoer) {
        this.sudoer = sudoer;
    }

}
