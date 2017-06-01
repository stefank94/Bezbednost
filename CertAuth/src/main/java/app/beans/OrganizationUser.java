package app.beans;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class OrganizationUser extends User {
	
	@Column(name = "name", nullable = false)
	protected String name;
	
	@Column(name = "address", nullable = false)
	protected String address;
	
	@Column(name = "country", nullable = false)
	protected String country;
	
	
	public OrganizationUser() { }


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}
	
	

}
