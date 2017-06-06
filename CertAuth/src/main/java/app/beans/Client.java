package app.beans;

import javax.persistence.Entity;

@Entity
public class Client extends User {

    private static final String ROLE = "CLIENT";

    public Client() { }

    @Override
    public String getRole() {
        return ROLE;
    }
}
