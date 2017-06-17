package app.dto;

import java.util.Date;

public class RevocationDTO extends AbstractEntityDTO {

    protected int certificate; // id
    protected boolean fullyRevoked;
    protected String reason;
    protected Date revocationDate;
    protected Date invalidityDate;

    public RevocationDTO() { }

    public int getCertificate() {
        return certificate;
    }

    public void setCertificate(int certificate) {
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

    public Date getInvalidityDate() {
        return invalidityDate;
    }

    public void setInvalidityDate(Date invalidityDate) {
        this.invalidityDate = invalidityDate;
    }
}
