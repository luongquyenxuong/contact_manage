package com.example.manage_contacts.repository;

import com.example.manage_contacts.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    @Query(value = "SELECT c.* FROM contacts c " +
            "JOIN user_contact uc ON uc.id_contact = c.id " +
            "WHERE c.phone_number = :phoneNumber " +
            "AND uc.id_user = :idUser ",nativeQuery = true)
    Optional<Contact> findByPhoneNumberAndUserId(@Param("phoneNumber") String phoneNumber, @Param("idUser") UUID idUser);

    @Query(value = "SELECT c.* FROM contacts c " +
            "JOIN user_contact uc ON uc.id_contact = c.id " +
            "WHERE c.id = :id " +
            "AND uc.id_user = :idUser ",nativeQuery = true)
    Optional<Contact> findContactByIdAndUserId(@Param("id") UUID id, @Param("idUser") UUID idUser);

}
