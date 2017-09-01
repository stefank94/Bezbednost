package app.x509;

import app.beans.CertificateAuthority;
import app.beans.CertificateSigningRequest;

public class NonCACertificateBuilder extends CertificateBuilder {

    public NonCACertificateBuilder(CertificateSigningRequest csr, int serialNumber, CertificateAuthority issuer){
        super();
        this.subjectData = csr.getCertificateData();
        this.creator = csr.getUser();
        this.serialNumber = serialNumber;
        this.issuer = issuer;
    }

}
