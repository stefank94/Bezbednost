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
import app.service.CertificateService;
import app.util.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
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

    private static final String folder = "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "certificates" + File.separator;

    // -------------------------------------------------

    @Override
    public Certificate generateCertificate(CertificateAuthority cA, CertificateSigningRequest request) {
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");

            KeyStoreReader keyStoreReader = new KeyStoreReader();
            ContentSigner contentSigner = builder.build(keyStoreReader.readPrivateKey(cA));

            Date notBefore = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(notBefore);
            calendar.add(Calendar.YEAR, 2); // Trajanje sertifikata 2 godine
            Date notAfter = calendar.getTime();

            X500Name issuerX500 = X509Helper.makeX500Name(cA.getCertificate().getCertificateData());
            X500Name subjectX500 = X509Helper.makeX500Name(request.getCertificateData());

            PublicKey subjectPublicKey = getPublicKey(request.getCertificateData().getPublicKey(),
                    request.getCertificateData().getKeyAlgorithm());

            int randomNumber = generateSerialNumber();

            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerX500,
                    new BigInteger(Integer.toString(randomNumber)),
                    notBefore,
                    notAfter,
                    subjectX500,
                    subjectPublicKey);

            X509Helper.makeExtensions(certGen, request.getCertificateData(), subjectPublicKey, cA, issuerX500);

            X509CertificateHolder certHolder = certGen.build(contentSigner);
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            X509Certificate x509Certificate = certConverter.getCertificate(certHolder);

            request.setState(CertificateSigningRequest.CSRState.APPROVED);
            csrRepository.save(request);

            Certificate certificate = new Certificate();
            certificate.setCertificateData(request.getCertificateData());
            certificate.setUser(request.getUser());
            certificate.setIssuer(cA);
            certificate.setValidFrom(notBefore);
            certificate.setValidTo(notAfter);
            certificate.getCertificateData().setSerialNumber(randomNumber);

            // Save the certificate in a .cer file
            String fileName = writeCerFile(x509Certificate, randomNumber);
            certificate.setCerFileName(fileName);

            Certificate saved = certificateRepository.save(certificate);

            return saved;

        } catch (OperatorCreationException e){
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return null;

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
            os.write("-----BEGIN CERTIFICATE-----\n".getBytes("US-ASCII"));
            os.write(Base64.encodeBase64(cert.getEncoded(), true));
            os.write("-----END CERTIFICATE-----\n".getBytes("US-ASCII"));
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
                else
                    revocation.setReason(revocationDTO.getReason());
                revocation.setFullyRevoked(revocationDTO.isFullyRevoked());
                revocation.setRevocationDate(now);
                cert.setRevocation(revocation);

                Certificate savedCert = save(cert);
                if (savedCert.getCertificateData().isCA())
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
                if (cert.getCertificateData().isCA())
                    revokeIssuedCertificates(cert.getCa(), invalidityDate);
            }
        }
    }

    private PublicKey getPublicKey(String publicKeyString, String keyAlgorithm){
        try {
            byte[] publicKey = Base64Utility.decode(publicKeyString);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
            return keyFactory.generatePublic(spec);
        } catch (IOException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

}
