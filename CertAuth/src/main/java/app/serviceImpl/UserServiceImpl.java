package app.serviceImpl;

import app.beans.User;
import app.dto.TwoStrings;
import app.exception.ActionNotPossibleException;
import app.exception.EntityNotFoundException;
import app.exception.NotPermittedException;
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

    @Override
    public void changePassword(User loggedInUser, TwoStrings twoStrings) throws NotPermittedException, ActionNotPossibleException {
        if (!loggedInUser.getPassword().equals(twoStrings.string1))
            throw new NotPermittedException("Wrong old password.");
        if (twoStrings.string1.equals(twoStrings.string2))
            throw new ActionNotPossibleException("Cannot use the old password again.");
        loggedInUser.setPassword(twoStrings.string2);
        userRepository.save(loggedInUser);
    }

}
