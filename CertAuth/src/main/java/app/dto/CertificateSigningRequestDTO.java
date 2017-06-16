package app.dto;

import app.beans.CertificateSigningRequest;
import java.util.Date;

public class CertificateSigningRequestDTO extends AbstractEntityDTO {

    protected CertificateDataDTO certificateData;

    protected String user; // email

    protected Date date;

    protected CertificateSigningRequest.CSRState state;

    protected int certificate; // id


    public CertificateSigningRequestDTO() { }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public CertificateSigningRequest.CSRState getState() {
        return state;
    }

    public void setState(CertificateSigningRequest.CSRState state) {
        this.state = state;
    }

    public int getCertificate() {
        return certificate;
    }

    public void setCertificate(int certificate) {
        this.certificate = certificate;
    }
}
