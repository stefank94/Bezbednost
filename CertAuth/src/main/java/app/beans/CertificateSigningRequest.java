package app.beans;

import javax.persistence.*;
import java.security.PublicKey;
import java.util.Date;

@Entity
public class CertificateSigningRequest extends AbstractEntity {

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "certificatedata", nullable = false)
    protected CertificateData certificateData;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "id")
    protected User user;

    @Column(name = "date", nullable = false)
    protected Date date;

    public enum CSRState{
        REQUESTED, APPROVED, REJECTED
    }

    @Column(name = "state", nullable = false)
    protected CSRState state;

    @OneToOne
    @JoinColumn(name = "certificate", referencedColumnName = "id")
    protected Certificate certificate;

    public CertificateSigningRequest() { }

    public CertificateData getCertificateData() {
        return certificateData;
    }

    public void setCertificateData(CertificateData certificateData) {
        this.certificateData = certificateData;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CSRState getState() {
        return state;
    }

    public void setState(CSRState state) {
        this.state = state;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }
}
