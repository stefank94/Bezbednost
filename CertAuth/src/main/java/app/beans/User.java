package app.beans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends AbstractEntity {
	
	@Column(name = "email", unique = true, nullable = false)
	protected String email;
	
	@Column(name = "password", nullable = false)
	protected String password;
	
	@Column(name = "salt", nullable = false)
	protected String salt;
	
	@Column(name = "signupdate", nullable = false)
	protected Date signupDate;
	
	
	public User() { }


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getSalt() {
		return salt;
	}


	public void setSalt(String salt) {
		this.salt = salt;
	}


	public Date getSignupDate() {
		return signupDate;
	}


	public void setSignupDate(Date signupDate) {
		this.signupDate = signupDate;
	}
	
	


}
