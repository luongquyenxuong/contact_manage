package com.example.manage_contacts.mapper;

import com.example.manage_contacts.constant.Constant;
import com.example.manage_contacts.dto.ContactDTO;
import com.example.manage_contacts.entity.Contact;

import java.sql.Timestamp;
import java.util.UUID;

public class ContactMapper {
    private ContactMapper() {
    }

    public static Contact convertToEntity(ContactDTO contactDTO) {
        Contact contact = new Contact();
        contact.setId(contactDTO.getId());
        contact.setName(contactDTO.getName());
        contact.setEmail(contactDTO.getEmail());
        contact.setPhoneNumber(contactDTO.getPhone());
        contact.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        contact.setUpdateDate(contactDTO.getUpdatedAt());
        contact.setDeletedDate(contactDTO.getDeletedAt());
        contact.setStatus(Constant.ACTIVE);
        return contact;
    }

    public static ContactDTO convertToDto(Contact contact , UUID idUser) {
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setId(contact.getId());
        contactDTO.setName(contact.getName());
        contactDTO.setEmail(contact.getEmail());
        contactDTO.setPhone(contact.getPhoneNumber());
        contactDTO.setCreatedAt(contact.getCreatedDate());
        contactDTO.setUpdatedAt(contact.getUpdateDate());
        contactDTO.setDeletedAt(contact.getDeletedDate());
        contactDTO.setIdUser(idUser);
        return contactDTO;
    }
}
