package app.util;

import app.beans.Admin;
import app.beans.OrganizationUser;
import app.beans.PersonUser;
import app.beans.User;
import app.dto.AdminDTO;
import app.dto.OrganizationUserDTO;
import app.dto.PersonUserDTO;
import app.dto.UserDTO;

public class DTOToBeanConverter {

    public static User userDTOToBean(UserDTO dto){
        if (dto == null)
            return null;
        User user;
        if (dto instanceof AdminDTO)
            user = adminDTOToBean((AdminDTO) dto);
        else if (dto instanceof PersonUserDTO)
            user = personUserDTOToBean((PersonUserDTO) dto);
        else
            user = organizationUserDTOToBean((OrganizationUserDTO) dto);
        return user;
    }

    public static Admin adminDTOToBean(AdminDTO dto){
        if (dto == null)
            return null;
        Admin admin = new Admin();
        admin.setPassword(dto.getPassword());
        admin.setEmail(dto.getEmail());
        return admin;
    }

    public static PersonUser personUserDTOToBean(PersonUserDTO dto){
        if (dto == null)
            return null;
        PersonUser person = new PersonUser();
        person.setEmail(dto.getEmail());
        person.setPassword(dto.getPassword());
        person.setLastName(dto.getLastName());
        person.setName(dto.getName());
        return person;
    }

    public static OrganizationUser organizationUserDTOToBean(OrganizationUserDTO dto){
        if (dto == null)
            return null;
        OrganizationUser org = new OrganizationUser();
        org.setName(dto.getName());
        org.setPassword(dto.getPassword());
        org.setEmail(dto.getEmail());
        org.setAddress(dto.getAddress());
        org.setCountry(dto.getCountry());
        return org;
    }

}
