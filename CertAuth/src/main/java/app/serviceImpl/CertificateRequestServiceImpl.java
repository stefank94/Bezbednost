package app.serviceImpl;

import app.beans.*;
import app.exception.EntityAlreadyExistsException;
import app.exception.EntityNotFoundException;
import app.repository.CertificateRequestRepository;
import app.service.CAService;
import app.service.CertificateRequestService;
import app.service.CertificateService;
import app.util.X509Helper;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
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

    @Override
    public CertificateSigningRequest create(CertificateSigningRequest request) throws EntityAlreadyExistsException {
        return certificateRequestRepository.save(request);
    }

    @Override
    public CertificateSigningRequest acceptCSRFile(byte[] csrFile, CertificateData.CertUsage usage, User user) throws EntityAlreadyExistsException {
        try {
            Reader reader = new InputStreamReader(new ByteArrayInputStream(csrFile));
            PemReader pemReader = new PemReader(reader);
            PKCS10CertificationRequest csr = new PKCS10CertificationRequest(pemReader.readPemObject().getContent());
            pemReader.close();
            reader.close();

            CertificateData data = X509Helper.readX500Name(csr.getSubject());
            CertificateSigningRequest request = new CertificateSigningRequest();
            request.setState(CertificateSigningRequest.CSRState.REQUESTED);
            request.setDate(new Date());
            request.setUser(user);
            request.setCertificateData(data);

            return create(request);

        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

}
