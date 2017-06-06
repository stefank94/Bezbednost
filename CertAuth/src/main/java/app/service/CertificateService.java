package app.service;

import app.beans.Certificate;
import app.beans.CertificateAuthority;
import app.beans.CertificateData;
import app.beans.CertificateSigningRequest;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public interface CertificateService {

    Certificate generateCertificate(CertificateAuthority cA, CertificateSigningRequest cr);

}
