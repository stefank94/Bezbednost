package app.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
public class Revocation extends AbstractEntity {

    @OneToOne(mappedBy = "revocation")
    protected Certificate certificate;

    @Column(name = "fullyrevoked", nullable = false)
    protected boolean fullyRevoked; // fully revoked or just on hold

    @Column(name = "reason", nullable = false)
    protected String reason;

    @Column(name = "revocationdate", nullable = false)
    protected Date revocationDate;

    @Column(name = "invaliditydate")
    protected Date invalidityDate;


    static {
        final String[] valids = {"unspecified", "keyCompromise", "cACompromise", "affiliationChanged",
            "superseded", "cessationOfOperation", "certificateHold", "removeFromCRL", "privilegeWithdrawn",
            "aACompromise"};
        validReasons = Arrays.asList(valids);
    }

    @Transient
    protected static List<String> validReasons;


    public Revocation() { }


    public int getReasonCode(){
        return validReasons.indexOf(reason);
    }


    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public boolean isFullyRevoked() {
        return fullyRevoked;
    }

    public void setFullyRevoked(boolean fullyRevoked) {
        this.fullyRevoked = fullyRevoked;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getRevocationDate() {
        return revocationDate;
    }

    public void setRevocationDate(Date revocationDate) {
        this.revocationDate = revocationDate;
    }

    public static List<String> getValidReasons() {
        return validReasons;
    }

    public static void setValidReasons(List<String> validReasons) {
        Revocation.validReasons = validReasons;
    }

    public Date getInvalidityDate() {
        return invalidityDate;
    }

    public void setInvalidityDate(Date invalidityDate) {
        this.invalidityDate = invalidityDate;
    }
}
