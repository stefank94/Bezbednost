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
    private CARepository caRepository;

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

            KeyStoreCredentials credentials = generateKeyStoreCredentialsForCertificate(
                    false,
                    Integer.toString(randomNumber));
            certificate.setKeyStoreFileName(credentials.getKeyStoreFileName());
            certificate.setKeyStorePassword(credentials.getKeyStorePassword());
            certificate.setKeyStoreAlias(credentials.getKeyStoreAlias());

            saveCertificateToKeyStore(certificate, x509Certificate);
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

    @Override
    public CertificateAuthority generateCertificateAuthority(CertificateAuthority issuer, CertificateData subjectData) {
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");


            PrivateKey issuerPrivateKey = null;
            KeyPair issuerKeyPair = null;
            if (issuer != null) {
                KeyStoreReader keyStoreReader = new KeyStoreReader();
                issuerPrivateKey = keyStoreReader.readPrivateKey(issuer);
            } else {
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
                keyGen.initialize(2048, random);
                issuerKeyPair = keyGen.generateKeyPair();
                issuerPrivateKey = issuerKeyPair.getPrivate();
            }

            ContentSigner contentSigner = builder.build(issuerPrivateKey);

            Date notBefore = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(notBefore);
            calendar.add(Calendar.YEAR, 2); // Trajanje sertifikata 2 godine
            Date notAfter = calendar.getTime();

            X500Name issuerX500 = null;
            if (issuer != null)
                issuerX500 = X500NameUtility.makeX500Name(issuer.getCertificate().getCertificateData());
            else
                issuerX500 = X500NameUtility.makeX500Name(subjectData);
            X500Name subjectX500 = X500NameUtility.makeX500Name(subjectData);

            SecureRandom random = new SecureRandom();
            int randomNumber = random.nextInt(Integer.MAX_VALUE - 1);

            KeyPair subjectKeyPair = null;
            if (issuer != null) {
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
                keyGen.initialize(2048, secureRandom);
                subjectKeyPair = keyGen.generateKeyPair();
            } else
                subjectKeyPair = issuerKeyPair;
            subjectData.setPublicKey(Base64Utility.encode(subjectKeyPair.getPublic().getEncoded()));

            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerX500,
                    new BigInteger(Integer.toString(randomNumber)),
                    notBefore,
                    notAfter,
                    subjectX500,
                    subjectKeyPair.getPublic());

            X509CertificateHolder certHolder = certGen.build(contentSigner);
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            X509Certificate x509Certificate = certConverter.getCertificate(certHolder);

            Certificate certificate = new Certificate();
            certificate.setCertificateData(subjectData);
            certificate.setUser(null);
            certificate.setIssuer(issuer);
            certificate.setValidFrom(notBefore);
            certificate.setValidTo(notAfter);

            KeyStoreCredentials credentials = generateKeyStoreCredentialsForCertificate(
                    false,
                    Integer.toString(randomNumber));
            certificate.setKeyStoreFileName(credentials.getKeyStoreFileName());
            certificate.setKeyStorePassword(credentials.getKeyStorePassword());
            certificate.setKeyStoreAlias(credentials.getKeyStoreAlias());
            saveCertificateToKeyStore(certificate, x509Certificate);

            KeyStoreCredentials privateKeyCredentials = generateKeyStoreCredentialsForCertificate(
                    true,
                    Integer.toString(randomNumber));
            CertificateAuthority newCA = new CertificateAuthority();
            newCA.setCertificate(certificate);
            newCA.setKeyStoreAlias(privateKeyCredentials.getKeyStoreAlias());
            newCA.setKeyStoreFileName(privateKeyCredentials.getKeyStoreFileName());
            newCA.setKeyStorePassword(privateKeyCredentials.getKeyStorePassword());
            newCA.setPrivateKeyPassword(privateKeyCredentials.getPrivateKeyPassword());
            if (issuer != null)
                certificate.setIssuer(issuer);
            else
                certificate.setIssuer(newCA);

            CertificateAuthority savedCA = caRepository.save(newCA);

            savePrivateKeyToStore(savedCA, subjectKeyPair.getPrivate(), x509Certificate);

            return savedCA;

        } catch(OperatorCreationException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public CertificateAuthority generateRootCA(CertificateData data) {
        return generateCertificateAuthority(null, data);
    }

    private void saveCertificateToKeyStore(Certificate cert, X509Certificate x509){
        KeyStoreWriter writer = new KeyStoreWriter();
        writer.loadKeyStore(null, cert.getKeyStorePassword().toCharArray());
        writer.writeCertificate(cert.getKeyStoreAlias(), x509);
        writer.saveKeyStore(cert.getKeyStoreFileName(), cert.getKeyStorePassword().toCharArray());
    }

    private void savePrivateKeyToStore(CertificateAuthority cA, PrivateKey pk, X509Certificate x509){
        KeyStoreWriter writer = new KeyStoreWriter();
        writer.loadKeyStore(null, cA.getKeyStorePassword().toCharArray());
        writer.writePrivateKey(cA.getKeyStoreAlias(), pk, cA.getPrivateKeyPassword().toCharArray(), x509);
        writer.saveKeyStore(cA.getKeyStoreFileName(), cA.getKeyStorePassword().toCharArray());
    }

    private KeyStoreCredentials generateKeyStoreCredentialsForCertificate(boolean forPrivateKey, String identifier){
        KeyStoreCredentials credentials = new KeyStoreCredentials();
        SecureRandom random = new SecureRandom();
        if (forPrivateKey) {
            credentials.setKeyStoreFileName(identifier + "_certificate");
            credentials.setPrivateKeyPassword(new BigInteger(130, random).toString(32));
        } else
            credentials.setKeyStoreFileName(identifier + "_PK");
        credentials.setKeyStoreAlias(new BigInteger(130, random).toString(32));
        credentials.setKeyStorePassword(new BigInteger(130, random).toString(32));

        return credentials;
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
