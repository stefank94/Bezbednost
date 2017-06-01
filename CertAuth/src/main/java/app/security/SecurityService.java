package app.security;

import app.beans.User;

public interface SecurityService {

    User getLoggedInUser();

    void autologin(String username, String password);

}
