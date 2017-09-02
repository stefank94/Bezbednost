package app.controller;

import app.beans.Admin;
import app.dto.AdminDTO;
import app.exception.EntityAlreadyExistsException;
import app.service.AdminService;
import app.util.BeanToDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // -------------------------------

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ResponseEntity<AdminDTO> registerAdmin(@RequestBody AdminDTO dto) throws EntityAlreadyExistsException {
        Admin newAdmin = adminService.create(dto);
        return new ResponseEntity<>(BeanToDTOConverter.adminToDTO(newAdmin), HttpStatus.OK);
    }

}
