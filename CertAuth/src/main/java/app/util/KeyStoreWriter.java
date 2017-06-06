package app.util;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;

public class KeyStoreWriter {

    private KeyStore keyStore;

    private static final String folder = "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "keystores" + File.separator;

    public KeyStoreWriter() {
        try {
            this.keyStore = KeyStore.getInstance("JKS", "SUN");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public void loadKeyStore(String fileName, char[] password) {
        try {
            if(fileName != null)
                keyStore.load(new FileInputStream(folder + fileName), password);
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

    public void saveKeyStore(String fileName, char[] password) {
        try {
            keyStore.store(new FileOutputStream(folder + fileName), password);
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

    public void writePrivateKey(String alias, PrivateKey privateKey, char[] password, Certificate certificate) {
        try {
            keyStore.setKeyEntry(alias, privateKey, password, new Certificate[] {certificate});
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public void writeCertificate(String alias, Certificate certificate){
        try{
            keyStore.setCertificateEntry(alias, certificate);
        } catch (KeyStoreException e){
            e.printStackTrace();
        }
    }

    public void saveCertificateToKeyStore(app.beans.Certificate cert, X509Certificate x509){
        loadKeyStore(null, cert.getKeyStorePassword().toCharArray());
        writeCertificate(cert.getKeyStoreAlias(), x509);
        saveKeyStore(cert.getKeyStoreFileName(), cert.getKeyStorePassword().toCharArray());
    }



}
