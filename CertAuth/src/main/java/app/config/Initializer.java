package app.config;

import app.beans.Admin;
import app.beans.CertificateAuthority;
import app.beans.CertificateData;
import app.dto.AdminDTO;
import app.service.AdminService;
import app.service.CAService;
import app.service.CRLService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.security.Security;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private CRLService crlService;

    private static final String FOLDER = "src" + File.separator + "main" + File.separator + "webapp" + File.separator;

    // -------------------------------------------

    @Override
    public void afterPropertiesSet() throws Exception {
        // Add Bouncy Castle Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        createFolders();

        // Add all CAs to CRL generation scheduler.
        List<CertificateAuthority> all = caService.getAllCAs();
        crlService.addCAListToSchedule(all);

        if (initializeRootCA){

            if (caService.getRootCA() != null)  // Don't generate root CA if it already exists,
                return;

            try {
                FileUtils.cleanDirectory(new File(FOLDER + "certificates" + File.separator));
                FileUtils.cleanDirectory(new File(FOLDER + "crl" + File.separator));
                FileUtils.cleanDirectory(new File(FOLDER + "keystores" + File.separator));
            } catch (Exception e) { }

            caService.generateRootCA();
            caService.generateHTTPSCertificate();

        }

        if (!adminService.adminExists()){
            AdminDTO admin = new AdminDTO();
            admin.setEmail("admin@admin.com");
            admin.setPassword("123");
            adminService.create(admin);
        }

    }

    public void createFolders(){
        File certificatesFolder = new File("src/main/webapp/certificates");
        File keystoresFolder = new File("src/main/webapp/keystores");
        File crlFolder = new File("src/main/webapp/crl");
        if (!certificatesFolder.exists())
            certificatesFolder.mkdir();
        if (!keystoresFolder.exists())
            keystoresFolder.mkdir();
        if (!crlFolder.exists())
            crlFolder.mkdir();
    }
}
