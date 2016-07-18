package cz.schrek.skladnik.model;

/**
 * Created by ondra on 16. 7. 2016.
 */
public class UserAccount {

    private String login;
    private String password;
    private String email;

    public UserAccount() {
    }

    public UserAccount(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
