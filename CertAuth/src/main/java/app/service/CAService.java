package app.service;

import app.beans.CertificateAuthority;
import app.beans.CertificateData;
import app.exception.EntityNotFoundException;

import java.util.List;

public interface CAService {

    CertificateAuthority getRootCA();

    CertificateAuthority generateCertificateAuthority(CertificateAuthority issuer, CertificateData data, boolean bottomCA);

    CertificateAuthority generateRootCA(CertificateData data);

    CertificateAuthority findById(int id) throws EntityNotFoundException;

    List<CertificateAuthority> getIntermediateCAs();

    CertificateAuthority getRandomBottomCA();

}
