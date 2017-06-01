package app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import app.beans.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
