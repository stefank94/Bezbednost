package csr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

public class CSRGenerator {
	
	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	private KeyPair keyPair;
	private X500Name x500Name;
	
	public CSRGenerator() { }
	
	public void generateCSR(SubjectData data, String fileName){
		try {
			KeyPair keyPair = generateKeyPair("RSA", 2048);
			this.keyPair = keyPair;
			X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
			addRDN(builder, BCStyle.CN, data.getCommonName());
			addRDN(builder, BCStyle.SURNAME, data.getSurname());
			addRDN(builder, BCStyle.GIVENNAME, data.getGivenName());
			addRDN(builder, BCStyle.O, data.getOrganization());
			addRDN(builder, BCStyle.OU, data.getOrganizationalUnit());
			addRDN(builder, BCStyle.C, data.getCountryCode());
			addRDN(builder, BCStyle.E, data.getEmail());
			X500Name x500Name = builder.build();
			this.x500Name = x500Name;
			ContentSigner signGen = new JcaContentSignerBuilder("SHA1withRSA").build(keyPair.getPrivate());
			PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(x500Name, keyPair.getPublic());
			PKCS10CertificationRequest csr = csrBuilder.build(signGen);
			File file = new File(fileName);
			file.createNewFile();
			FileOutputStream os = new FileOutputStream(file, false);
			OutputStreamWriter output = new OutputStreamWriter(os);
			PEMWriter pem = new PEMWriter(output);
			pem.writeObject(csr);
			pem.close();
			os.close();
		} catch (OperatorCreationException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addRDN(X500NameBuilder builder, ASN1ObjectIdentifier id, String value){
		if (value != null && !value.equals(""))
			builder.addRDN(id, value);
	}
 
    private KeyPair generateKeyPair(String alg, int keySize) {
    	KeyPairGenerator gen;
		try {
			gen = KeyPairGenerator.getInstance(alg);
			gen.initialize(keySize);
	    	KeyPair pair = gen.generateKeyPair();
	    	return pair;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	return null;
    }

	public KeyPair getKeyPair() {
		return keyPair;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	public X500Name getX500Name() {
		return x500Name;
	}

	public void setX500Name(X500Name x500Name) {
		this.x500Name = x500Name;
	}
	
    
    /*
    public void readCSR(){
    	try {
	    	FileReader fileReader = new FileReader("./data/request.csr");
	    	PemReader pemReader = new PemReader(fileReader);
	    	PKCS10CertificationRequest csr = new PKCS10CertificationRequest(pemReader.readPemObject().getContent());
	    	pemReader.close();
	    	fileReader.close();
	    	ASN1Encodable a = csr.getSubject().getRDNs(BCStyle.CN)[0].getFirst().getValue();
	    	System.out.println(IETFUtils.valueToString(a));
    	} catch (IOException e){
    		e.printStackTrace();
    	}
    }
    */
    
}
