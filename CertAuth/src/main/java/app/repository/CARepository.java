package app.repository;

import app.beans.CertificateAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CARepository extends JpaRepository<CertificateAuthority, Integer> {

    CertificateAuthority findByIssuerIsNull();

}
