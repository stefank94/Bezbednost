package app.beans;

import javax.persistence.Entity;

@Entity
public class Admin extends User {

	private static final String ROLE = "ADMIN";

	public Admin() {

	}

	@Override
	public String getRole() {
		return ROLE;
	}

}
