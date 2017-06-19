package app.beans;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CertificateAuthority extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "issuer")
    protected CertificateAuthority issuer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "certificate", referencedColumnName = "id")
    protected Certificate certificate;

    @Column(name = "keystorefilename")
    protected String keyStoreFileName;

    @Column(name = "keystorepassword")
    protected String keyStorePassword;

    @Column(name = "keystorealias")
    protected String keyStoreAlias;

    @Column(name = "privatekeypassword")
    protected String privateKeyPassword;

    public enum CARole {
        ROOT, INTERMEDIATE, CA_ISSUER, HTTPS_ISSUER, MAIL_ISSUER, DOC_SIGN_ISSUER
    }

    @Column(name = "carole", nullable = false)
    protected CARole caRole;

    @OneToMany(mappedBy = "issuer")
    protected List<Certificate> issuedCertificates;

    @OneToOne(mappedBy = "ca", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected CRLInformation crlInformation;

    @Column(name = "durationofissuedcertificates")
    protected int durationOfIssuedCertificates; // duration in MONTHS

    @Column(name = "duration")
    protected int duration; // duration of this CA's certificate in YEARS


    public CertificateAuthority() {
        issuedCertificates = new ArrayList<>();
    }

    public CertificateAuthority getIssuer() {
        return issuer;
    }

    public void setIssuer(CertificateAuthority issuer) {
        this.issuer = issuer;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
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

    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public void setPrivateKeyPassword(String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }

    public CARole getCaRole() {
        return caRole;
    }

    public void setCaRole(CARole caRole) {
        this.caRole = caRole;
    }

    public List<Certificate> getIssuedCertificates() {
        return issuedCertificates;
    }

    public void setIssuedCertificates(List<Certificate> issuedCertificates) {
        this.issuedCertificates = issuedCertificates;
    }

    public CRLInformation getCrlInformation() {
        return crlInformation;
    }

    public void setCrlInformation(CRLInformation crlInformation) {
        this.crlInformation = crlInformation;
    }

    public int getDurationOfIssuedCertificates() {
        return durationOfIssuedCertificates;
    }

    public void setDurationOfIssuedCertificates(int durationOfIssuedCertificates) {
        this.durationOfIssuedCertificates = durationOfIssuedCertificates;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
