package app.service;

import app.beans.User;
import app.exception.EntityNotFoundException;

public interface UserService {

    User findByEmail(String email) throws EntityNotFoundException;

}
