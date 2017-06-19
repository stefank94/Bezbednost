package app.controller;

import app.beans.CertificateAuthority;
import app.beans.CertificateData;
import app.dto.CertificateAuthorityDTO;
import app.exception.EntityNotFoundException;
import app.service.CAService;
import app.util.BeanToDTOConverter;
import app.util.DTOToBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/ca")
public class CAController {

    @Autowired
    private CAService caService;

    // -------------------------

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<CertificateAuthorityDTO> create(@RequestBody CertificateAuthorityDTO dto) throws EntityNotFoundException {
        CertificateAuthority issuer = caService.findById(dto.getIssuer());
        CertificateAuthority ca = caService.generateCertificateAuthority(issuer, dto);
        return new ResponseEntity<>(BeanToDTOConverter.certificateAuthorityToDTO(ca), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CertificateAuthorityDTO> findByID(@PathVariable("id") int id) throws EntityNotFoundException {
        CertificateAuthority ca = caService.findById(id);
        return new ResponseEntity<>(BeanToDTOConverter.certificateAuthorityToDTO(ca), HttpStatus.OK);
    }

    @RequestMapping(value = "/root", method = RequestMethod.GET)
    public ResponseEntity<CertificateAuthorityDTO> findRoot(){
        CertificateAuthority root = caService.getRootCA();
        return new ResponseEntity<>(BeanToDTOConverter.certificateAuthorityToDTO(root), HttpStatus.OK);
    }

    @RequestMapping(value = "/intermediateCAs", method = RequestMethod.GET)
    public ResponseEntity<List<CertificateAuthorityDTO>> getIntermediateCAs(){
        List<CertificateAuthority> list = caService.getByRole(CertificateAuthority.CARole.INTERMEDIATE);
        return new ResponseEntity<>(BeanToDTOConverter.certificateAuthorityListToDTO(list), HttpStatus.OK);
    }

    @RequestMapping(value = "/bottomCAs", method = RequestMethod.GET)
    public ResponseEntity<List<CertificateAuthorityDTO>> getBottomCAs(){
        List<CertificateAuthority> list = caService.getBottomCAs();
        return new ResponseEntity<>(BeanToDTOConverter.certificateAuthorityListToDTO(list), HttpStatus.OK);
    }

}
