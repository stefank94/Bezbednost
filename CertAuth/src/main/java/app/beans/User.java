package app.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

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

	@OneToMany(mappedBy = "user")
	protected List<CertificateSigningRequest> requests;

	@OneToMany(mappedBy = "user")
	protected List<Certificate> certificates;

	public User() {
		certificates = new ArrayList<>();
		requests = new ArrayList<>();
	}

	public abstract String getRole();

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

	public List<CertificateSigningRequest> getRequests() {
		return requests;
	}

	public void setRequests(List<CertificateSigningRequest> requests) {
		this.requests = requests;
	}

	public List<Certificate> getCertificates() {
		return certificates;
	}

	public void setCertificates(List<Certificate> certificates) {
		this.certificates = certificates;
	}
}
