package app.config;

import app.beans.Admin;
import app.beans.CertificateData;
import app.service.AdminService;
import app.service.CAService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Security;
import java.util.Date;

@Component
public class Initializer implements InitializingBean {
    /*
    Sets up data for the application.
     */

    @Value("${initializeRootCA}")
    private boolean initializeRootCA;

    @Autowired
    private CAService caService;

    @Autowired
    private AdminService adminService;

    // -------------------------------------------

    @Override
    public void afterPropertiesSet() throws Exception {
        // Add Bouncy Castle Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        if (initializeRootCA){
            if (caService.getRootCA() != null) // Don't generate root CA if it already exists
                return;

            CertificateData data = new CertificateData();
            data.setCommonName("My Root CA");
            data.setCA(true);
            data.setCountryCode("RS");
            data.setEmailAddress("root@root.com");
            data.setGivenName("My");
            data.setSurname("Root CA");
            data.setKeyAlgorithm("RSA");
            data.setOrganization("Root CA Organization");
            data.setOrganizationalUnit("Level 1");
            caService.generateRootCA(data);

        }

        if (!adminService.adminExists()){
            Admin admin = new Admin();
            admin.setEmail("admin@admin.com");
            admin.setPassword("123");
            admin.setSalt("");
            admin.setSignupDate(new Date());
            adminService.create(admin);
        }

    }
}
