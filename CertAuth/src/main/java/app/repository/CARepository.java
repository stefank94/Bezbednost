package app.repository;

import app.beans.CertificateAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CARepository extends JpaRepository<CertificateAuthority, Integer> {

    CertificateAuthority findByIssuerIsNull();

    List<CertificateAuthority> findByIssuerNotNull();

}
