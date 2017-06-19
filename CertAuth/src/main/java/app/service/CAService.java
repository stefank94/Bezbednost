package app.service;

import app.beans.Certificate;
import app.beans.CertificateAuthority;
import app.beans.CertificateData;
import app.dto.CertificateAuthorityDTO;
import app.exception.ActionNotPossibleException;
import app.exception.EntityNotFoundException;

import java.util.List;

public interface CAService {

    CertificateAuthority getRootCA();

    CertificateAuthority generateCertificateAuthority(CertificateAuthority issuer, CertificateAuthorityDTO dto);

    CertificateAuthority generateRootCA();

    void generateHTTPSCertificate();

    CertificateAuthority findById(int id) throws EntityNotFoundException;

    List<CertificateAuthority> getByRole(CertificateAuthority.CARole role);

    List<CertificateAuthority> getBottomCAs();

    List<CertificateAuthority> getAllCAs();

    CertificateAuthority getRandomCAForUsage(CertificateData.CertUsage usage) throws ActionNotPossibleException;

    CertificateAuthority save(CertificateAuthority ca);

}
