package app.dto;

public class CertificateAuthorityDTO extends AbstractEntityDTO {

    protected int issuer; // id

    protected CertificateDTO certificate;

    public CertificateAuthorityDTO() { }

    public int getIssuer() {
        return issuer;
    }

    public void setIssuer(int issuer) {
        this.issuer = issuer;
    }

    public CertificateDTO getCertificate() {
        return certificate;
    }

    public void setCertificate(CertificateDTO certificate) {
        this.certificate = certificate;
    }
}
