package app.x509;

import app.beans.*;
import app.beans.Certificate;
import app.util.Base64Utility;
import app.util.KeyStoreReader;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public abstract class CertificateBuilder {

    protected Certificate certificate;
    protected X509Certificate x509;

    protected int serialNumber;
    protected CertificateAuthority issuer;
    protected CertificateData subjectData;
    protected User creator;
    protected int duration; // in months
    protected PrivateKey privateKey;

    private int defaultRootDuration = 5;

    public void build(){
        try {

            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");

            KeyStoreReader keyStoreReader = new KeyStoreReader();
            ContentSigner contentSigner = builder.build(issuer != null ? keyStoreReader.readPrivateKey(issuer) : privateKey);

            Date notBefore = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(notBefore);
            if (!subjectData.isCA())
                // if this is not a CA certificate, duration is set by the issuer
                calendar.add(Calendar.MONTH, issuer.getDurationOfIssuedCertificates());
            else if (issuer != null)
                // if this is a CA certificate, but not the root CA, the duration is set by the administration upon creation
                calendar.add(Calendar.MONTH, duration);
            else
                // if this is the root CA certificate, use the default root duration
                calendar.add(Calendar.YEAR, defaultRootDuration);
            Date notAfter = calendar.getTime();

            X500Name subjectX500 = makeX500Name(subjectData);
            // Issuer is the subject if the CA if null.
            X500Name issuerX500 = issuer != null ? makeX500Name(issuer.getCertificate().getCertificateData()) : subjectX500;

            PublicKey subjectPublicKey = getPublicKey(subjectData.getPublicKey(),
                    subjectData.getKeyAlgorithm());

            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerX500,
                    new BigInteger(Integer.toString(serialNumber)),
                    notBefore,
                    notAfter,
                    subjectX500,
                    subjectPublicKey);

            makeExtensions(certGen, subjectData, subjectPublicKey, issuer, issuerX500, subjectX500);

            X509CertificateHolder certHolder = certGen.build(contentSigner);
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            this.x509 = certConverter.getCertificate(certHolder);

            this.certificate = new Certificate();
            certificate.setCertificateData(subjectData);
            certificate.setUser(creator);
            certificate.setIssuer(issuer);
            certificate.setValidFrom(notBefore);
            certificate.setValidTo(notAfter);
            certificate.getCertificateData().setSerialNumber(serialNumber);


        } catch (OperatorCreationException e){
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
    }

    public Certificate getCertificate(){
        return certificate;
    }

    public X509Certificate getX509Certificate(){
        return x509;
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

    private X500Name makeX500Name(CertificateData data){
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        addRDN(builder, BCStyle.CN, data.getCommonName()); //common name
        addRDN(builder, BCStyle.SURNAME, data.getSurname()); // surname
        addRDN(builder, BCStyle.GIVENNAME, data.getGivenName()); // given name
        addRDN(builder, BCStyle.O, data.getOrganization()); // organization
        addRDN(builder, BCStyle.OU, data.getOrganizationalUnit()); // organizational unit
        addRDN(builder, BCStyle.C, data.getCountryCode()); // country code
        addRDN(builder, BCStyle.E, data.getEmailAddress()); // email address
        addRDN(builder, BCStyle.SERIALNUMBER, Integer.toString(data.getSerialNumber())); // serial number

        return builder.build();
    }

    private void addRDN(X500NameBuilder builder, ASN1ObjectIdentifier id, String value){
        if (value != null && !value.equals(""))
            builder.addRDN(id, value);
    }

    private void makeExtensions(X509v3CertificateBuilder builder, CertificateData data, PublicKey publicKey, CertificateAuthority issuer, X500Name issuerName, X500Name subjectX500){
        try {
            // subject key identifier
            SubjectKeyIdentifier ski = createSubjectKeyIdentifier(publicKey);
            data.setSubjectKeyIdentifier(ski.getEncoded().toString());
            builder.addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyIdentifier(publicKey));
            // basic constraints ("is CA?")
            builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(data.isCA()));
            // key usage
            builder.addExtension(Extension.keyUsage, false, makeKeyUsage(data));
            if (!data.isCA()) {
                // extended key usage
                builder.addExtension(Extension.extendedKeyUsage, false, makeExtendedKeyUsage(data));
            }
            // subject alternative name
            GeneralName generalName = new GeneralName(GeneralName.dNSName, data.getCommonName());
            GeneralNames generalNames = new GeneralNames(generalName);
            builder.addExtension(Extension.subjectAlternativeName, false, generalNames);
            if (data.isCA()){
                // CRL distribution point
                List<DistributionPoint> list = new ArrayList<>();
                GeneralName gn2 = new GeneralName(GeneralName.uniformResourceIdentifier,  new DERIA5String("https://localhost:9000/crl/" + data.getSerialNumber() + ".crl"));
                GeneralName gn1 = new GeneralName(GeneralName.uniformResourceIdentifier,  new DERIA5String("https://localhost:9000/api/crl/" + data.getSerialNumber()));
                GeneralNames gns = new GeneralNames(new GeneralName[] {gn1, gn2});
                DistributionPointName distributionPointName = new DistributionPointName(gns);
                GeneralName issuerGN = new GeneralName(subjectX500);
                DistributionPoint point = new DistributionPoint(distributionPointName, new ReasonFlags(ReasonFlags.unused), null);
                DistributionPoint[] points = new DistributionPoint[1];
                points[0] = point;
                CRLDistPoint crlDistPoint = new CRLDistPoint(points);
                builder.addExtension(Extension.cRLDistributionPoints, false, crlDistPoint);
            }
            if (issuer != null){
                // authority information access
                GeneralName gn = new GeneralName(GeneralName.uniformResourceIdentifier, new DERIA5String("https://localhost:9000/certificates/" + issuer.getCertificate().getCertificateData().getSerialNumber() + ".cer"));
                //GeneralName gn = new GeneralName(GeneralName.uniformResourceIdentifier, new DERIA5String("https://localhost:8080/api/cert/cerFile/" + issuer.getCertificate().getCertificateData().getSerialNumber()));
                AccessDescription accessDescription = new AccessDescription(AccessDescription.id_ad_caIssuers, gn);

                AuthorityInformationAccess authorityInformationAccess = new AuthorityInformationAccess(accessDescription);
                builder.addExtension(Extension.authorityInfoAccess, false, authorityInformationAccess);
            }

        } catch (CertIOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private KeyUsage makeKeyUsage(CertificateData data){
        int usage = 0;
        switch (data.getCertUsage()) {
            case CA:
                usage |= KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign;
                break;
            case HTTPS:
                usage |= KeyUsage.keyAgreement;
                break;
            case MAIL:
                usage |= KeyUsage.dataEncipherment | KeyUsage.digitalSignature | KeyUsage.nonRepudiation;
                break;
            case DOC_SIGN:
                usage |= KeyUsage.nonRepudiation | KeyUsage.digitalSignature | KeyUsage.dataEncipherment;
                break;
            default:
                usage |= KeyUsage.digitalSignature;
        }
        return new KeyUsage(usage);
    }

    private ExtendedKeyUsage makeExtendedKeyUsage(CertificateData data){
        List<KeyPurposeId> list = new ArrayList<>();
        switch (data.getCertUsage()) {
            case HTTPS:
                list.add(KeyPurposeId.id_kp_serverAuth);
                list.add(KeyPurposeId.id_kp_clientAuth);
                break;
            case MAIL:
                list.add(KeyPurposeId.id_kp_emailProtection);
                break;
            case DOC_SIGN:
                list.add(KeyPurposeId.id_kp_codeSigning);
                list.add(KeyPurposeId.anyExtendedKeyUsage);
                break;
        }
        KeyPurposeId[] purposes = new KeyPurposeId[list.size()];
        for (int i = 0; i < purposes.length; i++)
            purposes[i] = list.get(i);
        return new ExtendedKeyUsage(purposes);
    }

    private SubjectKeyIdentifier createSubjectKeyIdentifier(PublicKey key) throws IOException {
        ByteArrayInputStream bIn = new ByteArrayInputStream(key.getEncoded());
        ASN1InputStream is = null;
        try {
            is = new ASN1InputStream(bIn);
            ASN1Sequence seq = (ASN1Sequence) is.readObject();
            SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(seq);
            return new BcX509ExtensionUtils().createSubjectKeyIdentifier(info);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        return null;
    }

}
