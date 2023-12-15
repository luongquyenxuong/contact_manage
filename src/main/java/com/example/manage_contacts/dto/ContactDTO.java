package com.example.manage_contacts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDTO {
    private UUID id;
    private UUID idUser;
    private String name;
    private String phone;
    private String email;
    private Timestamp createdAt;
    private Timestamp deletedAt;
    private Timestamp updatedAt;
    private String status;
}
