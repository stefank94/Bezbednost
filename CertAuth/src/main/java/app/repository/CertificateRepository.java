package app.repository;

import app.beans.Certificate;
import app.beans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {

    List<Certificate> findByUser(User user);

    @Query(value = "SELECT c from Certificate c where c.certificateData.serialNumber = :serial")
    Certificate findBySerialNumber(@Param("serial") int serial);

}
