package com.example.interactionservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@Builder
@Table(name = "interactions")
@AllArgsConstructor
@NoArgsConstructor
public class Interaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", length = 36)
    private UUID id;

    @Column( name = "interaction_type")
    private String interactionType;

    @Column( name = "interaction_date")
    private Timestamp interactionDate;

    @Column(name = "id_contact")
    private UUID  contactId;

    @Column(name = "id_user")
    private UUID  userId;
}
