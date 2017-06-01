package app.controller;

import app.beans.User;
import app.dto.LoginUserDTO;
import app.dto.TwoStrings;
import app.dto.UserDTO;
import app.exception.EntityNotFoundException;
import app.security.SecurityService;
import app.service.UserService;
import app.util.BeanToDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    // -------------------------------------

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<UserDTO> login(@RequestBody LoginUserDTO loginUser) throws EntityNotFoundException{
        User user = userService.findByEmail(loginUser.getUsername());
        if (user != null && user.getPassword().equals(loginUser.getPassword())){
            securityService.autologin(user.getEmail(), user.getPassword());
            return new ResponseEntity<>(BeanToDTOConverter.userToDTO(user), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/logged", method = RequestMethod.GET)
    public ResponseEntity<UserDTO> getLoggedInUser(){
        User logged = securityService.getLoggedInUser();
        UserDTO dto = BeanToDTOConverter.userToDTO(logged);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
