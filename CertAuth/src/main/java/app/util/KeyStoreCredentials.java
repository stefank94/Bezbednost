package app.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public class KeyStoreCredentials {

    private String keyStoreFileName;
    private String keyStoreAlias;
    private String keyStorePassword;
    private String privateKeyPassword;

    public KeyStoreCredentials() { }


    public static KeyStoreCredentials generateKeyStoreCredentials(String identifier){
        KeyStoreCredentials credentials = new KeyStoreCredentials();
        SecureRandom random = new SecureRandom();
        credentials.setKeyStoreFileName(identifier + "_PK.jks");
        credentials.setPrivateKeyPassword(new BigInteger(130, random).toString(32));
        credentials.setKeyStoreAlias(new BigInteger(130, random).toString(32));
        credentials.setKeyStorePassword(new BigInteger(130, random).toString(32));

        return credentials;
    }

    public String getKeyStoreFileName() {
        return keyStoreFileName;
    }

    public void setKeyStoreFileName(String keyStoreFileName) {
        this.keyStoreFileName = keyStoreFileName;
    }

    public String getKeyStoreAlias() {
        return keyStoreAlias;
    }

    public void setKeyStoreAlias(String keyStoreAlias) {
        this.keyStoreAlias = keyStoreAlias;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public void setPrivateKeyPassword(String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }
}
