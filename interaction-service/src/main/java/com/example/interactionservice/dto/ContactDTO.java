package com.example.interactionservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDTO {
    private UUID id;
    private UUID idUser;
    private String name;
    private String phone;
    private String email;
    private Long interactionCount;
//    private Timestamp createdAt;
    private Timestamp deletedAt;
    private Timestamp updatedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private LocalDateTime createdAt;
    private String status;
}
