package app.serviceImpl;

import app.beans.Certificate;
import app.beans.CertificateAuthority;
import app.beans.CertificateSigningRequest;
import app.beans.User;
import app.exception.EntityNotFoundException;
import app.repository.CertificateRequestRepository;
import app.service.CAService;
import app.service.CertificateRequestService;
import app.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateRequestServiceImpl implements CertificateRequestService {

    @Autowired
    private CertificateRequestRepository certificateRequestRepository;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CAService caService;

    // ---------------------------------------------------------------

    @Override
    public CertificateSigningRequest findByID(int id) throws EntityNotFoundException {
        CertificateSigningRequest req = certificateRequestRepository.findOne(id);
        if (req == null)
            throw new EntityNotFoundException("Certificate Request not found with ID: " + id);
        return req;
    }

    @Override
    public List<CertificateSigningRequest> getMyRequests(User logged) {
        return certificateRequestRepository.findByUser(logged);
    }

    @Override
    public List<CertificateSigningRequest> getSubmittedRequests() {
        return certificateRequestRepository.findByState(CertificateSigningRequest.CSRState.REQUESTED);
    }

    @Override
    public Certificate approveRequest(int id) throws EntityNotFoundException {
        CertificateSigningRequest req = certificateRequestRepository.findOne(id);
        if (req == null)
            throw new EntityNotFoundException("Certificate Request not found with ID: " + id);
        CertificateAuthority ca = caService.getRandomCAForUsage(req.getCertificateData().getCertUsage());
        Certificate cert = certificateService.generateCertificate(ca, req);
        req.setState(CertificateSigningRequest.CSRState.APPROVED);
        certificateRequestRepository.save(req);
        return cert;
    }

    @Override
    public CertificateSigningRequest rejectRequest(int id) throws EntityNotFoundException {
        CertificateSigningRequest req = certificateRequestRepository.findOne(id);
        if (req == null)
            throw new EntityNotFoundException("Certificate Request not found with ID: " + id);
        req.setState(CertificateSigningRequest.CSRState.REJECTED);
        return certificateRequestRepository.save(req);
    }

}
