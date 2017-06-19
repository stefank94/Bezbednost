package app.serviceImpl;

import app.beans.User;
import app.dto.TwoStrings;
import app.exception.ActionNotPossibleException;
import app.exception.EntityNotFoundException;
import app.exception.NotPermittedException;
import app.repository.UserRepository;
import app.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // -----------------------------------

    @Override
    public User findByEmail(String email) throws EntityNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new EntityNotFoundException("User not found with email: " + email);
        return user;
    }

    @Override
    public void changePassword(User loggedInUser, TwoStrings twoStrings) throws NotPermittedException, ActionNotPossibleException {
        if (!checkPassword(loggedInUser, twoStrings.string1))
            throw new NotPermittedException("Wrong old password.");
        if (twoStrings.string1.equals(twoStrings.string2))
            throw new ActionNotPossibleException("Cannot use the old password again.");
        String salt = generateSalt();
        loggedInUser.setSalt(salt);
        loggedInUser.setPassword(passwordEncoder.encode(twoStrings.string2 + salt));
        userRepository.save(loggedInUser);
    }

    @Override
    public String generateSalt() {
        return RandomStringUtils.random(20, true, true);
    }

    @Override
    public boolean checkPassword(User user, String sentPassword) {
        System.out.println("in check password");
        return passwordEncoder.matches(sentPassword + user.getSalt(), user.getPassword());
    }

}
