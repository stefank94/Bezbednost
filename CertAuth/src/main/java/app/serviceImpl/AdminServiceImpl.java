package app.serviceImpl;

import app.beans.Admin;
import app.dto.AdminDTO;
import app.exception.EntityAlreadyExistsException;
import app.repository.AdminRepository;
import app.repository.UserRepository;
import app.service.AdminService;
import app.service.UserService;
import app.util.DTOToBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // -------------------------------------

    @Override
    public boolean adminExists(){
        return adminRepository.count() > 0;
    }

    @Override
    public Admin create(AdminDTO dto) throws EntityAlreadyExistsException {
        Admin admin = new Admin();
        admin.setEmail(dto.getEmail());
        if (userRepository.findByEmail(admin.getEmail()) != null)
            throw new EntityAlreadyExistsException("User already exists with email: " + admin.getEmail());
        admin.setSignupDate(new Date());
        String salt = userService.generateSalt();
        admin.setSalt(salt);
        admin.setPassword(userService.hashPassword(dto.getPassword(), salt));
        return adminRepository.save(admin);
    }

}
