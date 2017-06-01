package app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.beans.PersonUser;

public interface PersonUserRepository extends JpaRepository<PersonUser, Integer> {

}
