package app.service;

import app.beans.CRLInformation;
import app.beans.CertificateAuthority;

public interface CRLService {

    void issueCRL(CertificateAuthority ca);

    CRLInformation saveCRLInformation(CRLInformation crlInformation);

}
