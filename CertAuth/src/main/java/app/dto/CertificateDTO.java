package app.dto;

import java.util.Date;

public class CertificateDTO extends AbstractEntityDTO {

    protected int issuer; // id
    protected CertificateDataDTO certificateData;
    protected String user; // email
    protected Date validFrom;
    protected Date validTo;

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
}