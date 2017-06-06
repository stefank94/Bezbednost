package app.util;

import app.beans.Certificate;
import app.beans.CertificateData;
import app.beans.CertificateSigningRequest;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class X500NameUtility {

    public static X500Name makeX500Name(CertificateData data){
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, data.getCommonName()); //common name
        builder.addRDN(BCStyle.SURNAME, data.getSurname()); // surname
        builder.addRDN(BCStyle.GIVENNAME, data.getGivenName()); // given name
        builder.addRDN(BCStyle.O, data.getOrganization()); // organization
        builder.addRDN(BCStyle.OU, data.getOrganizationalUnit()); // organizational unit
        builder.addRDN(BCStyle.C, data.getCountryCode()); // country code
        builder.addRDN(BCStyle.E, data.getEmailAddress()); // email address
        builder.addRDN(BCStyle.UID, data.getUid()); // LDAP user id

        return builder.build();
    }

}
