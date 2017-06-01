package app.beans;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class PersonUser extends User {

	private static final String ROLE = "PERSON";

	@Column(name = "name", nullable = false)
	protected String name;
	
	@Column(name = "lastname", nullable = false)
	protected String lastName;
	
	
	public PersonUser() { }


	@Override
	public String getRole(){
		return ROLE;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	
	
}
