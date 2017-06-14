package app.service;

import app.beans.*;
import app.exception.EntityNotFoundException;

import java.security.cert.X509Certificate;
import java.util.List;

public interface CertificateService {

    Certificate generateCertificate(CertificateAuthority cA, CertificateSigningRequest cr);

    Certificate findOne(int id) throws EntityNotFoundException;

    List<Certificate> getMyCertificates(User logged);

    String writeCerFile(X509Certificate cert, int randomNumber);

}
