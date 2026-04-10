package models;

import java.util.List;

public class LoginData {
    public String validEmail;
    public String validPassword;
    public List<InvalidLogin> invalidLogins;

    public static class InvalidLogin {
        public String email;
        public String password;
        public String testCase;

        public InvalidLogin(String email, String password, String testCase) {
            this.email = email;
            this.password = password;
            this.testCase = testCase;
        }
    }
}
