package app.repository;

import app.beans.Certificate;
import app.beans.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {

    List<Certificate> findByUser(User user);

}
