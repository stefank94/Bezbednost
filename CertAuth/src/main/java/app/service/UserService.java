package app.service;

import app.beans.User;
import app.dto.TwoStrings;
import app.exception.ActionNotPossibleException;
import app.exception.EntityNotFoundException;
import app.exception.NotPermittedException;

public interface UserService {

    User findByEmail(String email) throws EntityNotFoundException;

    void changePassword(User loggedInUser, TwoStrings twoStrings) throws NotPermittedException, ActionNotPossibleException;

    String generateSalt();

    boolean checkPassword(User user, String sentPassword);

}
