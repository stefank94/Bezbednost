package app.service;

import app.beans.CertificateAuthority;
import app.beans.CertificateData;
import app.exception.EntityNotFoundException;

public interface CAService {

    CertificateAuthority getRootCA();

    CertificateAuthority generateCertificateAuthority(CertificateAuthority issuer, CertificateData data);

    CertificateAuthority generateRootCA(CertificateData data);

    CertificateAuthority findById(int id) throws EntityNotFoundException;

}
