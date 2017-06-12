package app.repository;

import app.beans.CertificateSigningRequest;
import app.beans.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRequestRepository extends JpaRepository<CertificateSigningRequest, Integer> {

    List<CertificateSigningRequest> findByUser(User user);
    List<CertificateSigningRequest> findByState(CertificateSigningRequest.CSRState state);

}
