package app.repository;

import app.beans.CertificateSigningRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CSRRepository extends JpaRepository<CertificateSigningRequest, Integer> {

}
