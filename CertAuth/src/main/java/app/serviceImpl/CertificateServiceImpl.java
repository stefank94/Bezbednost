package app.serviceImpl;

import app.beans.*;
import app.beans.Certificate;
import app.dto.RevocationDTO;
import app.exception.ActionNotPossibleException;
import app.exception.EntityNotFoundException;
import app.exception.InvalidDataException;
import app.exception.NotPermittedException;
import app.repository.CSRRepository;
import app.repository.CertificateDataRepository;
import app.repository.CertificateRepository;
import app.repository.RevocationRepository;
import app.service.CAService;
import app.service.CertificateService;
import app.util.*;
import app.x509.CertificateBuilder;
import app.x509.NonCACertificateBuilder;
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
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CSRRepository csrRepository;

    @Autowired
    private RevocationRepository revocationRepository;

    @Autowired
    private CertificateDataRepository certificateDataRepository;

    @Autowired
    private CAService caService;

    private static final String folder = "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "certificates" + File.separator;

    // -------------------------------------------------

    @Override
    public Certificate generateCertificate(CertificateAuthority cA, CertificateSigningRequest request, KeyStoreCredentials keyStoreCredentials){
        int serialNumber = generateSerialNumber();
        CertificateBuilder builder = new NonCACertificateBuilder(request, serialNumber, cA);

        builder.build();
        Certificate certificate = builder.getCertificate();
        X509Certificate x509Certificate = builder.getX509Certificate();

        CertificateData savedData = certificateDataRepository.save(certificate.getCertificateData());

        // Save the certificate in a .cer file
        String fileName = writeCerFile(x509Certificate, serialNumber);
        certificate.setCerFileName(fileName);
        certificate.setCertificateData(savedData);

        if (keyStoreCredentials != null)
            saveCertificateToKeyStore(keyStoreCredentials, x509Certificate);

        return certificateRepository.save(certificate);
    }

    private void saveCertificateToKeyStore(KeyStoreCredentials keyStoreCredentials, X509Certificate certificate){
        KeyStoreWriter writer = new KeyStoreWriter();
        writer.loadKeyStore(null, keyStoreCredentials.getKeyStorePassword().toCharArray());

        writer.writePrivateKey(keyStoreCredentials.getKeyStoreAlias(), keyStoreCredentials.getPrivateKey(),
                keyStoreCredentials.getKeyStorePassword().toCharArray(), certificate);
        writer.saveKeyStore(keyStoreCredentials.getKeyStoreFileName(), keyStoreCredentials.getKeyStorePassword().toCharArray());
    }

    @Override
    public int generateSerialNumber(){
        int randomNumber;
        CertificateData data;
        do {
            SecureRandom random = new SecureRandom();
            randomNumber = random.nextInt(Integer.MAX_VALUE - 1);

            data = certificateDataRepository.findBySerialNumber(randomNumber);
        } while (data != null);
        return randomNumber;
    }

    @Override
    public boolean isValid(Certificate cert) {
        if (cert.getRevocation() != null)
            return false;
        else
            return cert.getValidTo().after(new Date());
    }

    @Override
    public void getCerFileBySerialNumber(int serialNumber, OutputStream os) throws EntityNotFoundException {
        Certificate cert = certificateRepository.findBySerialNumber(serialNumber);
        if (cert == null)
            throw new EntityNotFoundException("Certificate not found by serial number: " + serialNumber);
        try {
            File file = new File(cert.getCerFileName());
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int count = 0;

            while ((count = fileInputStream.read(buffer)) >= 0) {
                os.write(buffer, 0, count);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Certificate findOne(int id) throws EntityNotFoundException {
        Certificate cert = certificateRepository.findOne(id);
        if (cert == null)
            throw new EntityNotFoundException("Certificate not found with ID: " + id);
        return cert;
    }

    @Override
    public Certificate save(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    @Override
    public List<Certificate> getMyCertificates(User logged) {
        return certificateRepository.findByUser(logged);
    }

    @Override
    public String writeCerFile(X509Certificate cert, int randomNumber) {
        try {
            String fileName = folder + randomNumber + ".cer";
            File file = new File(fileName);
            file.createNewFile();
            FileOutputStream os = new FileOutputStream(file, false);
            //os.write("-----BEGIN CERTIFICATE-----\n".getBytes("US-ASCII"));
            //os.write(Base64.encodeBase64(cert.getEncoded(), true));
            os.write(cert.getEncoded());
            //os.write("-----END CERTIFICATE-----\n".getBytes("US-ASCII"));
            os.close();
            return fileName;
        } catch(IOException e){
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Certificate revokeCertificate(RevocationDTO revocationDTO, User logged) throws EntityNotFoundException, ActionNotPossibleException, NotPermittedException, InvalidDataException {
        Certificate cert = certificateRepository.findOne(revocationDTO.getCertificate());
        if (cert == null)
            throw new EntityNotFoundException("Certificate not found with ID: " + revocationDTO.getCertificate());
        if (logged.equals(cert.getUser()) || logged instanceof Admin){
            if (cert.getRevocation() == null){
                Revocation revocation = new Revocation();
                Date now = new Date();
                if (revocationDTO.getInvalidityDate() != null && now.before(revocationDTO.getInvalidityDate()))
                    throw new InvalidDataException("Invalidity date cannot be in the future.");
                revocation.setInvalidityDate(revocationDTO.getInvalidityDate());
                revocation.setCertificate(cert);
                if (!revocationDTO.isFullyRevoked())
                    revocation.setReason("certificateHold");
                else if (!Revocation.getValidReasons().contains(revocationDTO.getReason()) || revocationDTO.equals("certificateHold"))
                    throw new InvalidDataException("Invalid certificate revocation reason.");
                else {
                    revocation.setReason(revocationDTO.getReason());
                    CertificateAuthority ca = cert.getCa();
                    if (ca != null && ca.getCaRole() == CertificateAuthority.CARole.ROOT) // If we're revoking the root CA
                        caService.generateRootCA(); // create a new root
                }
                revocation.setFullyRevoked(revocationDTO.isFullyRevoked());
                revocation.setRevocationDate(now);
                cert.setRevocation(revocation);

                Certificate savedCert = save(cert);
                if (savedCert.getCertificateData().isCA() && savedCert.getCa() != null)
                    revokeIssuedCertificates(savedCert.getCa(), revocationDTO.getInvalidityDate());

                return savedCert;

            } else
                throw new ActionNotPossibleException("This certificate has already been revoked.");
        } else
            throw new NotPermittedException("You do not have permission to revoke this certificate.");
    }

    @Override
    public Certificate restoreCertificateOnHold(int id, User logged) throws EntityNotFoundException, ActionNotPossibleException, NotPermittedException {
        Certificate cert = certificateRepository.findOne(id);
        if (cert == null)
            throw new EntityNotFoundException("Certificate not found with ID: " + id);
        if (logged.equals(cert.getUser()) || logged instanceof Admin){
            if (cert.getRevocation() == null)
                throw new ActionNotPossibleException("Cannot restore a certificate that has not been put on hold.");
            if (cert.getRevocation().isFullyRevoked())
                throw new ActionNotPossibleException("Cannot restore a fully revoked certificate.");
            Revocation rev = cert.getRevocation();
            cert.setRevocation(null);
            Certificate savedCertificate = certificateRepository.save(cert);
            revocationRepository.delete(rev);

            return savedCertificate;
        } else
            throw new NotPermittedException("You do not have permission to revoke this certificate.");
    }

    @Override
    public Certificate fullyRevokeCertificateOnHold(int id, User logged, String reason) throws EntityNotFoundException, ActionNotPossibleException, NotPermittedException {
        Certificate cert = certificateRepository.findOne(id);
        if (cert == null)
            throw new EntityNotFoundException("Certificate not found with ID: " + id);
        if (logged.equals(cert.getUser()) || logged instanceof Admin){
            if (cert.getRevocation() == null)
                throw new ActionNotPossibleException("Cannot fully revoke a certificate that has not been put on hold.");
            if (cert.getRevocation().isFullyRevoked())
                throw new ActionNotPossibleException("Certificate is already fully revoked.");
            Revocation rev = cert.getRevocation();
            rev.setFullyRevoked(true);
            if (!Revocation.getValidReasons().contains(reason) || reason.equals("certificateHold"))
                throw new ActionNotPossibleException("Invalid certificate revocation reason.");
            rev.setReason(reason);
            revocationRepository.save(rev);

            if (cert.getCertificateData().isCA())
                revokeIssuedCertificates(cert.getCa(), rev.getInvalidityDate());
            CertificateAuthority ca = cert.getCa();
            if (ca != null && ca.getCaRole() == CertificateAuthority.CARole.ROOT) // If we're revoking the root CA
                caService.generateRootCA(); // create a new root

            return certificateRepository.findOne(id);
        } else
            throw new NotPermittedException("You do not have permission to revoke this certificate.");
    }

    private void revokeIssuedCertificates(CertificateAuthority ca, Date invalidityDate){
        for (Certificate cert : ca.getIssuedCertificates()){
            Revocation rev = cert.getRevocation();
            if (rev == null || rev.getReason().equals("certificateHold")){
                Revocation newRev = new Revocation();
                newRev.setReason("cACompromise");
                newRev.setRevocationDate(new Date());
                newRev.setInvalidityDate(invalidityDate);
                newRev.setFullyRevoked(true);
                newRev.setCertificate(cert);
                cert.setRevocation(newRev);
                certificateRepository.save(cert);
                if (cert.getCertificateData().isCA() && cert.getCa() != null)
                    revokeIssuedCertificates(cert.getCa(), invalidityDate);
            }
        }
    }

}
