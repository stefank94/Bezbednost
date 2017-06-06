package app.service;

import app.beans.Admin;
import app.exception.EntityAlreadyExistsException;

public interface AdminService {

    boolean adminExists();

    Admin create(Admin admin) throws EntityAlreadyExistsException;

}
