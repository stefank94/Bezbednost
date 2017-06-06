package app.repository;

import app.beans.CertificateData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateDataRepository extends JpaRepository<CertificateData, Integer> {

}
