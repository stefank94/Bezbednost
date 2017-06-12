package app.beans;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Certificate extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "issuer", referencedColumnName = "id")
    protected CertificateAuthority issuer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "certificatedata", nullable = false)
    protected CertificateData certificateData;

    @OneToOne(mappedBy = "certificate")
    protected CertificateAuthority ca;

    @ManyToOne
    @JoinColumn(name = "user")
    protected User user;

    @Column(name = "validfrom", nullable = false)
    protected Date validFrom;

    @Column(name = "validto", nullable = false)
    protected Date validTo;

    @Column(name = "keystorefilename")
    protected String keyStoreFileName;

    @Column(name = "keystorepassword")
    protected String keyStorePassword;

    @Column(name = "keystorealias")
    protected String keyStoreAlias;


    public Certificate() { }

    public CertificateAuthority getIssuer() {
        return issuer;
    }

    public void setIssuer(CertificateAuthority issuer) {
        this.issuer = issuer;
    }

    public CertificateData getCertificateData() {
        return certificateData;
    }

    public void setCertificateData(CertificateData certificateData) {
        this.certificateData = certificateData;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getKeyStoreFileName() {
        return keyStoreFileName;
    }

    public void setKeyStoreFileName(String keyStoreFileName) {
        this.keyStoreFileName = keyStoreFileName;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStoreAlias() {
        return keyStoreAlias;
    }

    public void setKeyStoreAlias(String keyStoreAlias) {
        this.keyStoreAlias = keyStoreAlias;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CertificateAuthority getCa() {
        return ca;
    }

    public void setCa(CertificateAuthority ca) {
        this.ca = ca;
    }
}
