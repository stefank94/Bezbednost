package app.service;

import app.beans.Certificate;
import app.beans.CertificateAuthority;
import app.beans.CertificateSigningRequest;
import app.beans.User;
import app.exception.EntityNotFoundException;
import java.util.List;

public interface CertificateService {

    Certificate generateCertificate(CertificateAuthority cA, CertificateSigningRequest cr);

    Certificate findOne(int id) throws EntityNotFoundException;

    List<Certificate> getMyCertificates(User logged);

}
