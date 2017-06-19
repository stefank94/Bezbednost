package app.serviceImpl;

import app.beans.Client;
import app.dto.LoginUserDTO;
import app.exception.EntityAlreadyExistsException;
import app.repository.ClientRepository;
import app.service.ClientService;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserService userService;

    // ---------------------------------------

    @Override
    public Client register(LoginUserDTO dto) throws EntityAlreadyExistsException {
        Client found = clientRepository.findByEmail(dto.getUsername());
        if (found != null)
            throw new EntityAlreadyExistsException("User already exists with email: " + dto.getUsername());
        Client newClient = new Client();
        newClient.setSalt("");
        newClient.setSignupDate(new Date());
        newClient.setEmail(dto.getUsername());
        String salt = userService.generateSalt();
        newClient.setSalt(salt);
        newClient.setPassword(userService.hashPassword(dto.getPassword(), salt));
        return clientRepository.save(newClient);
    }
}
