package app.serviceImpl;

import app.beans.User;
import app.dto.TwoStrings;
import app.exception.ActionNotPossibleException;
import app.exception.EntityNotFoundException;
import app.exception.NotPermittedException;
import app.repository.UserRepository;
import app.service.UserService;
import app.util.Base64Utility;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    @Override
    public void changePassword(User loggedInUser, TwoStrings twoStrings) throws NotPermittedException, ActionNotPossibleException {
        if (!checkPassword(loggedInUser, twoStrings.string1))
            throw new NotPermittedException("Wrong old password.");
        if (twoStrings.string1.equals(twoStrings.string2))
            throw new ActionNotPossibleException("Cannot use the old password again.");
        String salt = generateSalt();
        loggedInUser.setSalt(salt);
        loggedInUser.setPassword(hashPassword(twoStrings.string2, salt));
        userRepository.save(loggedInUser);
    }

    @Override
    public String generateSalt() {
        return RandomStringUtils.random(20, true, true);
    }

    @Override
    public boolean checkPassword(User user, String sentPassword) {
        String hashed = hashPassword(sentPassword, user.getSalt());
        return user.getPassword().equals(hashed);
    }

    @Override
    public String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((password + salt).getBytes(StandardCharsets.UTF_8));
            return Base64Utility.encode(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
