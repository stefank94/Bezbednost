package app.service;

import app.beans.CRLInformation;
import app.beans.CertificateAuthority;

import java.util.Date;

public interface CRLService {

    Date issueCRL(CertificateAuthority ca, Date nextUpdate);

    CRLInformation saveCRLInformation(CRLInformation crlInformation);

}
