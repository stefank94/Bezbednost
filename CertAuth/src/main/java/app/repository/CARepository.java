package app.repository;

import app.beans.CertificateAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CARepository extends JpaRepository<CertificateAuthority, Integer> {

    List<CertificateAuthority> findByIssuerIsNull();

    List<CertificateAuthority> findByCaRole(CertificateAuthority.CARole role);

    List<CertificateAuthority> findByCaRoleIn(List<CertificateAuthority.CARole> roles);

}
