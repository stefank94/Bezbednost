package app.dto;

import java.util.Date;

public class CertificateDTO extends AbstractEntityDTO {

    protected int issuer; // id
    protected CertificateDataDTO certificateData;
    protected int ca; // id
    protected String user; // email
    protected Date validFrom;
    protected Date validTo;
    protected String cerFileName;
    protected RevocationDTO revocation;
    protected CertStatus status;

    public enum CertStatus{
        VALID, EXPIRED, REVOKED, ON_HOLD
    }

    public CertificateDTO() { }

    public int getIssuer() {
        return issuer;
    }

    public void setIssuer(int issuer) {
        this.issuer = issuer;
    }

    public CertificateDataDTO getCertificateData() {
        return certificateData;
    }

    public void setCertificateData(CertificateDataDTO certificateData) {
        this.certificateData = certificateData;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public int getCa() {
        return ca;
    }

    public void setCa(int ca) {
        this.ca = ca;
    }

    public String getCerFileName() {
        return cerFileName;
    }

    public void setCerFileName(String cerFileName) {
        this.cerFileName = cerFileName;
    }

    public RevocationDTO getRevocation() {
        return revocation;
    }

    public void setRevocation(RevocationDTO revocation) {
        this.revocation = revocation;
    }

    public CertStatus getStatus() {
        return status;
    }

    public void setStatus(CertStatus status) {
        this.status = status;
    }
}
