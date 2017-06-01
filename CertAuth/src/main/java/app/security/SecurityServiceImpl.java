package app.security;

import app.beans.User;
import app.exception.EntityNotFoundException;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    // -----------------------------


    @Override
    public User getLoggedInUser() {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(obj instanceof UserDetails))
            return null;
        UserDetails userDetails = (UserDetails) obj;
        if (userDetails instanceof UserDetails) {
            String email = ((UserDetails)userDetails).getUsername();
            User user;
            try {
                user = userService.findByEmail(email);
                return user;
            } catch (EntityNotFoundException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void autologin(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
    }
}
