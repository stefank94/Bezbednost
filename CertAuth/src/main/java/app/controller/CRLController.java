package app.controller;

import app.beans.CertificateAuthority;
import app.exception.EntityNotFoundException;
import app.service.CAService;
import app.service.CRLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crl")
public class CRLController {

    @Autowired
    private CRLService crlService;

    @Autowired
    private CAService caService;

    // ---------------------------



}
