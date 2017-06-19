package app.service;

import app.beans.CRLInformation;
import app.beans.CertificateAuthority;
import app.exception.EntityNotFoundException;
import app.exception.InvalidDataException;

import java.util.Date;
import java.util.List;

public interface CRLService {

    Date issueCRL(int id, Date nextUpdate);

    CRLInformation saveCRLInformation(CRLInformation crlInformation);

    void addCAToSchedule(CertificateAuthority ca);

    void addCAListToSchedule(List<CertificateAuthority> list);

    void rescheduleCRLExecution(int id, String cronExp, String frequencyDescription)
            throws EntityNotFoundException, InvalidDataException;

    void rescheduleCRLExecutionForAll(String cronExp, String frequencyDescription) throws InvalidDataException;

    void cancelExecution(CertificateAuthority ca);

}
