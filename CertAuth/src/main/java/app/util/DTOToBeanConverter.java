package app.util;

import app.beans.*;
import app.dto.*;

public class DTOToBeanConverter {

    public static User userDTOToBean(UserDTO dto){
        if (dto == null)
            return null;
        User user;
        if (dto instanceof AdminDTO)
            user = adminDTOToBean((AdminDTO) dto);
        else
            user = clientDTOToBean((ClientDTO) dto);
        return user;
    }

    public static Admin adminDTOToBean(AdminDTO dto){
        if (dto == null)
            return null;
        Admin admin = new Admin();
        admin.setEmail(dto.getEmail());
        return admin;
    }

    public static Client clientDTOToBean(ClientDTO dto){
        if (dto == null)
            return null;
        Client client = new Client();
        client.setEmail(dto.getEmail());
        return client;
    }

    public static CertificateData certificateDataDTOToBean(CertificateDataDTO dto){
        if (dto == null)
            return null;
        CertificateData data = new CertificateData();
        data.setOrganization(dto.getOrganization());
        data.setOrganizationalUnit(dto.getOrganizationalUnit());
        data.setSurname(dto.getSurname());
        data.setGivenName(dto.getGivenName());
        data.setCommonName(dto.getCommonName());
        data.setCountryCode(dto.getCountryCode());
        data.setEmailAddress(dto.getEmailAddress());
        data.setCertUsage(dto.getUsage());
        return data;
    }

}
