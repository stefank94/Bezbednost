package app.serviceImpl;

import app.beans.CRLInformation;
import app.beans.Certificate;
import app.beans.CertificateAuthority;
import app.beans.Revocation;
import app.exception.EntityNotFoundException;
import app.exception.InvalidDataException;
import app.repository.CARepository;
import app.repository.CRLInformationRepository;
import app.service.CAService;
import app.service.CRLService;
import app.task.TaskManager;
import app.util.KeyStoreReader;
import app.util.ParameterHelper;
import app.util.X509Helper;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class CRLServiceImpl implements CRLService{

    private static final String FOLDER = "src" + File.separator + "main" + File.separator + "webapp" +
            File.separator + "crl" + File.separator;

    @Autowired
    private CRLInformationRepository crlInformationRepository;

    @Autowired
    private CAService caService;

    @Autowired
    private TaskManager taskManager;

    // -------------------------------------------------------

    @Override
    public Date issueCRL(CertificateAuthority ca, Date nextUpdate) { // Returns date of execution
        try {
            // Information about this CA's CRLs
            CRLInformation crlInformation = ca.getCrlInformation();
            // Private key from the issuing CA
            KeyStoreReader reader = new KeyStoreReader();
            PrivateKey privateKey = reader.readPrivateKey(ca);
            X509Certificate x509 = X509Helper.readCertificateFromFile(ca.getCertificate());
            // The CA's information from its certificate
            X500Name issuerName = new X500Name(PrincipalUtil.getIssuerX509Principal(x509).getName());
            Date now = new Date();
            X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(issuerName, now);

            crlBuilder.setNextUpdate(nextUpdate);

            // add all revoked certificates
            for (Certificate cert : ca.getIssuedCertificates()){
                Revocation rev = cert.getRevocation();
                if (rev != null){
                    int reasonCode = rev.getReasonCode();
                    if (rev.getInvalidityDate() != null)
                        crlBuilder.addCRLEntry(BigInteger.valueOf(cert.getCertificateData().getSerialNumber()),
                                rev.getRevocationDate(),
                                reasonCode,
                                rev.getInvalidityDate());
                    else
                        crlBuilder.addCRLEntry(BigInteger.valueOf(cert.getCertificateData().getSerialNumber()),
                                rev.getRevocationDate(),
                                reasonCode);
                }
            }

            // add CRL extensions
            // Authority Key Identifier :
            GeneralName generalName = new GeneralName(issuerName);
            GeneralNames generalNames = new GeneralNames(generalName);
            int serialNumber = ca.getCertificate().getCertificateData().getSerialNumber();
            AuthorityKeyIdentifier authorityKeyIdentifier = new AuthorityKeyIdentifier(generalNames,
                    BigInteger.valueOf(serialNumber));
            crlBuilder.addExtension(Extension.authorityKeyIdentifier, false, authorityKeyIdentifier);
            // CRL number :
            int number = crlInformation.incrementAndReturnNumber();
            CRLNumber crlNumber = new CRLNumber(BigInteger.valueOf(number));
            crlBuilder.addExtension(Extension.cRLNumber, false, crlNumber);

            // sign with private key
            ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(privateKey);
            X509CRLHolder crl = crlBuilder.build(signer);

            // write to file
            String filename = FOLDER + serialNumber + ".crl";
            File crlFile = new File(filename);
            if (crlFile.exists())
                crlFile.delete();
            crlFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(crlFile);
            fos.write(crl.getEncoded());
            fos.flush();
            fos.close();

            // Save CRL information
            crlInformation.setCurrentIssued(now);
            crlInformation.setNextIssued(nextUpdate);
            crlInformation.setCrlFilename(filename);
            saveCRLInformation(crlInformation);

            return now;

        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CRLInformation saveCRLInformation(CRLInformation crlInformation) {
        return crlInformationRepository.save(crlInformation);
    }

    @Override
    public void addCAToSchedule(CertificateAuthority ca) {
        taskManager.addToSchedule(ca);
    }

    @Override
    public void addCAListToSchedule(List<CertificateAuthority> list) {
        taskManager.addListToSchedule(list);
    }

    @Override
    public void rescheduleCRLExecution(int id, String cronExp, String frequencyDescription) throws EntityNotFoundException, InvalidDataException {
        CertificateAuthority ca = caService.findById(id);
        taskManager.reschedule(ca, cronExp, frequencyDescription);
    }

    @Override
    public void rescheduleCRLExecutionForAll(String cronExp, String frequencyDescription) throws InvalidDataException {
        taskManager.rescheduleAll(cronExp, frequencyDescription);
        ParameterHelper.setDefaultCronAndDescription(cronExp, frequencyDescription);
    }

    @Override
    public void cancelExecution(CertificateAuthority ca) {
        taskManager.removeFromSchedule(ca);
    }

}
