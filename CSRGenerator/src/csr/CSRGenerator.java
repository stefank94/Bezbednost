package csr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemReader;

public class CSRGenerator {
	
	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	public CSRGenerator() { }
	
	public void generateCSR(){
		try {
			KeyPair keyPair = generateKeyPair("RSA", 2048);
			X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
			builder.addRDN(BCStyle.CN, "Olaf Bergsson");
			builder.addRDN(BCStyle.SURNAME, "Bergsson");
			builder.addRDN(BCStyle.GIVENNAME, "Olaf");
			builder.addRDN(BCStyle.O, "University of Bergen");
			builder.addRDN(BCStyle.OU, "Department of pharmacognosy");
			builder.addRDN(BCStyle.C, "Norway");
			builder.addRDN(BCStyle.E, "olaf.bergsson@uib.ac.no");
			X500Name x500Name = builder.build();
			ContentSigner signGen = new JcaContentSignerBuilder("SHA1withRSA").build(keyPair.getPrivate());
			PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(x500Name, keyPair.getPublic());
			PKCS10CertificationRequest csr = csrBuilder.build(signGen);
			String fileName = "./data/request.csr";
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
    
    public static void main(String[] args){
    	CSRGenerator generator = new CSRGenerator();
    	generator.generateCSR();
    	System.out.println("CSR created.");
    	generator.readCSR();
    }
    
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
    
}
