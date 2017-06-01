package app.security;

import app.beans.User;
import app.exception.EntityNotFoundException;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = userService.findByEmail(email);
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            GrantedAuthority auth = new SimpleGrantedAuthority(user.getRole());
            grantedAuthorities.add(auth);
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);
        } catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }

    }
}
