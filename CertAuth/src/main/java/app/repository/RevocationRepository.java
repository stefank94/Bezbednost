package app.repository;

import app.beans.Revocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevocationRepository extends JpaRepository<Revocation, Integer> {

}
