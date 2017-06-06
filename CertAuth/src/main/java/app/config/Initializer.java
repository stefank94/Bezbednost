package app.config;

import app.beans.CertificateAuthority;
import app.beans.CertificateData;
import app.service.CertificateService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Initializer implements InitializingBean {

    @Value("${setupCAsOnStartup}")
    private boolean setupCAsOnStartup;

    @Autowired
    private CertificateService certificateService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (setupCAsOnStartup){
            CertificateData data = new CertificateData();
            data.setCommonName("My Root CA");
            data.setUid("myrootca");
            data.setCA(true);
            data.setCountryCode("RS");
            data.setEmailAddress("root@root.com");
            data.setGivenName("My");
            data.setSurname("Root CA");
            data.setKeyAlgorithm("RSA");
            data.setOrganization("Root CA Organization");
            data.setOrganizationalUnit("Level 1");

            CertificateAuthority root = certificateService.generateRootCA(data);
            data.setCommonName("Intermediate CA 1");
            data.setUid("int1");
            data.setEmailAddress("inter1@root.com");
            data.setGivenName("Intermediate");
            data.setSurname("CA 1");
            data.setOrganizationalUnit("Level 2");
            certificateService.generateCertificateAuthority(root, data);

            data.setCommonName("Intermediate CA 2");
            data.setUid("int2");
            data.setEmailAddress("inter2@root.com");
            data.setGivenName("Intermediate");
            data.setSurname("CA 2");
            data.setOrganizationalUnit("Level 2");
            certificateService.generateCertificateAuthority(root, data);

            data.setCommonName("Intermediate CA 3");
            data.setUid("int3");
            data.setEmailAddress("inter3@root.com");
            data.setGivenName("Intermediate");
            data.setSurname("CA 3");
            data.setOrganizationalUnit("Level 2");
            certificateService.generateCertificateAuthority(root, data);
        }
    }
}
