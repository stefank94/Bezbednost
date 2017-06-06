package app.util;

import app.beans.*;
import app.dto.*;

public class BeanToDTOConverter {

    public static void buildAbstractEntityDTO(AbstractEntityDTO dto, AbstractEntity ae){
        dto.setId(ae.getId());
        dto.setDeleted(ae.isDeleted());
    }

    public static UserDTO userToDTO(User user){
        if (user == null)
            return null;
        UserDTO dto;
        if (user instanceof Admin)
            dto = adminToDTO((Admin) user);
        else
            dto = clientToDTO((Client) user);
        return dto;
    }

    public static AdminDTO adminToDTO(Admin admin){
        if (admin == null)
            return null;
        AdminDTO dto = new AdminDTO();
        buildAbstractEntityDTO(dto, admin);
        dto.setEmail(admin.getEmail());
        dto.setSignupDate(admin.getSignupDate());
        return dto;
    }

    public static ClientDTO clientToDTO(Client client){
        if (client == null)
            return null;
        ClientDTO dto = new ClientDTO();
        buildAbstractEntityDTO(dto, client);
        dto.setSignupDate(client.getSignupDate());
        dto.setEmail(client.getEmail());
        return dto;
    }

    public static CertificateDataDTO certificateDataToDTO(CertificateData data){
        if (data == null)
            return null;
        CertificateDataDTO dto = new CertificateDataDTO();
        buildAbstractEntityDTO(dto, data);
        dto.setOrganization(data.getOrganization());
        dto.setKeyAlgorithm(data.getKeyAlgorithm());
        dto.setCA(data.isCA());
        dto.setCommonName(data.getCommonName());
        dto.setCountryCode(data.getCountryCode());
        dto.setEmailAddress(data.getEmailAddress());
        dto.setGivenName(data.getGivenName());
        dto.setOrganizationalUnit(data.getOrganizationalUnit());
        dto.setPublicKey(data.getPublicKey());
        dto.setSurname(data.getSurname());
        dto.setUid(data.getUid());
        return dto;
    }

    public static CertificateDTO certificateToDTO(Certificate cert){
        if (cert == null)
            return null;
        CertificateDTO dto = new CertificateDTO();
        buildAbstractEntityDTO(dto, cert);
        dto.setCertificateData(certificateDataToDTO(cert.getCertificateData()));
        dto.setIssuer(cert.getIssuer() == null ? -1 : cert.getIssuer().getId());
        dto.setUser(cert.getUser() == null ? null : cert.getUser().getEmail());
        dto.setValidFrom(cert.getValidFrom());
        dto.setValidTo(cert.getValidTo());
        return dto;
    }

    public static CertificateAuthorityDTO certificateAuthorityToDTO(CertificateAuthority ca){
        if (ca == null)
            return null;
        CertificateAuthorityDTO dto = new CertificateAuthorityDTO();
        buildAbstractEntityDTO(dto, ca);
        dto.setIssuer(ca.getIssuer() == null ? -1 : ca.getIssuer().getId());
        dto.setCertificate(certificateToDTO(ca.getCertificate()));
        return dto;
    }

    public static CertificateSigningRequestDTO certificateSigningRequestToDTO(CertificateSigningRequest request){
        if (request == null)
            return null;
        CertificateSigningRequestDTO dto = new CertificateSigningRequestDTO();
        buildAbstractEntityDTO(dto, request);
        dto.setUser(request.getUser().getEmail());
        dto.setState(request.getState());
        dto.setCertificateData(certificateDataToDTO(request.getCertificateData()));
        dto.setDate(request.getDate());
        return dto;
    }

}
