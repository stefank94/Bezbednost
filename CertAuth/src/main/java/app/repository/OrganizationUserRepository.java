package app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.beans.OrganizationUser;

public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, Integer>{

}
