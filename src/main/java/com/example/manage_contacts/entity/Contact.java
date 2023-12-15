package com.example.manage_contacts.entity;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", length = 36)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "deleted_date")
    private Timestamp deletedDate;

    @Column(name = "updated_date")
    private Timestamp updateDate;

    @Column(name = "status")
    private String status;
}
