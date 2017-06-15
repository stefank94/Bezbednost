package csr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class KeyStoreWriter {

    private KeyStore keyStore;
    private String fileName;

    public KeyStoreWriter(String fileName) {
        try {
        	this.fileName = fileName;
            this.keyStore = KeyStore.getInstance("JKS", "SUN");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public void loadKeyStore(char[] password) {
        try {
        	File file = new File(fileName);
            if(file.exists())
                keyStore.load(new FileInputStream(fileName), password);
            else
                keyStore.load(null, password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveKeyStore(char[] password) {
        try {
            keyStore.store(new FileOutputStream(fileName), password);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePrivateKey(String alias, KeyPair keyPair, char[] password, X500Name subject) {
        try {
        	Certificate cert = generateSelfSignerCertificate(keyPair, subject);
            keyStore.setKeyEntry(alias, keyPair.getPrivate(), password, new Certificate[] {cert});
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }
    
    // I need this to write with to KeyStore along with the private key
    private Certificate generateSelfSignerCertificate(KeyPair keyPair, X500Name subject){
    	try {
	    	JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
	        builder = builder.setProvider("BC");
	        ContentSigner contentSigner = builder.build(keyPair.getPrivate());
	        Date now = new Date();
	        SecureRandom random = new SecureRandom();
	        int randomNumber = random.nextInt(Integer.MAX_VALUE - 1);
	        
	        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(subject,
	                new BigInteger(Integer.toString(randomNumber)),
	                now,
	                now,
	                subject,
	                keyPair.getPublic());
	        X509CertificateHolder certHolder = certGen.build(contentSigner);
	        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
	        certConverter = certConverter.setProvider("BC");
	
	        return certConverter.getCertificate(certHolder);
    	} catch (CertificateException e){
    		e.printStackTrace();
    	} catch (OperatorCreationException e) {
			e.printStackTrace();
		}
        return null;
    }


}
