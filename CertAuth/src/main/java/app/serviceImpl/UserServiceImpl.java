package app.serviceImpl;

import app.beans.User;
import app.exception.EntityNotFoundException;
import app.repository.UserRepository;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // -----------------------------------

    @Override
    public User findByEmail(String email) throws EntityNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new EntityNotFoundException("User not found with email: " + email);
        return user;
    }

}
