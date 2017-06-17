package app.dto;

import app.beans.CertificateAuthority;

public class CertificateAuthorityDTO extends AbstractEntityDTO {

    protected int issuer; // id
    protected CertificateDTO certificate;
    protected CertificateAuthority.CARole caRole;
    protected CRLInformationDTO crlInformation;

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

    public CertificateAuthority.CARole getCaRole() {
        return caRole;
    }

    public void setCaRole(CertificateAuthority.CARole caRole) {
        this.caRole = caRole;
    }

    public CRLInformationDTO getCrlInformation() {
        return crlInformation;
    }

    public void setCrlInformation(CRLInformationDTO crlInformation) {
        this.crlInformation = crlInformation;
    }
}
