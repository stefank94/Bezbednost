package app.controller;

import app.beans.Certificate;
import app.beans.CertificateData;
import app.beans.User;
import app.dto.CertificateDTO;
import app.dto.RevocationDTO;
import app.exception.ActionNotPossibleException;
import app.exception.EntityNotFoundException;
import app.exception.InvalidDataException;
import app.exception.NotPermittedException;
import app.security.SecurityService;
import app.service.CertificateService;
import app.util.BeanToDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
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

    @RequestMapping(value = "/revoke", method = RequestMethod.POST)
    public ResponseEntity<CertificateDTO> revokeCertificate(@RequestBody RevocationDTO revocationDTO) throws InvalidDataException, ActionNotPossibleException, NotPermittedException, EntityNotFoundException {
        User logged = securityService.getLoggedInUser();
        Certificate cert = certificateService.revokeCertificate(revocationDTO, logged);
        return new ResponseEntity<>(BeanToDTOConverter.certificateToDTO(cert), HttpStatus.OK);
    }

    @RequestMapping(value = "/restore/{id}", method = RequestMethod.PUT)
    public ResponseEntity<CertificateDTO> restoreCertificate(@PathVariable("id") int id) throws EntityNotFoundException, ActionNotPossibleException, NotPermittedException {
        User logged = securityService.getLoggedInUser();
        Certificate cert = certificateService.restoreCertificateOnHold(id, logged);
        return new ResponseEntity<>(BeanToDTOConverter.certificateToDTO(cert), HttpStatus.OK);
    }

    @RequestMapping(value = "/fullyRevoke/{id}", method = RequestMethod.PUT)
    public ResponseEntity<CertificateDTO> fullyRevokeCertificateOnHold(@PathVariable("id") int id, @RequestBody String reason) throws EntityNotFoundException, ActionNotPossibleException, NotPermittedException, InvalidDataException {
        User logged = securityService.getLoggedInUser();
        Certificate cert = certificateService.fullyRevokeCertificateOnHold(id, logged, reason);
        return new ResponseEntity<>(BeanToDTOConverter.certificateToDTO(cert), HttpStatus.OK);
    }

    @RequestMapping(value = "/cerFile/{serial}", method = RequestMethod.GET, produces = "application/pkix-cert")
    public void getCerFile(@PathVariable("serial") int serial, HttpServletResponse response){
        try {
            certificateService.getCerFileBySerialNumber(serial, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
