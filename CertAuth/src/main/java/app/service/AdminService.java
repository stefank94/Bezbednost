package app.service;

import app.beans.Admin;
import app.dto.AdminDTO;
import app.exception.EntityAlreadyExistsException;

public interface AdminService {

    boolean adminExists();

    Admin create(AdminDTO admin) throws EntityAlreadyExistsException;

}
