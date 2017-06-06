package app.serviceImpl;

import app.beans.Certificate;
import app.beans.CertificateAuthority;
import app.beans.CertificateData;
import app.beans.CertificateSigningRequest;
import app.repository.CARepository;
import app.repository.CSRRepository;
import app.repository.CertificateDataRepository;
import app.repository.CertificateRepository;
import app.service.CertificateService;
import app.util.*;
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

import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CSRRepository csrRepository;

    @Autowired
    private CertificateDataRepository certificateDataRepository;

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

            X500Name issuerX500 = X500NameUtility.makeX500Name(cA.getCertificate().getCertificateData());
            X500Name subjectX500 = X500NameUtility.makeX500Name(request.getCertificateData());

            PublicKey subjectPublicKey = getPublicKey(request.getCertificateData().getPublicKey(),
                    request.getCertificateData().getKeyAlgorithm());

            SecureRandom random = new SecureRandom();
            int randomNumber = random.nextInt(Integer.MAX_VALUE - 1);

            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerX500,
                    new BigInteger(Integer.toString(randomNumber)),
                    notBefore,
                    notAfter,
                    subjectX500,
                    subjectPublicKey);

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

            KeyStoreCredentials credentials = KeyStoreCredentials.generateKeyStoreCredentialsForCertificate(
                    false,
                    Integer.toString(randomNumber));
            certificate.setKeyStoreFileName(credentials.getKeyStoreFileName());
            certificate.setKeyStorePassword(credentials.getKeyStorePassword());
            certificate.setKeyStoreAlias(credentials.getKeyStoreAlias());

            KeyStoreWriter writer = new KeyStoreWriter();
            writer.saveCertificateToKeyStore(certificate, x509Certificate);
            certificateDataRepository.save(certificate.getCertificateData());
            Certificate saved = certificateRepository.save(certificate);

            return saved;

        } catch (OperatorCreationException e){
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return null;

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
