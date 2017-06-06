package app.serviceImpl;

import app.beans.Admin;
import app.exception.EntityAlreadyExistsException;
import app.repository.AdminRepository;
import app.repository.UserRepository;
import app.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    // -------------------------------------

    @Override
    public boolean adminExists(){
        return adminRepository.count() > 0;
    }

    @Override
    public Admin create(Admin admin) throws EntityAlreadyExistsException {
        if (userRepository.findByEmail(admin.getEmail()) != null)
            throw new EntityAlreadyExistsException("User already exists with email: " + admin.getEmail());
        return adminRepository.save(admin);
    }

}
