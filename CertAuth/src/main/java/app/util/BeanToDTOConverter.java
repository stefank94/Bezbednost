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
        else if (user instanceof PersonUser)
            dto = personUserToDTO((PersonUser) user);
        else
            dto = organizationUserToDTO((OrganizationUser) user);
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

    public static PersonUserDTO personUserToDTO(PersonUser person){
        if (person == null)
            return null;
        PersonUserDTO dto = new PersonUserDTO();
        buildAbstractEntityDTO(dto, person);
        dto.setSignupDate(person.getSignupDate());
        dto.setEmail(person.getEmail());
        dto.setLastName(person.getLastName());
        dto.setName(person.getName());
        return dto;
    }

    public static OrganizationUserDTO organizationUserToDTO(OrganizationUser org){
        if (org == null)
            return null;
        OrganizationUserDTO dto = new OrganizationUserDTO();
        buildAbstractEntityDTO(dto, org);
        dto.setName(org.getName());
        dto.setEmail(org.getEmail());
        dto.setAddress(org.getAddress());
        dto.setCountry(org.getCountry());
        dto.setSignupDate(org.getSignupDate());
        return dto;
    }

}
