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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/ca")
public class CAController {

    @Autowired
    private CAService caService;

    // -------------------------

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<CertificateAuthorityDTO> create(@RequestBody CertificateAuthorityDTO dto) throws EntityNotFoundException {
        CertificateAuthority issuer = caService.findById(dto.getIssuer());
        CertificateData data = DTOToBeanConverter.certificateDataDTOToBean(dto.getCertificate().getCertificateData());
        CertificateAuthority ca = caService.generateCertificateAuthority(issuer, data);
        return new ResponseEntity<>(BeanToDTOConverter.certificateAuthorityToDTO(ca), HttpStatus.OK);
    }

}
