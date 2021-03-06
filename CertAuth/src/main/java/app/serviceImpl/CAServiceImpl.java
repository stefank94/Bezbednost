package app.serviceImpl;

import app.beans.*;
import app.beans.Certificate;
import app.dto.CertificateAuthorityDTO;
import app.dto.CertificateDTO;
import app.dto.CertificateDataDTO;
import app.exception.ActionNotPossibleException;
import app.exception.EntityNotFoundException;
import app.repository.CARepository;
import app.repository.CSRRepository;
import app.repository.CertificateDataRepository;
import app.service.CAService;
import app.service.CRLService;
import app.service.CertificateService;
import app.util.*;
import app.x509.CACertificateBuilder;
import app.x509.CertificateBuilder;
import app.x509.X509Helper;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CAServiceImpl implements CAService {

    @Autowired
    private CARepository caRepository;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CRLService crlService;

    @Autowired
    private CSRRepository csrRepository;

    @Autowired
    private CertificateDataRepository certificateDataRepository;

    @Value("${defaultRootDuration}")
    private int defaultRootDuration;

    @Value("${server.ssl.key-store}")
    private String httpsKeyStore;

    @Value("${server.ssl.key-store-password}")
    private String httpsKeyStorePassword;

    @Value("${server.ssl.keyAlias}")
    private String httpsKeyStoreAlias;

    private static final String FOLDER = "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "certificates" + File.separator;

    // -------------------------------

    @Override
    public CertificateAuthority getRootCA() {
        List<CertificateAuthority> roots = caRepository.findByIssuerIsNull();
        for (CertificateAuthority ca : roots)
            if (certificateService.isValid(ca.getCertificate()))
                return ca;
        return null;
    }

    @Override
    public CertificateAuthority generateCertificateAuthority(CertificateAuthority issuer, CertificateAuthorityDTO caDTO){
        try {
            CertificateData subjectData = DTOToBeanConverter.certificateDataDTOToBean(caDTO.getCertificate().getCertificateData());
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            KeyPair subjectKeyPair = keyGen.generateKeyPair();

            PrivateKey issuerPrivateKey;
            if (issuer != null){
                KeyStoreReader keyStoreReader = new KeyStoreReader();
                issuerPrivateKey = keyStoreReader.readPrivateKey(issuer);
            } else
                issuerPrivateKey = subjectKeyPair.getPrivate();

            subjectData.setPublicKey(Base64Utility.encode(subjectKeyPair.getPublic().getEncoded()));
            subjectData.setKeyAlgorithm("RSA");
            subjectData.setCA(true);
            subjectData.setCertUsage(CertificateData.CertUsage.CA);
            int serialNumber = certificateService.generateSerialNumber();

            CertificateBuilder builder;
            if (issuer != null)
                builder = new CACertificateBuilder(subjectData, caDTO.getDuration() * 12, serialNumber, issuer);
            else
                builder = new CACertificateBuilder(subjectData, caDTO.getDuration() * 12, serialNumber, issuerPrivateKey);

            builder.build(); // create the Certificate

            Certificate certificate = builder.getCertificate();
            X509Certificate x509Certificate = builder.getX509Certificate();

            CertificateData savedSubjectData = certificateDataRepository.save(subjectData);
            certificate.setCertificateData(savedSubjectData);

            // Save the certificate in a .cer file
            String fileName = certificateService.writeCerFile(x509Certificate, serialNumber);
            certificate.setCerFileName(fileName);

            KeyStoreCredentials privateKeyCredentials = KeyStoreCredentials.generateKeyStoreCredentials(Integer.toString(serialNumber));
            CertificateAuthority newCA = new CertificateAuthority();
            newCA.setCertificate(certificate);
            newCA.setKeyStoreAlias(privateKeyCredentials.getKeyStoreAlias());
            newCA.setKeyStoreFileName(privateKeyCredentials.getKeyStoreFileName());
            newCA.setKeyStorePassword(privateKeyCredentials.getKeyStorePassword());
            newCA.setPrivateKeyPassword(privateKeyCredentials.getPrivateKeyPassword());
            newCA.setCaRole(caDTO.getCaRole());
            newCA.setDuration(caDTO.getDuration());
            CRLInformation crl = new CRLInformation();
            crl.setCa(newCA);
            crl.setFrequencyDescription(ParameterHelper.getDefaultFrequencyDescription());
            crl.setCronExpression(ParameterHelper.getDefaultCron());
            newCA.setCrlInformation(crl);
            newCA.setDurationOfIssuedCertificates(caDTO.getDurationOfIssuedCertificates());

            certificate.setCa(newCA);

            if (issuer != null) {
                // set issuer
                certificate.setIssuer(issuer);
                newCA.setIssuer(issuer);
            }
            else
                // the issuer is self
                certificate.setIssuer(newCA);

            CertificateAuthority savedCA = caRepository.save(newCA);

            // save Private key to KeyStore
            savePrivateKeyToStore(savedCA, subjectKeyPair.getPrivate(), x509Certificate);

            // Start CRL generation
            crlService.addCAToSchedule(savedCA);

            return caRepository.findOne(savedCA.getId());

        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        } catch (NoSuchProviderException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CertificateAuthority generateRootCA() {
        CertificateDataDTO dataDTO = new CertificateDataDTO();
        dataDTO.setCommonName("My Root CA");
        dataDTO.setCA(true);
        dataDTO.setCountryCode("RS");
        dataDTO.setEmailAddress("root@root.com");
        dataDTO.setGivenName("My");
        dataDTO.setSurname("Root CA");
        dataDTO.setKeyAlgorithm("RSA");
        dataDTO.setOrganization("Root CA Organization");
        dataDTO.setOrganizationalUnit("Level 1");

        CertificateAuthorityDTO dto = new CertificateAuthorityDTO();
        CertificateDTO cert = new CertificateDTO();
        cert.setCertificateData(dataDTO);
        dto.setCertificate(cert);
        dto.setCaRole(CertificateAuthority.CARole.ROOT);
        return generateCertificateAuthority(null, dto);
    }

    @Override
    public void generateHTTPSCertificate() {
        try {
            CertificateAuthority root = getRootCA();
            CertificateAuthorityDTO caDTO = new CertificateAuthorityDTO();
            caDTO.setCaRole(CertificateAuthority.CARole.INTERMEDIATE);
            caDTO.setDuration(3);
            caDTO.setDurationOfIssuedCertificates(12);
            caDTO.setIssuer(root.getId());
            CertificateDTO certDTO = new CertificateDTO();
            CertificateDataDTO dataDTO = new CertificateDataDTO();
            dataDTO.setOrganization(root.getCertificate().getCertificateData().getOrganization());
            dataDTO.setOrganizationalUnit("Level 2");
            dataDTO.setCommonName("Intermediate No. 1");
            dataDTO.setGivenName("Inter");
            dataDTO.setSurname("mediate");
            dataDTO.setCA(true);
            certDTO.setCertificateData(dataDTO);
            caDTO.setCertificate(certDTO);
            CertificateAuthority ca = generateCertificateAuthority(root, caDTO);

            CertificateSigningRequest csr = new CertificateSigningRequest();
            csr.setDate(new Date());
            csr.setState(CertificateSigningRequest.CSRState.REQUESTED);
            CertificateData data = new CertificateData();
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            KeyPair keyPair = keyGen.generateKeyPair();
            data.setPublicKey(Base64Utility.encode(keyPair.getPublic().getEncoded()));
            data.setKeyAlgorithm("RSA");
            data.setCommonName("localhost:9000");
            data.setOrganization("Root CA Organization");
            data.setOrganizationalUnit("Level cert-https");

            data.setCertUsage(CertificateData.CertUsage.HTTPS);
            csr.setCertificateData(data);

            KeyStoreCredentials ksc = new KeyStoreCredentials();
            String[] tokens = httpsKeyStore.split("/");
            ksc.setKeyStoreFileName(tokens[tokens.length - 1]);
            ksc.setKeyStorePassword(httpsKeyStorePassword);
            ksc.setKeyStoreAlias(httpsKeyStoreAlias);
            ksc.setPrivateKey(keyPair.getPrivate());

            CertificateSigningRequest savedCsr = csrRepository.save(csr);

            certificateService.generateCertificate(ca, savedCsr, ksc);

            savedCsr.setState(CertificateSigningRequest.CSRState.APPROVED);
            csrRepository.save(savedCsr);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

    }

    @Override
    public CertificateAuthority findById(int id) throws EntityNotFoundException {
        CertificateAuthority ca = caRepository.findOne(id);
        if (ca == null)
            throw new EntityNotFoundException("CA not found with ID: " + id);
        return ca;
    }

    @Override
    public List<CertificateAuthority> getByRole(CertificateAuthority.CARole role) {
        List<CertificateAuthority> found = caRepository.findByCaRole(role);
        return filterInvalidCAs(found, true);
    }

    @Override
    public List<CertificateAuthority> getBottomCAs() {
        List<CertificateAuthority.CARole> roles = new ArrayList<>();
        roles.add(CertificateAuthority.CARole.CA_ISSUER);
        roles.add(CertificateAuthority.CARole.DOC_SIGN_ISSUER);
        roles.add(CertificateAuthority.CARole.HTTPS_ISSUER);
        roles.add(CertificateAuthority.CARole.MAIL_ISSUER);
        List<CertificateAuthority> found = caRepository.findByCaRoleIn(roles);
        return filterInvalidCAs(found, true);
    }

    @Override
    public List<CertificateAuthority> getAllCAs() {
        return caRepository.findAll();
    }

    @Override
    public CertificateAuthority getRandomCAForUsage(CertificateData.CertUsage usage) throws ActionNotPossibleException {
        switch (usage) {
            case CA:
                return getRandomCAWithRole(CertificateAuthority.CARole.CA_ISSUER);
            case HTTPS:
                return getRandomCAWithRole(CertificateAuthority.CARole.HTTPS_ISSUER);
            case MAIL:
                return getRandomCAWithRole(CertificateAuthority.CARole.MAIL_ISSUER);
            case DOC_SIGN:
                return getRandomCAWithRole(CertificateAuthority.CARole.DOC_SIGN_ISSUER);
            default:
                return null;
        }
    }

    @Override
    public CertificateAuthority save(CertificateAuthority ca){
        return caRepository.save(ca);
    }

    private CertificateAuthority getRandomCAWithRole(CertificateAuthority.CARole role) throws ActionNotPossibleException {
        List<CertificateAuthority> found = caRepository.findByCaRole(role);
        List<CertificateAuthority> cas = filterInvalidCAs(found, false);
        if (cas.isEmpty())
            throw new ActionNotPossibleException("There is no CA assigned to issue this type of certificate.");
        int idx = ThreadLocalRandom.current().nextInt(0, cas.size());
        return cas.get(idx);
    }

    private void savePrivateKeyToStore(CertificateAuthority cA, PrivateKey pk, X509Certificate x509){
        KeyStoreWriter writer = new KeyStoreWriter();
        writer.loadKeyStore(null, cA.getKeyStorePassword().toCharArray());
        writer.writePrivateKey(cA.getKeyStoreAlias(), pk, cA.getPrivateKeyPassword().toCharArray(), x509);
        writer.saveKeyStore(cA.getKeyStoreFileName(), cA.getKeyStorePassword().toCharArray());
    }

    private List<CertificateAuthority> filterInvalidCAs(List<CertificateAuthority> list, boolean acceptHold){
        Date now = new Date();
        List<CertificateAuthority> newList = new ArrayList<>();
        for (CertificateAuthority ca : list){
            if (ca.getCertificate().getValidTo().after(now)){
                if (ca.getCertificate().getRevocation() == null)
                    newList.add(ca);
                else if (!ca.getCertificate().getRevocation().isFullyRevoked() && acceptHold)
                    newList.add(ca);
            }
        }
        return newList;
    }

}
