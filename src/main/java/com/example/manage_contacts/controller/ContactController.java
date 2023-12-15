package com.example.manage_contacts.controller;

import com.example.manage_contacts.dto.*;
import com.example.manage_contacts.service.ContactService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;


    @GetMapping(value = "/list-contact")
    public ResponseEntity<ResponseDTO<Page<ContactDTO>>> listContact(@RequestParam(defaultValue = "", required = false) String phone,
                                                                     @RequestParam(defaultValue = "", required = false) String name,
                                                                     @RequestParam(defaultValue = "", required = false) String email,
                                                                     @RequestParam(defaultValue = "1", required = false) Integer page) {
        ResponseDTO<Page<ContactDTO>> responseDTO = contactService.getListContactUser(name, phone, email, page);

        return ResponseEntity
                .status(responseDTO.getStatus())
                .body(responseDTO);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<ContactDTO>> create(@RequestBody ContactDTO contactDTO) {
        ResponseDTO<ContactDTO> responseDTO = contactService.create(contactDTO);
        return ResponseEntity
                .status(responseDTO.getStatus())
                .body(responseDTO);
    }


    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<ContactDTO>> edit(@RequestBody ContactDTO updatedContactDTO) {
        ResponseDTO<ContactDTO> responseDTO = contactService.edit(updatedContactDTO);
        return ResponseEntity
                .status(responseDTO.getStatus())
                .body(responseDTO);
    }


    @PostMapping(value = "/delete/{id}")
    public ResponseEntity<ResponseDTO<Void>> delete(@PathVariable UUID id) {
        ResponseDTO<Void> responseDTO = contactService.delete(id);
        return ResponseEntity
                .status(responseDTO.getStatus())
                .body(responseDTO);
    }


    @GetMapping(value = "/detailsById")
    public ResponseEntity<ResponseDTO<ContactDTO>> getDetails(@RequestParam(defaultValue = "") UUID idContact) {
        ResponseDTO<ContactDTO> responseDTO = contactService.fetchContactDetailsById(idContact);
        return ResponseEntity
                .status(responseDTO.getStatus())
                .body(responseDTO);
    }


    @GetMapping(value = "/detailsByPhone")
    public ResponseEntity<ResponseDTO<ContactDTO>> getDetails(@RequestParam(defaultValue = "") String phone) {
        ResponseDTO<ContactDTO> responseDTO = contactService.fetchContactDetailsByPhone(phone);
        return ResponseEntity
                .status(responseDTO.getStatus())
                .body(responseDTO);
    }


    @GetMapping(value = "/{contactId}/interactions")
    public ResponseEntity<ResponseDTO<List<InteractionDTO>>> getContactHistory(@PathVariable UUID contactId, @RequestParam(defaultValue = "1") int page) {
        ResponseDTO<List<InteractionDTO>> responseDTO = contactService.getContactHistory(contactId, page);
        return ResponseEntity
                .status(responseDTO.getStatus())
                .body(responseDTO);
    }


    @GetMapping(value = "/interactions/statistic")
    public ResponseEntity<ResponseDTO<InteractionStatisticsDTO>> getContactHistory() {
        ResponseDTO<InteractionStatisticsDTO> responseDTO = contactService.getStatistics();
        return ResponseEntity
                .status(responseDTO.getStatus())
                .body(responseDTO);
    }

}
