package com.example.manage_contacts.service;

import com.example.manage_contacts.dto.ContactDTO;
import com.example.manage_contacts.dto.InteractionDTO;
import com.example.manage_contacts.dto.InteractionStatisticsDTO;
import com.example.manage_contacts.dto.ResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ContactService {

    ResponseDTO<Page<ContactDTO>> getListContactUser(String name, String phone, String email, Integer page);

    ResponseDTO<ContactDTO> create(ContactDTO contactDTO);

    ResponseDTO<ContactDTO> edit(ContactDTO updateContactDTO);

    ResponseDTO<Void> delete(UUID contactDTO);

    ResponseDTO<ContactDTO> fetchContactDetailsById(UUID contactId);
    ResponseDTO<ContactDTO> fetchContactDetailsByPhone(String phone);

    ResponseDTO<List<InteractionDTO>> getContactHistory(UUID contactId, int page);

    ResponseDTO<InteractionStatisticsDTO> getStatistics();


}
