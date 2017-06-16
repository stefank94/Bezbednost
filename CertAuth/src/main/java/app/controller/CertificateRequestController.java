package app.controller;

import app.beans.Certificate;
import app.beans.CertificateData;
import app.beans.CertificateSigningRequest;
import app.beans.User;
import app.dto.CertificateDTO;
import app.dto.CertificateSigningRequestDTO;
import app.exception.ActionNotPossibleException;
import app.exception.EntityAlreadyExistsException;
import app.exception.EntityNotFoundException;
import app.exception.WrongFileTypeException;
import app.security.SecurityService;
import app.service.CertificateRequestService;
import app.util.BeanToDTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RequestMapping(value = "/api/request")
@RestController
public class CertificateRequestController {

    @Autowired
    private CertificateRequestService certificateRequestService;

    @Autowired
    private SecurityService securityService;

    // -------------------------------------

    @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CertificateSigningRequestDTO> uploadCSR(@RequestParam MultipartFile file,
                         @RequestParam("usage") CertificateData.CertUsage usage) throws EntityAlreadyExistsException, WrongFileTypeException, InvalidKeyException, NoSuchAlgorithmException {

        User logged = securityService.getLoggedInUser();
        if (!file.getOriginalFilename().endsWith(".csr"))
            throw new WrongFileTypeException("Can only accept .csr files.");
        try {
            CertificateSigningRequest req = certificateRequestService.acceptCSRFile(file.getBytes(), usage, logged);
            return new ResponseEntity<>(BeanToDTOConverter.certificateSigningRequestToDTO(req), HttpStatus.OK);
        } catch (IOException e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CertificateSigningRequestDTO> getByID(@PathVariable("id") int id) throws EntityNotFoundException {
        CertificateSigningRequest req = certificateRequestService.findByID(id);
        return new ResponseEntity<>(BeanToDTOConverter.certificateSigningRequestToDTO(req), HttpStatus.OK);
    }

    @RequestMapping(value = "/getMyRequests", method = RequestMethod.GET)
    public ResponseEntity<List<CertificateSigningRequestDTO>> getMyRequests(){
        User logged = securityService.getLoggedInUser();
        List<CertificateSigningRequest> list = certificateRequestService.getMyRequests(logged);
        return new ResponseEntity<>(BeanToDTOConverter.certificateSigningRequestListToDTO(list), HttpStatus.OK);
    }

    @RequestMapping(value = "/getAllSubmittedRequests", method = RequestMethod.GET)
    public ResponseEntity<List<CertificateSigningRequestDTO>> getAllSubmittedRequests(){
        List<CertificateSigningRequest> list = certificateRequestService.getSubmittedRequests();
        return new ResponseEntity<>(BeanToDTOConverter.certificateSigningRequestListToDTO(list), HttpStatus.OK);
    }

    @RequestMapping(value = "/approve/{id}", method = RequestMethod.PUT)
    public ResponseEntity<CertificateDTO> approveRequest(@PathVariable("id") int id) throws EntityNotFoundException, ActionNotPossibleException {
        Certificate cert = certificateRequestService.approveRequest(id);
        return new ResponseEntity<>(BeanToDTOConverter.certificateToDTO(cert), HttpStatus.OK);
    }

    @RequestMapping(value = "/reject/{id}", method = RequestMethod.PUT)
    public ResponseEntity<CertificateSigningRequestDTO> rejectRequest(@PathVariable("id") int id) throws EntityNotFoundException {
        CertificateSigningRequest req = certificateRequestService.rejectRequest(id);
        return new ResponseEntity<>(BeanToDTOConverter.certificateSigningRequestToDTO(req), HttpStatus.OK);
    }

}
