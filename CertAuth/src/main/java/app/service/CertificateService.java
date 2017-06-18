package app.service;

import app.beans.*;
import app.dto.RevocationDTO;
import app.exception.ActionNotPossibleException;
import app.exception.EntityNotFoundException;
import app.exception.InvalidDataException;
import app.exception.NotPermittedException;
import java.security.cert.X509Certificate;
import java.util.List;

public interface CertificateService {

    Certificate generateCertificate(CertificateAuthority cA, CertificateSigningRequest cr);

    Certificate findOne(int id) throws EntityNotFoundException;

    Certificate save(Certificate certificate);

    List<Certificate> getMyCertificates(User logged);

    String writeCerFile(X509Certificate cert, int randomNumber);

    Certificate revokeCertificate(RevocationDTO revocation, User logged) throws EntityNotFoundException, ActionNotPossibleException,
            NotPermittedException, InvalidDataException;

    Certificate restoreCertificateOnHold(int id, User logged) throws EntityNotFoundException, ActionNotPossibleException,
            NotPermittedException;

    Certificate fullyRevokeCertificateOnHold(int id, User logged, String reason) throws EntityNotFoundException, ActionNotPossibleException,
            NotPermittedException, InvalidDataException;

    int generateSerialNumber();

}
