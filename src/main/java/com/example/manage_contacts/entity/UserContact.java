package com.example.manage_contacts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "user_contact")
public class UserContact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", length = 36)
    private UUID id;

    @Column(name = "id_contact", length = 36)
    private UUID idContact;

    @Column(name = "id_user", length = 36)
    private UUID idUser;

}
