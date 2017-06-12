package app.service;

import app.beans.Client;
import app.dto.LoginUserDTO;
import app.exception.EntityAlreadyExistsException;

public interface ClientService {

    Client register(LoginUserDTO dto) throws EntityAlreadyExistsException;

}
