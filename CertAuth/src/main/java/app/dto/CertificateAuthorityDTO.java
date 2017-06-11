package app.dto;

public class CertificateAuthorityDTO extends AbstractEntityDTO {

    protected int issuer; // id

    protected CertificateDTO certificate;

    protected boolean bottomCA;

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

    public boolean isBottomCA() {
        return bottomCA;
    }

    public void setBottomCA(boolean bottomCA) {
        this.bottomCA = bottomCA;
    }
}
