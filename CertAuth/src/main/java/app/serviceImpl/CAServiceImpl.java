package app.serviceImpl;

import app.beans.Certificate;
import app.beans.CertificateAuthority;
import app.beans.CertificateData;
import app.exception.EntityNotFoundException;
import app.repository.CARepository;
import app.service.CAService;
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

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CAServiceImpl implements CAService {

    @Autowired
    private CARepository caRepository;

    // -------------------------------

    @Override
    public CertificateAuthority getRootCA() {
        return caRepository.findByIssuerIsNull();
    }

    @Override
    public CertificateAuthority generateCertificateAuthority(CertificateAuthority issuer, CertificateData subjectData, boolean bottomCA) {
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");


            PrivateKey issuerPrivateKey = null;
            KeyPair issuerKeyPair = null;
            if (issuer != null) {
                // if we specified the issuer for our new CA, we read the issuer's private key from Key Store
                KeyStoreReader keyStoreReader = new KeyStoreReader();
                issuerPrivateKey = keyStoreReader.readPrivateKey(issuer);
            } else {
                // otherwise, we are creating a new root CA, so we instantiate a key pair now
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
                keyGen.initialize(2048, random);
                issuerKeyPair = keyGen.generateKeyPair();
                issuerPrivateKey = issuerKeyPair.getPrivate();
            }

            ContentSigner contentSigner = builder.build(issuerPrivateKey);

            // Validity for the certificate
            Date notBefore = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(notBefore);
            calendar.add(Calendar.YEAR, 2); // Trajanje sertifikata 2 godine
            Date notAfter = calendar.getTime();

            X500Name issuerX500 = null;
            if (issuer != null)
                // if the issuer is specified, we get his information
                issuerX500 = X500NameUtility.makeX500Name(issuer.getCertificate().getCertificateData());
            else
                // otherwise we are creating a root CA, so the issuer data is the same as subject data
                issuerX500 = X500NameUtility.makeX500Name(subjectData);
            X500Name subjectX500 = X500NameUtility.makeX500Name(subjectData);

            SecureRandom random = new SecureRandom();
            int randomNumber = random.nextInt(Integer.MAX_VALUE - 1);

            KeyPair subjectKeyPair = null;
            if (issuer != null) {
                // We need to create a key pair for our new CA
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
                keyGen.initialize(2048, secureRandom);
                subjectKeyPair = keyGen.generateKeyPair();
            } else
                // if the issuer is null, we are creating a root CA, so the issuer key pair created earlier will be used
                subjectKeyPair = issuerKeyPair;
            subjectData.setPublicKey(Base64Utility.encode(subjectKeyPair.getPublic().getEncoded()));
            subjectData.setCA(true);
            if (subjectData.getKeyAlgorithm() == null || subjectData.getKeyAlgorithm().equals(""))
                subjectData.setKeyAlgorithm("RSA");

            // create a certificate for our new CA
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

            // randomly create Key Store credentials, and save the certificate in Key Store
            KeyStoreCredentials credentials = KeyStoreCredentials.generateKeyStoreCredentialsForCertificate(
                    false,
                    Integer.toString(randomNumber));
            certificate.setKeyStoreFileName(credentials.getKeyStoreFileName());
            certificate.setKeyStorePassword(credentials.getKeyStorePassword());
            certificate.setKeyStoreAlias(credentials.getKeyStoreAlias());
            KeyStoreWriter writer = new KeyStoreWriter();
            writer.saveCertificateToKeyStore(certificate, x509Certificate);

            KeyStoreCredentials privateKeyCredentials = KeyStoreCredentials.generateKeyStoreCredentialsForCertificate(
                    true,
                    Integer.toString(randomNumber));
            CertificateAuthority newCA = new CertificateAuthority();
            newCA.setCertificate(certificate);
            newCA.setKeyStoreAlias(privateKeyCredentials.getKeyStoreAlias());
            newCA.setKeyStoreFileName(privateKeyCredentials.getKeyStoreFileName());
            newCA.setKeyStorePassword(privateKeyCredentials.getKeyStorePassword());
            newCA.setPrivateKeyPassword(privateKeyCredentials.getPrivateKeyPassword());
            newCA.setBottomCA(bottomCA);

            certificate.setCa(newCA);

            if (issuer != null) {
                // set issuer
                certificate.setIssuer(issuer);
                newCA.setIssuer(issuer);
                // Isser CA is no longer a bottom CA, so update it
                if (issuer.isBottomCA()) {
                    issuer.setBottomCA(false);
                    caRepository.save(issuer);
                }
            }
            else
                // the issuer is self
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
        return generateCertificateAuthority(null, data, false);
    }

    @Override
    public CertificateAuthority findById(int id) throws EntityNotFoundException {
        CertificateAuthority ca = caRepository.findOne(id);
        if (ca == null)
            throw new EntityNotFoundException("CA not found with ID: " + id);
        return ca;
    }

    @Override
    public List<CertificateAuthority> getIntermediateCAs() {
        return caRepository.findByIssuerNotNull();
    }

    @Override
    public CertificateAuthority getRandomBottomCA() {
        List<CertificateAuthority> cas = caRepository.findByBottomCA(true);
        int idx = ThreadLocalRandom.current().nextInt(0, cas.size());
        return cas.get(idx);
    }

    private void savePrivateKeyToStore(CertificateAuthority cA, PrivateKey pk, X509Certificate x509){
        KeyStoreWriter writer = new KeyStoreWriter();
        writer.loadKeyStore(null, cA.getKeyStorePassword().toCharArray());
        writer.writePrivateKey(cA.getKeyStoreAlias(), pk, cA.getPrivateKeyPassword().toCharArray(), x509);
        writer.saveKeyStore(cA.getKeyStoreFileName(), cA.getKeyStorePassword().toCharArray());
    }

}
