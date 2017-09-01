package app.x509;

import app.beans.CertificateAuthority;
import app.beans.CertificateData;

import java.security.PrivateKey;

public class CACertificateBuilder extends CertificateBuilder {

    public CACertificateBuilder(CertificateData data, int durationInMonths, int serialNumber, CertificateAuthority issuer){
        super();
        this.subjectData = data;
        this.duration = durationInMonths;
        this.serialNumber = serialNumber;
        this.issuer = issuer;
    }

    public CACertificateBuilder(CertificateData data, int durationInMonths, int serialNumber, PrivateKey privateKey){
        super();
        this.subjectData = data;
        this.duration = durationInMonths;
        this.privateKey = privateKey;
        this.serialNumber = serialNumber;
    }

}
