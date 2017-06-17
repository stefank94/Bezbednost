package app.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Date;

@Entity
public class CRLInformation extends AbstractEntity {

    @OneToOne
    @JoinColumn(name = "ca", referencedColumnName = "id")
    protected CertificateAuthority ca;

    @Column(name = "currentissued")
    protected Date currentIssued;

    @Column(name = "nextissued")
    protected Date nextIssued;

    //protected String cronFrequency;       ????
    //protected String nextCronFrequency;  // for when the frequency is changed to a longer time, and we still need to
                                           // issue the next CRL by nextIssued. The new frequency takes effect after
                                           // the next CRL is issued.
    @Column(name = "crlfilename")
    protected String crlFilename;

    @Column(name = "crlnumber")
    protected int crlNumber;


    public CRLInformation() { }


    public int incrementAndReturnNumber(){
        return ++this.crlNumber;
    }


    public CertificateAuthority getCa() {
        return ca;
    }

    public void setCa(CertificateAuthority ca) {
        this.ca = ca;
    }

    public Date getCurrentIssued() {
        return currentIssued;
    }

    public void setCurrentIssued(Date currentIssued) {
        this.currentIssued = currentIssued;
    }

    public Date getNextIssued() {
        return nextIssued;
    }

    public void setNextIssued(Date nextIssued) {
        this.nextIssued = nextIssued;
    }

    public String getCrlFilename() {
        return crlFilename;
    }

    public void setCrlFilename(String crlFilename) {
        this.crlFilename = crlFilename;
    }

    public int getCrlNumber() {
        return crlNumber;
    }

    public void setCrlNumber(int crlNumber) {
        this.crlNumber = crlNumber;
    }
}
