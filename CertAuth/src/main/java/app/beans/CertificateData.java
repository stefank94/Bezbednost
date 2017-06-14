package app.beans;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class CertificateData extends AbstractEntity {

    @Column(name = "publickey", nullable = false, length = 2048)
    protected String publicKey;

    @Column(name = "keyalgorithm", nullable = false)
    protected String keyAlgorithm;

    @Column(name = "commonname", nullable = false)
    protected String commonName;

    @Column(name = "givenname", nullable = false)
    protected String givenName;

    @Column(name = "surname", nullable = false)
    protected String surname;

    @Column(name = "organization", nullable = false)
    protected String organization;

    @Column(name = "organizationalunit", nullable = false)
    protected String organizationalUnit;

    @Column(name = "countrycode", nullable = false)
    protected String countryCode;

    @Column(name = "emailaddress")
    protected String emailAddress;

    @Column(name = "serialNumber")
    protected int serialNumber;

    @Column(name = "isCA", nullable = false)
    protected boolean isCA;

    @Column(name = "subjectkeyidentifier")
    protected String subjectKeyIdentifier;

    public enum CertUsage{
        CA, HTTPS, MAIL, DOC_SIGN
    }

    @Column(name = "certusage", nullable = false)
    protected CertUsage certUsage;

    public CertificateData() { }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    public void setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean isCA() {
        return isCA;
    }

    public void setCA(boolean CA) {
        isCA = CA;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSubjectKeyIdentifier() {
        return subjectKeyIdentifier;
    }

    public void setSubjectKeyIdentifier(String subjectKeyIdentifier) {
        this.subjectKeyIdentifier = subjectKeyIdentifier;
    }

    public CertUsage getCertUsage() {
        return certUsage;
    }

    public void setCertUsage(CertUsage certUsage) {
        this.certUsage = certUsage;
    }
}
