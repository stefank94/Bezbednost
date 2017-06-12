package app.controller;

import app.beans.Certificate;
import app.beans.User;
import app.dto.CertificateDTO;
import app.exception.EntityNotFoundException;
import app.security.SecurityService;
import app.service.CertificateService;
import app.util.BeanToDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/cert")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private SecurityService securityService;

    // -------------------------------------------

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CertificateDTO> getCertificate(@PathVariable("id") int id) throws EntityNotFoundException {
        Certificate cert = certificateService.findOne(id);
        return new ResponseEntity<>(BeanToDTOConverter.certificateToDTO(cert), HttpStatus.OK);
    }

    @RequestMapping(value = "/getMyCertificates", method = RequestMethod.GET)
    public ResponseEntity<List<CertificateDTO>> getMyCertificates(){
        User logged = securityService.getLoggedInUser();
        List<Certificate> list = certificateService.getMyCertificates(logged);
        return new ResponseEntity<>(BeanToDTOConverter.certificateListToDTO(list), HttpStatus.OK);
    }

}
