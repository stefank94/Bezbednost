package app.util;

import app.beans.CertificateData;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PublicKey;
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

    public static void makeExtensions(X509v3CertificateBuilder builder, CertificateData data, PublicKey publicKey){
        try {
            SubjectKeyIdentifier ski = createSubjectKeyIdentifier(publicKey);
            data.setSubjectKeyIdentifier(ski.getEncoded().toString());
            builder.addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyIdentifier(publicKey));
            builder.addExtension(Extension.basicConstraints, false, new BasicConstraints(data.isCA()));
            builder.addExtension(Extension.keyUsage, false, makeKeyUsage(data));
            if (!data.isCA())
                builder.addExtension(Extension.extendedKeyUsage, false, makeExtendedKeyUsage(data));
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
        return new ExtendedKeyUsage((KeyPurposeId[]) list.toArray());
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

}
