package app.repository;

import app.beans.CRLInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CRLInformationRepository extends JpaRepository<CRLInformation, Integer> {

}
