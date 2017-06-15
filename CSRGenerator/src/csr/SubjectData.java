package csr;

public class SubjectData {
	
	private String commonName, givenName, surname, countryCode, organization, organizationalUnit, email;
	
	public SubjectData() {}
	
	

	public SubjectData(String commonName, String givenName, String surname, String countryCode, String organization,
			String organizationalUnit, String email) {
		super();
		this.commonName = commonName;
		this.givenName = givenName;
		this.surname = surname;
		this.countryCode = countryCode;
		this.organization = organization;
		this.organizationalUnit = organizationalUnit;
		this.email = email;
	}



	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getOrganizationalUnit() {
		return organizationalUnit;
	}

	public void setOrganizationalUnit(String organizationalUnit) {
		this.organizationalUnit = organizationalUnit;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

}
