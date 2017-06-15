package app.service;

import app.beans.Certificate;
import app.beans.CertificateData;
import app.beans.CertificateSigningRequest;
import app.beans.User;
import app.exception.EntityAlreadyExistsException;
import app.exception.EntityNotFoundException;

import java.util.List;

public interface CertificateRequestService {

    CertificateSigningRequest findByID(int id) throws EntityNotFoundException;

    List<CertificateSigningRequest> getMyRequests(User logged);

    List<CertificateSigningRequest> getSubmittedRequests();

    Certificate approveRequest(int id) throws EntityNotFoundException;

    CertificateSigningRequest rejectRequest(int id) throws EntityNotFoundException;

    CertificateSigningRequest create(CertificateSigningRequest request) throws EntityAlreadyExistsException;

    CertificateSigningRequest acceptCSRFile(byte[] csrFile, CertificateData.CertUsage usage, User user) throws EntityAlreadyExistsException;

}
