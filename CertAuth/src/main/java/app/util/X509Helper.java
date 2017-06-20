package app.util;

import app.beans.*;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class X509Helper {

    public static X500Name makeX500Name(CertificateData data){
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

    private static void addRDN(X500NameBuilder builder, ASN1ObjectIdentifier id, String value){
        if (value != null && !value.equals(""))
            builder.addRDN(id, value);
    }

    public static CertificateData readX500Name(X500Name x500Name){
        CertificateData data = new CertificateData();
        data.setCommonName(getRDN(x500Name, BCStyle.CN));
        data.setSurname(getRDN(x500Name, BCStyle.SURNAME));
        data.setGivenName(getRDN(x500Name, BCStyle.GIVENNAME));
        data.setOrganization(getRDN(x500Name, BCStyle.O));
        data.setOrganizationalUnit(getRDN(x500Name, BCStyle.OU));
        data.setCountryCode(getRDN(x500Name, BCStyle.C));
        data.setEmailAddress(getRDN(x500Name, BCStyle.E));

        return data;
    }

    private static String getRDN(X500Name x500Name, ASN1ObjectIdentifier id){
        RDN[] rdn = x500Name.getRDNs(id);//[0].getFirst().getValue();
        if (rdn != null && rdn.length > 0){
            ASN1Encodable asn = rdn[0].getFirst().getValue();
            String value = IETFUtils.valueToString(asn);
            if (!"".equals(value))
                return value;
        }
        return null;
    }

    public static void makeExtensions(X509v3CertificateBuilder builder, CertificateData data, PublicKey publicKey, CertificateAuthority issuer, X500Name issuerName, X500Name subjectX500){
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
                GeneralName gn = new GeneralName(GeneralName.uniformResourceIdentifier, new DERIA5String("https://localhost:8080/certificates/" + issuer.getCertificate().getCertificateData().getSerialNumber() + ".cer"));
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

    private static KeyUsage makeKeyUsage(CertificateData data){
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

    private static ExtendedKeyUsage makeExtendedKeyUsage(CertificateData data){
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

    private static SubjectKeyIdentifier createSubjectKeyIdentifier(PublicKey key) throws IOException {
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

    public static X509Certificate readCertificateFromFile(app.beans.Certificate cert){
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            File file = new File(cert.getCerFileName());
            InputStream in = new ByteArrayInputStream(Files.readAllBytes(file.toPath()));
            X509Certificate x509 = (X509Certificate) certFactory.generateCertificate(in);
            return x509;
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
